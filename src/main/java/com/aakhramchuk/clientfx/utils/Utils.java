package com.aakhramchuk.clientfx.utils;

import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.objects.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    private static final Logger logger = LogManager.getLogger(Utils.class);

    public static String createMessage(Configuration config, String opcode, String message) {
        String prefix = config.getString("message.prefix");
        String length = String.format("%04d", message.length());

        return prefix + opcode + length + message;
    }

    public static DeserializedMessage confirmAndDeserializeErrorMessage(ConnectionObject connectionObject, String originalOpcode, String message, boolean isPing ) {
        Configuration config = connectionObject.getConfig();
        String prefix = config.getString("message.prefix");
        String startGameCommand = connectionObject.getConfig().getString("message.game_start_command");
        String takeGameCommand = connectionObject.getConfig().getString("message.game_take_command");
        String passGameCommand = connectionObject.getConfig().getString("message.game_pass_command");
        String turnGameCommand = connectionObject.getConfig().getString("message.game_turn_command");
        String endGameCommand = connectionObject.getConfig().getString("message.game_end_command");

        if (!message.startsWith(prefix)) {
            logger.error("Invalid message prefix");
        }

        String responseOpcode = message.substring(prefix.length(), prefix.length() + 2);
        if (!isPing) {
            if (responseOpcode.charAt(0) != '1' || responseOpcode.charAt(1) != originalOpcode.charAt(originalOpcode.length() - 1)) {
                logger.error("Invalid opcode in response");
            }
        }

        int declaredLength = Integer.parseInt(message.substring(prefix.length() + 2, prefix.length() + 6));
        String payload = message.substring(prefix.length() + 6);
        if (payload.length() != declaredLength) {
            logger.error("Invalid message length");
        }
        boolean isSuccess = true;
        boolean isGameMessage = true;
        String deserializedMessage;
        String messageType = "";

        if (payload.startsWith(startGameCommand)) {
            deserializedMessage = payload.substring(startGameCommand.length());
            messageType = startGameCommand;
        } else if (payload.startsWith(takeGameCommand)) {
            deserializedMessage = payload.substring(takeGameCommand.length());
            messageType = takeGameCommand;
        }
          else if (payload.startsWith(passGameCommand)) {
            deserializedMessage = payload.substring(passGameCommand.length());
            messageType = passGameCommand;
        } else if (payload.startsWith(turnGameCommand)) {
            deserializedMessage = payload.substring(turnGameCommand.length());
            messageType = turnGameCommand;
        } else if (payload.startsWith(endGameCommand)) {
            deserializedMessage = payload.substring(endGameCommand.length());
            messageType = endGameCommand;
        }
        else {
            isGameMessage = false;
            isSuccess = payload.charAt(0) == '1';

            String messageFromPayload = payload.substring(2);
            deserializedMessage = !isSuccess || "LOGOUT_SUCCESS".equals(messageFromPayload) ? config.getString(messageFromPayload.toLowerCase()) : messageFromPayload; // TODO: fix
        }

        DeserializedMessage deserializedMessageObject = new DeserializedMessage(isSuccess, deserializedMessage, originalOpcode, isGameMessage);

        if (isGameMessage) {
            deserializedMessageObject.setMessageType(messageType);
        }

        return deserializedMessageObject;
    }

    public static String deserializeLoginStateAndUpdateLobbiesList(Configuration config, DeserializedMessage deserializedMessage) {
        if (deserializedMessage.isSucess() && deserializedMessage.getOpcode() != null && deserializedMessage.getOpcode().equals(config.getString("message.login_opcode"))) {
            String prefixMenu = config.getString("message.menu_prefix");
            String prefixLobby = config.getString("message.lobby_prefix");
            String prefixNoLobbies = config.getString("message.no_lobbies_prefix");
            String prefixGame = config.getString("message.game_prefix");

            String message = deserializedMessage.getMessage().substring(deserializedMessage.getMessage().indexOf("];") + 2);
            if (message.startsWith(prefixMenu) || message.startsWith(prefixNoLobbies)) {
                if (message.startsWith(prefixMenu)) {
                    message = message.substring(prefixMenu.length());
                } else {
                    message = message.substring(prefixNoLobbies.length());
                }
                parseAndUpdateLobbies(message);
                return prefixMenu;
            } else if (message.startsWith(prefixLobby)) {
                String lobby = message.substring(prefixLobby.length());
                LobbyManager.setCurrentLobby(parseLobby(lobby, true, false));
                return prefixLobby;
            } else if (message.startsWith(prefixGame)) {
                String game = message.substring(prefixGame.length());
                LobbyManager.setCurrentLobby(parseLobby(game, true, true));
                return prefixGame;
            }
        }
        return null;
    }

    public static String deserializeStateAndUpdateLobbiesList(Configuration config, DeserializedMessage deserializedMessage) {
        if (deserializedMessage.isSucess()) {
            String prefixMenu = "MENU";
            String message = deserializedMessage.getMessage();
            if (message.startsWith(prefixMenu)) {
                message = message.substring(prefixMenu.length());
                parseAndUpdateLobbies(message);
                return prefixMenu;
            } else {
                LobbyManager.updateLobbies(new ArrayList<>());
            }
        }
        return null;
    }

    public static void parseAndUpdateLobbies(String lobbiesString) {
        List<String> lobbies = splitLobbies(lobbiesString);

        List<Lobby> lobbiesList = new ArrayList<>();
        for (String lobby : lobbies) {
            lobbiesList.add(parseLobby(lobby, false, false));
        }
        LobbyManager.updateLobbies(lobbiesList);
    }

    private static List<String> splitLobbies(String lobbiesString) {
        List<String> lobbies = new ArrayList<>();
        int depth = 0;
        int start = 0;

        for (int i = 0; i < lobbiesString.length(); i++) {
            char c = lobbiesString.charAt(i);

            if (c == '[') {
                if (depth == 1) {
                    start = i;
                }
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 1) {
                    lobbies.add(lobbiesString.substring(start, i + 1));
                }
            }
        }
        return lobbies;
    }

    public static DeserializedMessage sendMesageAndTakeResponse(String opcode, String sentMessage) throws InterruptedException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        logger.info("QUEUED: " + sentMessage);
        MainContainer.getOutgoingMessageQueue().put(sentMessage);

        MainContainer.setAwaitingResponse(true);
        String response = MainContainer.getIncomingMessageQueue().take();
        logger.info("RECEIVED: " + response);

        return Utils.confirmAndDeserializeErrorMessage(connectionObject, opcode, response, false);
    }

    static Lobby parseLobby(String lobby, boolean inLobby, boolean inGame) {
        String[] userParts = extractUsers(lobby, inLobby, inGame);
        User adminUser = parseUserInfo(userParts[0]);
        User creatorUser = parseUserInfo(userParts[1]);
        List<User> usersInLobby = new ArrayList<>();
        List<GamePlayer> gamePlayers = new ArrayList<>();
        String lobbyWithoutUsersAndGameInfo;
        GameObject gameObject = null;

        if (inLobby) {
            usersInLobby = parseUsersInLobby(userParts[2]);
            if (inGame) {
                gamePlayers = parseGamePlayers(userParts[3]);
                lobbyWithoutUsersAndGameInfo = lobby.replace(userParts[0], "").replace(userParts[1], "").replace(userParts[2], "").replace(userParts[3], "");
            } else {
                lobbyWithoutUsersAndGameInfo = lobby.replace(userParts[0], "").replace(userParts[1], "").replace(userParts[2], "");
            }
        } else {
            lobbyWithoutUsersAndGameInfo = lobby.replace(userParts[0], "").replace(userParts[1], "");
        }

        String[] lobbyDetails = lobbyWithoutUsersAndGameInfo.substring(1, lobbyWithoutUsersAndGameInfo.length() - 1).split(";");

        // Парсинг основной информации о лобби
        String lobbyId = lobbyDetails[0];
        String lobbyName = lobbyDetails[1];
        String maxPlayers = lobbyDetails[2];
        boolean hasPassword = lobbyDetails[3].equals("1");
        String currentPlayers = lobbyDetails[4];
        boolean gameStarted = lobbyDetails[7].equals("1");

        // Вывод информации о лобби
        if (inLobby) {
            if (!inGame) {
                /*System.out.println(lobbyId + ". " + lobbyName);
                System.out.println("max count of players: " + maxPlayers);
                System.out.println("has password: " + hasPassword);
                System.out.println("current count of players: " + currentPlayers);
                System.out.println("admin: " + adminInfo);
                System.out.println("creator: " + creatorInfo);
                System.out.println("users in lobby:");*/
/*                for (int i = 0; i < usersInLobby.size(); i++) {
                    System.out.println(i + 1 + ". " + usersInLobby.get(i));
                }*/
            } else {
                gameObject = new GameObject(gamePlayers);
                System.out.println(gameObject);
            }
        }

        Lobby newLobby = new Lobby(Integer.parseInt(lobbyId), lobbyName, Integer.parseInt(maxPlayers), hasPassword, Integer.parseInt(currentPlayers), adminUser != null ? adminUser.toString() : "none", creatorUser != null ? creatorUser.toString() : "none", gameStarted);

        usersInLobby.forEach(user ->{
            user.setAdmin(user.equals(adminUser));

            user.setCreator(user.equals(creatorUser));

            user.setOnline(false);

            newLobby.addUser(user);
        });

        if (gameObject != null) {
            newLobby.setGameObject(gameObject);
        }

        return newLobby;
    }

    public static List<GamePlayer> parseStartGamePlayers(String gamePlayersString) {
        List<GamePlayer> players = new ArrayList<>();

        // Разбор строки на отдельные игроков
        List<String> playerStrings = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < gamePlayersString.length(); i++) {
            char c = gamePlayersString.charAt(i);
            if (c == '[') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 0) {
                    playerStrings.add(gamePlayersString.substring(start, i + 1));
                }
            }
        }

        // Обработка остальных игроков
        for (String playerString : playerStrings) {
            GamePlayer player = parseGamePlayer(playerString);
            players.add(player);
        }

        return players;
    }

    public static List<GamePlayer> parseGamePlayers(String gamePlayersString) {
        List<GamePlayer> players = new ArrayList<>();
        if (gamePlayersString.equals("[]")) {
            return players;
        }

        String trimmedGamePlayersString = gamePlayersString.substring(1, gamePlayersString.length() - 1);

        // Разбор строки на отдельные игроков
        List<String> playerStrings = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < trimmedGamePlayersString.length(); i++) {
            char c = trimmedGamePlayersString.charAt(i);
            if (c == '[') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 0) {
                    playerStrings.add(trimmedGamePlayersString.substring(start, i + 1));
                }
            }
        }

        String currentPlayerString = playerStrings.remove(0);
        if (currentPlayerString.startsWith("[")) {
            currentPlayerString = currentPlayerString.substring(1, currentPlayerString.length() - 1);
        }

        String[] currentPlayerStringDetails = currentPlayerString.split(";");

        for (String playerString : playerStrings) {
            GamePlayer player = parseGamePlayer(playerString);
            if (player.getUsername().equals(currentPlayerStringDetails[0])) {
                player.setIsCurrentPlayer(true);
            }
            players.add(player);
        }

        return players;
    }




    private static GamePlayer parseGamePlayer(String playerInfo) {
        if (playerInfo.startsWith("[")) {
            playerInfo = playerInfo.substring(1, playerInfo.length() - 1);
        }

        List<String> details = new ArrayList<>();
        int depth = 0;
        StringBuilder currentSegment = new StringBuilder();

        for (char c : playerInfo.toCharArray()) {
            if (c == '[') {
                depth++;
                currentSegment.append(c);
            } else if (c == ']') {
                depth--;
                currentSegment.append(c);
            } else if (c == ';' && depth == 0) {
                details.add(currentSegment.toString());
                currentSegment = new StringBuilder();
            } else {
                currentSegment.append(c);
            }
        }
        if (currentSegment.length() > 0) {
            details.add(currentSegment.toString());
        }

        // Теперь у нас есть все детали, разделенные правильно.
        String login = details.get(0);
        String name = details.get(1);
        String surname = details.get(2);
        boolean cardsVisible = details.get(3).equals("1");
        List<String> cards = new ArrayList<>();
        int cardCount = 0;

        if (cardsVisible) {
            String cardDetailString = details.get(4);
            if (cardDetailString.startsWith("[")) {
                cardDetailString = cardDetailString.substring(1, cardDetailString.length() - 1);
            }
            if (!cardDetailString.isBlank()) {
                String[] cardDetails = cardDetailString.split(";");
                cards.addAll(Arrays.asList(cardDetails));
                cardCount = cards.size();
            }
        } else {
            cardCount = Integer.parseInt(details.get(4));
        }

        GamePlayer player = new GamePlayer(login, name, surname, cardsVisible, cards, cardCount);

        if (cardsVisible && !cards.isEmpty()) {
            player.setCardsValue(Integer.parseInt(details.get(5)));
        }

        return player;
    }

    private static List<User> parseUsersInLobby(String usersString) {
        List<User> users = new ArrayList<>();
        if (usersString.equals("[]")) {
            return users;
        }

        String trimmedUsersString = usersString.substring(1, usersString.length() - 1);

        int depth = 0;
        int start = 0;
        for (int i = 0; i < trimmedUsersString.length(); i++) {
            if (trimmedUsersString.charAt(i) == '[') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (trimmedUsersString.charAt(i) == ']') {
                depth--;
                if (depth == 0) {
                    User userToAdd = parseUserInfo(trimmedUsersString.substring(start, i + 1));
                    if (userToAdd != null) {
                        users.add(userToAdd);
                    }
                }
            }
        }
        return users;
    }

    private static String[] extractUsers(String lobby, boolean inLobby, boolean inGame) {
        int firstBracket = lobby.indexOf('[');
        int secondBracket = lobby.indexOf('[', firstBracket + 1);

        int adminEnd = findClosingBracket(lobby, secondBracket);
        int creatorEnd = findClosingBracket(lobby, adminEnd + 2);
        int usersEnd = inLobby ? findClosingBracket(lobby, creatorEnd + 2) : creatorEnd;

        String adminInfo = lobby.substring(secondBracket, adminEnd + 1);
        String creatorInfo = lobby.substring(adminEnd + 2, creatorEnd + 1);
        String usersInfo = inLobby ? lobby.substring(creatorEnd + 2, usersEnd + 1) : "[]";
        String gameInfo = inGame ? lobby.substring(usersEnd + 4) : "[]"; // Добавляем обработку игровой информации

        return new String[]{adminInfo, creatorInfo, usersInfo, gameInfo};
    }

    private static int findClosingBracket(String str, int start) {
        int depth = 1;
        for (int i = start + 1; i < str.length(); i++) {
            if (str.charAt(i) == '[') {
                depth++;
            } else if (str.charAt(i) == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static User parseUserInfo(String userInfo) {
        if (userInfo.equals("[]")) {
            return null;
        }
        String[] userDetails = userInfo.substring(1, userInfo.length() - 1).split(";");
        return new User(userDetails[0], userDetails[1], userDetails[2]);
    }

    public static void handleServerMessage(String message, String opcodeString) {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String opcode = connectionObject.getConfig().getString(opcodeString);
        String turnGameCommand = connectionObject.getConfig().getString("message.game_turn_command");
        String startGameCommand = connectionObject.getConfig().getString("message.game_start_command");
        String takeGameCommand = connectionObject.getConfig().getString("message.game_take_command");
        String passGameCommand = connectionObject.getConfig().getString("message.game_pass_command");
        String endGameCommand = connectionObject.getConfig().getString("message.game_end_command");

        DeserializedMessage deserializedReceivedMessage = Utils.confirmAndDeserializeErrorMessage(connectionObject, opcode, message, true);
        if (deserializedReceivedMessage.isSucess()) {
            if (MainContainer.isInSelectLobbyMenu()) {
                Utils.parseAndUpdateLobbies(deserializedReceivedMessage.getMessage());
            } else if (MainContainer.isInLobbyMenu()) {
                String messageToParse = deserializedReceivedMessage.getMessage().substring(Constants.LOBBY_PREFIX_VALE.length());
                LobbyManager.updateCurrentLobby(Utils.parseLobby(messageToParse, true, false));
            } else if (deserializedReceivedMessage.isGameMessage()) {
                    if (LobbyManager.getCurrentLobby() != null) {
                        if (deserializedReceivedMessage.getMessageType().equals(turnGameCommand)) {
                            if (LobbyManager.getCurrentLobby() != null && LobbyManager.getCurrentLobby().getGameObject() != null) {
                                LobbyManager.getCurrentLobby().getGameObject().getPlayers().forEach(player -> {
                                    if (player.isCurrentPlayer()) {
                                        player.setIsCurrentPlayer(false);
                                    }
                                    if (player.getUsername().equals(deserializedReceivedMessage.getMessage().substring(2))) {
                                        player.setIsCurrentPlayer(true);
                                    }
                                });
                            }
                            MainContainer.setInGame(true);
                        } else if (deserializedReceivedMessage.getMessageType().equals(startGameCommand)
                                || deserializedReceivedMessage.getMessageType().equals(takeGameCommand)
                                || deserializedReceivedMessage.getMessageType().equals(passGameCommand)) {
                            if (LobbyManager.getCurrentLobby().getGameObject() == null) {
                                LobbyManager.getCurrentLobby().setGameObject(new GameObject(Utils.parseStartGamePlayers(message)));
                            } else {
                                LobbyManager.getCurrentLobby().getGameObject().setPlayers(Utils.parseStartGamePlayers(deserializedReceivedMessage.getMessage().substring(1)));
                            }
                        } else if (deserializedReceivedMessage.getMessageType().equals(endGameCommand)) {
                            GameUtils.endGame(deserializedReceivedMessage.getMessage());

                        }
                    }
                }
            }
        }

}

package com.aakhramchuk.clientfx.utils;

import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.aakhramchuk.clientfx.objects.Constants.MENU_PREFIX_VALUE;

public class Utils {

    private static final Logger logger = LogManager.getLogger(Utils.class);

    /**
     * Creates a formatted message string based on a configuration, opcode, and message content.
     *
     * @param config The configuration object containing message formatting settings.
     * @param opcode The operation code to be included in the message.
     * @param message The actual message content.
     * @return A formatted message string.
     */
    public static String createMessage(Configuration config, String opcode, String message) {
        String prefix = config.getString("message.prefix");
        String length = String.format("%04d", message.length());

        return prefix + opcode + length + message;
    }

    /**
     * Confirms the validity of a message based on its prefix and length.
     *
     * @param connectionObject The connection object containing configuration details.
     * @param message The message to be validated.
     * @return true if the message is valid, false otherwise.
     */
    public static boolean confirmMessage(ConnectionObject connectionObject, String message) {
        Configuration config = connectionObject.getConfig();
        String prefix = config.getString("message.prefix");

        if (!message.startsWith(prefix)) {
            logger.error("Invalid message prefix");
        }

        int declaredLength = Integer.parseInt(message.substring(prefix.length() + 2, prefix.length() + 6));
        String payload = message.substring(prefix.length() + 6);
        if (payload.length() != declaredLength) {
            logger.error("Invalid message length");
        }

        return true;
    }

    /**
     * Confirms, validates, and deserializes a message received from the server.
     * This method includes special handling for ping messages and different types of game commands.
     *
     * @param connectionObject The connection object for configuration and server communication.
     * @param originalOpcode The original opcode sent in the request for which this is the response.
     * @param message The message received from the server.
     * @param isPing A flag indicating if the message is a ping message.
     * @return A DeserializedMessage object representing the received message.
     */
    public static DeserializedMessage confirmAndDeserializeErrorMessage(ConnectionObject connectionObject, String originalOpcode, String message, boolean isPing ) {
        Configuration config = connectionObject.getConfig();
        String prefix = config.getString("message.prefix");
        String startGameCommand = connectionObject.getConfig().getString("message.game_start_command");
        String takeGameCommand = connectionObject.getConfig().getString("message.game_take_command");
        String passGameCommand = connectionObject.getConfig().getString("message.game_pass_command");
        String turnGameCommand = connectionObject.getConfig().getString("message.game_turn_command");
        String endGameCommand = connectionObject.getConfig().getString("message.game_end_command");
        String gameLeaveCommand = connectionObject.getConfig().getString("message.game_leave_command");
        String disconnectCommand = connectionObject.getConfig().getString("message.disconnect_command");
        String disconnectEndCommand = connectionObject.getConfig().getString("message.disconnect_end_command");

        if (!message.startsWith(prefix)) {
            logger.error("Invalid message prefix");
            // TODO: invalid message prefix validate!!!
        }

        String responseOpcode = message.substring(prefix.length(), prefix.length() + 2);
        if (!isPing) {
            if (responseOpcode.charAt(0) != '1' || responseOpcode.charAt(1) != originalOpcode.charAt(originalOpcode.length() - 1)) {
                logger.error("Invalid opcode in response");
             } // TODO: opcode not valid validate!!!
        }

        int declaredLength = Integer.parseInt(message.substring(prefix.length() + 2, prefix.length() + 6));
        String payload = message.substring(prefix.length() + 6);
        if (payload.length() != declaredLength) {
            logger.error("Invalid message length");
            // TODO: invalid message length validate!!!
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
        } else if (payload.startsWith(passGameCommand)) {
            deserializedMessage = payload.substring(passGameCommand.length());
            messageType = passGameCommand;
        } else if (payload.startsWith(turnGameCommand)) {
            deserializedMessage = payload.substring(turnGameCommand.length());
            messageType = turnGameCommand;
        } else if (payload.startsWith(endGameCommand)) {
            deserializedMessage = payload.substring(endGameCommand.length());
            messageType = endGameCommand;
        } else if (payload.startsWith(disconnectCommand)) {
            deserializedMessage = payload.substring(disconnectCommand.length());
            messageType = disconnectCommand;
        } else if (payload.startsWith(gameLeaveCommand)) {
            deserializedMessage = payload.substring(gameLeaveCommand.length());
            messageType = gameLeaveCommand;
        } else if (payload.startsWith(disconnectEndCommand)) {
            deserializedMessage = payload.substring(disconnectEndCommand.length());
            messageType = disconnectEndCommand;
        } else {
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

    /**
     * Deserializes the login state and updates the lobbies list based on the received message.
     *
     * @param config The configuration object.
     * @param deserializedMessage The deserialized message object containing the server response.
     * @return A string indicating the type of message (e.g., menu, lobby, game) or null if not applicable.
     */
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

    /**
     * Deserializes the state and updates the lobbies list.
     *
     * @param config The configuration object.
     * @param deserializedMessage The deserialized message object containing the server response.
     */
    public static void deserializeStateAndUpdateLobbiesList(Configuration config, DeserializedMessage deserializedMessage) {
        if (deserializedMessage.isSucess()) {
            String message = deserializedMessage.getMessage();
            if (message.startsWith(MENU_PREFIX_VALUE)) {
                message = message.substring(MENU_PREFIX_VALUE.length());
                parseAndUpdateLobbies(message);
            } else {
                LobbyManager.updateLobbies(new ArrayList<>());
            }
        }
    }

    /**
     * Parses a string representation of lobbies and updates the lobby list.
     *
     * @param lobbiesString The string representation of lobbies.
     */
    public static void parseAndUpdateLobbies(String lobbiesString) {
        List<String> lobbies = splitLobbies(lobbiesString);

        List<Lobby> lobbiesList = new ArrayList<>();
        for (String lobby : lobbies) {
            lobbiesList.add(parseLobby(lobby, false, false));
        }
        LobbyManager.updateLobbies(lobbiesList);
    }

    /**
     * Splits a string representation of lobbies into individual lobby strings.
     *
     * @param lobbiesString The string representation of multiple lobbies.
     * @return A list of strings, each representing a single lobby.
     */
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

    /**
     * Sends a message to the server and waits for a response.
     *
     * @param opcode The opcode of the message being sent.
     * @param sentMessage The message being sent to the server.
     * @return A DeserializedMessage object representing the server's response.
     * @throws InterruptedException If the thread is interrupted while waiting for the response.
     */
    public static DeserializedMessage sendMesageAndTakeResponse(String opcode, String sentMessage) throws InterruptedException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        logger.info("QUEUED: " + sentMessage);
        MainContainer.getOutgoingMessageQueue().put(sentMessage);

        MainContainer.setAwaitingResponse(true);
        String response = MainContainer.getIncomingMessageQueue().take();
        logger.info("RECEIVED: " + response);

        return Utils.confirmAndDeserializeErrorMessage(connectionObject, opcode, response, false);
    }

    /**
     * Parses a lobby string to create a Lobby object.
     *
     * @param lobby The string representation of the lobby.
     * @param inLobby A flag indicating if the lobby is currently active.
     * @param inGame A flag indicating if a game is currently active in the lobby.
     * @return A new Lobby object constructed from the parsed string.
     */
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

        String lobbyId = lobbyDetails[0];
        String lobbyName = lobbyDetails[1];
        String maxPlayers = lobbyDetails[2];
        boolean hasPassword = lobbyDetails[3].equals("1");
        String currentPlayers = lobbyDetails[4];
        boolean gameStarted = lobbyDetails[7].equals("1");

        if (inLobby && inGame) {
            gameObject = new GameObject(gamePlayers);
        }

        Lobby newLobby = new Lobby(Integer.parseInt(lobbyId), lobbyName, Integer.parseInt(maxPlayers), hasPassword, Integer.parseInt(currentPlayers), adminUser != null ? adminUser.toString() : "none", creatorUser != null ? creatorUser.toString() : "none", gameStarted);

        usersInLobby.forEach(user -> {
            user.setAdmin(user.equals(adminUser));

            user.setCreator(user.equals(creatorUser));

            newLobby.addUser(user);
        });

        if (gameObject != null) {
            newLobby.setGameObject(gameObject);
        }

        return newLobby;
    }

    /**
     * Parses a string representation of game players at the start of a game.
     *
     * @param gamePlayersString The string representation of game players.
     * @return A list of GamePlayer objects representing the players at the start of the game.
     */
    public static List<GamePlayer> parseStartGamePlayers(String gamePlayersString) {
        List<GamePlayer> players = new ArrayList<>();

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

        for (String playerString : playerStrings) {
            GamePlayer player = parseGamePlayer(playerString);
            players.add(player);
        }

        return players;
    }

    /**
     * Parses a string representation of game players during a game.
     *
     * @param gamePlayersString The string representation of game players during a game.
     * @return A list of GamePlayer objects representing the players during the game.
     */
    public static List<GamePlayer> parseGamePlayers(String gamePlayersString) {
        List<GamePlayer> players = new ArrayList<>();
        if (gamePlayersString.equals("[]")) {
            return players;
        }

        String trimmedGamePlayersString = gamePlayersString.substring(1, gamePlayersString.length() - 1);

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


    /**
     * Parses information about a single game player.
     *
     * @param playerInfo The string representation of a single game player's information.
     * @return A GamePlayer object created from the parsed information.
     */
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

        String login = details.get(0);
        String name = details.get(1);
        String surname = details.get(2);
        String isOnline = details.get(3);
        boolean cardsVisible = details.get(4).equals("1");
        List<String> cards = new ArrayList<>();
        int cardCount = 0;

        if (cardsVisible) {
            String cardDetailString = details.get(5);
            if (cardDetailString.startsWith("[")) {
                cardDetailString = cardDetailString.substring(1, cardDetailString.length() - 1);
            }
            if (!cardDetailString.isBlank()) {
                String[] cardDetails = cardDetailString.split(";");
                cards.addAll(Arrays.asList(cardDetails));
                cardCount = cards.size();
            }
        } else {
            cardCount = Integer.parseInt(details.get(5));
        }

        GamePlayer player = new GamePlayer(login, name, surname, cardsVisible, cards, cardCount, "1".equals(isOnline));

        if (cardsVisible && !cards.isEmpty()) {
            player.setCardsValue(Integer.parseInt(details.get(6)));
        }

        return player;
    }

    /**
     * Parses users in a lobby from a string representation.
     *
     * @param usersString The string representation of users in a lobby.
     * @return A list of User objects representing the users in the lobby.
     */
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

    /**
     * Extracts user information from a lobby string.
     *
     * @param lobby The string representation of the lobby.
     * @param inLobby A flag indicating if the lobby is currently active.
     * @param inGame A flag indicating if a game is currently active in the lobby.
     * @return An array of strings, each containing user information.
     */
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

    /**
     * Finds the index of the closing bracket that matches the opening bracket at the given start index.
     *
     * @param str The string in which to find the closing bracket.
     * @param start The index of the opening bracket.
     * @return The index of the corresponding closing bracket.
     */
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

    /**
     * Parses user information from a string representation.
     *
     * @param userInfo The string representation of a user's information.
     * @return A User object created from the parsed information.
     */
    public static User parseUserInfo(String userInfo) {
        if (userInfo.equals("[]")) {
            return null;
        }
        String[] userDetails = userInfo.substring(1, userInfo.length() - 1).split(";");
        return new User(userDetails[0], userDetails[1], userDetails[2], "1".equals(userDetails[3]));
    }

    /**
     * Handles messages received from the server by parsing and responding appropriately based on the opcode.
     * This method orchestrates different actions depending on the message type (e.g., updating lobbies, handling game state changes).
     *
     * @param message The message received from the server to be handled.
     * @param opcodeString The opcode associated with the message for identifying the type of message.
     * @throws IOException If an IO error occurs during the handling of the message.
     */
    public static void handleServerMessage(String message, String opcodeString) throws IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String opcode = connectionObject.getConfig().getString(opcodeString);
        String turnGameCommand = connectionObject.getConfig().getString("message.game_turn_command");
        String startGameCommand = connectionObject.getConfig().getString("message.game_start_command");
        String takeGameCommand = connectionObject.getConfig().getString("message.game_take_command");
        String passGameCommand = connectionObject.getConfig().getString("message.game_pass_command");
        String endGameCommand = connectionObject.getConfig().getString("message.game_end_command");
        String gameLeaveCommand = connectionObject.getConfig().getString("message.game_leave_command");
        String disconnectCommand = connectionObject.getConfig().getString("message.disconnect_command");
        String disconnectEndCommand = connectionObject.getConfig().getString("message.disconnect_end_command");
        String gamePrefix = connectionObject.getConfig().getString("message.game_prefix");

        DeserializedMessage deserializedReceivedMessage = Utils.confirmAndDeserializeErrorMessage(connectionObject, opcode, message, true);
        if (deserializedReceivedMessage.isSucess()) {
            if (MainContainer.isInSelectLobbyMenu()) {
                Utils.parseAndUpdateLobbies(deserializedReceivedMessage.getMessage());
            } else if (MainContainer.isInLobbyMenu() && deserializedReceivedMessage.getMessage().startsWith(Constants.LOBBY_PREFIX_VALE)) {
                String messageToParse = deserializedReceivedMessage.getMessage().substring(Constants.LOBBY_PREFIX_VALE.length());
                if (Platform.isFxApplicationThread()) {
                    LobbyManager.updateCurrentLobby(Utils.parseLobby(messageToParse, true, false));
                } else {
                    Platform.runLater(() -> LobbyManager.updateCurrentLobby(Utils.parseLobby(messageToParse, true, false)));
                }
            } else if (deserializedReceivedMessage.isGameMessage()) {
                    if (MainContainer.isInGameEndMenu()) {
                        MainContainer.setInGameEndMenu(false);
                        dropToMainMenu();
                    }
                    if (LobbyManager.getCurrentLobby() != null) {
                        if (deserializedReceivedMessage.getMessageType().equals(turnGameCommand)) {
                            if (LobbyManager.getCurrentLobby() != null && LobbyManager.getCurrentLobby().getGameObject() != null) {
                                LobbyManager.getCurrentLobby().getGameObject().getPlayers().forEach(player -> {
                                    if (player.isCurrentPlayer()) {
                                        player.setIsCurrentPlayer(false);
                                    }
                                    if (player.getUsername().equals(deserializedReceivedMessage.getMessage().substring(2))) {
                                        player.setIsCurrentPlayer(true);
                                        if (player.getUsername().equals(MainContainer.getUser().getUsername())) {
                                            if (Platform.isFxApplicationThread()) {
                                                FxUtils.showTurnAlert();
                                            } else {
                                                Platform.runLater(FxUtils::showTurnAlert);
                                            }
                                        }
                                    }
                                });
                            }
                        } else if (deserializedReceivedMessage.getMessageType().equals(disconnectCommand)) {
                            if (Platform.isFxApplicationThread()) {
                                User disconnectedUser = Utils.parseUserInfo(deserializedReceivedMessage.getMessage().substring(1));
                                if (disconnectedUser != null) {
                                    LobbyManager.markUserAsDisconnect(disconnectedUser);
                                    FxUtils.showDisconnectAlert(disconnectedUser);
                                }
                            } else {
                                Platform.runLater(() -> {
                                    User disconnectedUser = Utils.parseUserInfo(deserializedReceivedMessage.getMessage().substring(1));
                                    if (disconnectedUser != null) {
                                        LobbyManager.markUserAsDisconnect(disconnectedUser);
                                        FxUtils.showDisconnectAlert(disconnectedUser);
                                    }
                                });
                            }
                        } else if (deserializedReceivedMessage.getMessageType().equals(gameLeaveCommand) || deserializedReceivedMessage.getMessageType().equals(disconnectEndCommand)) {
                            User disconnectedUser = Utils.parseUserInfo(deserializedReceivedMessage.getMessage().substring(1));
                            if (deserializedReceivedMessage.getMessageType().equals(disconnectEndCommand) && disconnectedUser != null) {
                                if (Platform.isFxApplicationThread()) {
                                    FxUtils.showDisconnectEndAlert(disconnectedUser);
                                } else {
                                    Platform.runLater(() -> FxUtils.showDisconnectEndAlert(disconnectedUser));
                                }
                            } else if (disconnectedUser != null) {
                                if (Platform.isFxApplicationThread()) {
                                    FxUtils.showGameLeaveAlert(disconnectedUser);
                                } else {
                                    Platform.runLater(() -> FxUtils.showGameLeaveAlert(disconnectedUser));
                                }
                            }
                            MainContainer.setInGame(false);
                            dropToMainMenu();
                        }
                        else if (deserializedReceivedMessage.getMessageType().equals(startGameCommand)
                                || deserializedReceivedMessage.getMessageType().equals(takeGameCommand)
                                || deserializedReceivedMessage.getMessageType().equals(passGameCommand)) {
                            if (LobbyManager.getCurrentLobby().getGameObject() == null) {
                                LobbyManager.getCurrentLobby().setGameObject(new GameObject(Utils.parseStartGamePlayers(message)));
                                if (Platform.isFxApplicationThread()) {
                                    FxManager.changeCurrentSceneToGameScene();
                                } else {
                                    Platform.runLater(() -> {
                                        try {
                                            FxManager.changeCurrentSceneToGameScene();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            } else {
                                if (LobbyManager.getCurrentLobby().getGameObject().getPlayers() == null || LobbyManager.getCurrentLobby().getGameObject().getPlayers().isEmpty()) {
                                    if (Platform.isFxApplicationThread()) {
                                        LobbyManager.getCurrentLobby().getGameObject().setPlayers(Utils.parseStartGamePlayers(deserializedReceivedMessage.getMessage().substring(1)));
                                    } else {
                                        Platform.runLater(() -> {
                                            LobbyManager.getCurrentLobby().getGameObject().setPlayers(Utils.parseStartGamePlayers(deserializedReceivedMessage.getMessage().substring(1)));
                                        });
                                    }
                                } else {
                                    if (Platform.isFxApplicationThread()) {
                                        LobbyManager.getCurrentLobby().getGameObject().updatePlayers(Utils.parseStartGamePlayers(deserializedReceivedMessage.getMessage().substring(1)), true);
                                    } else {
                                        Platform.runLater(() -> {
                                            LobbyManager.getCurrentLobby().getGameObject().updatePlayers(Utils.parseStartGamePlayers(deserializedReceivedMessage.getMessage().substring(1)), true);
                                        });
                                    }
                                }
                            }
                        } else if (deserializedReceivedMessage.getMessageType().equals(endGameCommand)) {
                            GameUtils.endGame(deserializedReceivedMessage.getMessage());
                        }
                    }
                } else if (deserializedReceivedMessage.getMessage().startsWith(gamePrefix)) {
                    String messageToParse = deserializedReceivedMessage.getMessage().substring(gamePrefix.length());
                    if (Platform.isFxApplicationThread()) {
                        LobbyManager.updateCurrentLobbyWithoutUpdateApplicationUser(Utils.parseLobby(messageToParse, true, true));
                    } else {
                        Platform.runLater(() -> LobbyManager.updateCurrentLobbyWithoutUpdateApplicationUser(Utils.parseLobby(messageToParse, true, true)));
                    }
                }
            }
        }

    /**
     * Transitions the user interface back to the main lobby menu.
     * This method is typically called when exiting a game or lobby, ensuring the user is returned to the main menu.
     * It handles the transition both on the JavaFX application thread and outside of it.
     *
     * @throws IOException If an error occurs while changing scenes in the JavaFX application.
     */
    private static void dropToMainMenu() throws IOException {
        MainContainer.setInLobbyMenu(true);
        LobbyManager.getCurrentLobby().setGameObject(null);
        if (Platform.isFxApplicationThread()) {
            FxManager.changeCurrentSceneToLobbyScene();
        } else {
            Platform.runLater(() -> {
                try {
                    FxManager.changeCurrentSceneToLobbyScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Retrieves the image path for a specific card based on its code.
     * This method maps card codes to their corresponding image file paths, supporting different card suits.
     *
     * @param cardCode The code of the card (e.g., "C2" for Two of Clubs).
     * @return The file path to the image representing the specified card or null if the suit is unrecognized.
     */
    public static String getCardImagePath(String cardCode) {
        char suit = cardCode.charAt(0);

        switch (suit) {
            case 'C': return "/Images/clubs/" + cardCode + ".png";
            case 'D': return "/Images/diamonds/" + cardCode + ".png";
            case 'H': return "/Images/hearts/" + cardCode + ".png";
            case 'S': return "/Images/spades/" + cardCode + ".png";
            default: return null;
        }
    }
}

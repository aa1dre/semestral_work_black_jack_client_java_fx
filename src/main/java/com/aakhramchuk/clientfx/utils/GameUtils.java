package com.aakhramchuk.clientfx.utils;

import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameUtils {
    private static final Logger logger = LogManager.getLogger(GameUtils.class);

    public static void startGame() throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String gameActionOpcode = connectionObject.getConfig().getString("message.game_action_opcode");
        String startGameCommand = connectionObject.getConfig().getString("message.game_start_command");
        Configuration config = connectionObject.getConfig();

        evaluateGameAction(gameActionOpcode, startGameCommand, config);
    }

    public static void passAction() throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String gameActionOpcode = connectionObject.getConfig().getString("message.game_action_opcode");
        String startGameCommand = connectionObject.getConfig().getString("message.game_pass_command");
        Configuration config = connectionObject.getConfig();

        evaluateGameAction(gameActionOpcode, startGameCommand, config);
    }

    public static void takeAction() throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String gameActionOpcode = connectionObject.getConfig().getString("message.game_action_opcode");
        String startGameCommand = connectionObject.getConfig().getString("message.game_take_command");
        Configuration config = connectionObject.getConfig();

        evaluateGameAction(gameActionOpcode, startGameCommand, config);
    }

    private static void evaluateGameAction(String gameActionOpcode, String startGameCommand, Configuration config) throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        boolean startedCommand = false;

        String endGameCommand = connectionObject.getConfig().getString("message.game_end_command");
        String sentMessage = Utils.createMessage(config, gameActionOpcode, startGameCommand);
        connectionObject.getWriter().print(sentMessage);
        connectionObject.getWriter().flush();
        MainContainer.setAwaitingResponse(true);
        MainContainer.setOurTurnEvaluated(false);
        String response = MainContainer.getIncomingMessageQueue().take();

        logger.info("SENT: " + sentMessage);

        logger.info("RECEIVED: " + response);

        String messageToDeserialize;
        if (startedCommand = response.startsWith(startGameCommand)) {
            messageToDeserialize = response.substring(startGameCommand.length() + 1);
        } else {
            messageToDeserialize = response;
        }
        DeserializedMessage deserializedReceivedMessage = Utils.confirmAndDeserializeErrorMessage(connectionObject, gameActionOpcode, messageToDeserialize, false);
        if (deserializedReceivedMessage.isSucess()) {
            if (deserializedReceivedMessage.getMessageType().equals(startGameCommand)) {
                String message = deserializedReceivedMessage.getMessage().substring(1);
                message = message.substring(message.indexOf(';') + 1);
                if (LobbyManager.getCurrentLobby().getGameObject() == null) {
                    LobbyManager.getCurrentLobby().setGameObject(new GameObject(Utils.parseStartGamePlayers(message)));
                } else {
                    LobbyManager.getCurrentLobby().getGameObject().setPlayers(Utils.parseStartGamePlayers(deserializedReceivedMessage.getMessage().substring(1)));
                }
                FxManager.changeCurrentSceneToGameScene();
                MainContainer.setOurTurnEvaluated(true);
                while (!MainContainer.getGameQueue().isEmpty()) {
                    Utils.handleServerMessage(MainContainer.getGameQueue().take(), "message.game_action_opcode");
                }
            } else if (deserializedReceivedMessage.getMessageType().equals(endGameCommand)) {
                endGame(deserializedReceivedMessage.getMessage());
            }
        } else {
            if (startedCommand) {
                MainContainer.setInGame(false);
            }
            MainContainer.setOurTurnEvaluated(true);
            System.out.println(deserializedReceivedMessage.getMessage());
        }
    }

    public static void endGame(String message) {
        if (message !=null) {
            String messageToEvaluate = message.substring(1);
            messageToEvaluate = messageToEvaluate.substring(messageToEvaluate.indexOf(';') + 1);
            int lastClosingBracketIndex = messageToEvaluate.lastIndexOf(']');
            int secondLastClosingBracketIndex = messageToEvaluate.lastIndexOf(']', lastClosingBracketIndex - 1);
            String messageToEvaluateWinner = messageToEvaluate.substring(secondLastClosingBracketIndex + 2);
            messageToEvaluate = messageToEvaluate.substring(0, secondLastClosingBracketIndex + 1);
            messageToEvaluateWinner = messageToEvaluateWinner.substring(messageToEvaluateWinner.indexOf(';') + 1);
            messageToEvaluateWinner = messageToEvaluateWinner.substring(1, messageToEvaluateWinner.length() - 1);
            String[] winnerList = messageToEvaluateWinner.split(";");

            List<GamePlayer> players = Utils.parseStartGamePlayers(messageToEvaluate);
            List<GamePlayer> winersList = new ArrayList<>();
            for (GamePlayer player : players) {
                for (String winner : winnerList) {
                    if (player.getUsername().equals(winner)) {
                        winersList.add(player);
                    }
                }
            }

            LobbyManager.getCurrentLobby().setGameObject(null);
            MainContainer.setInGame(false);
            MainContainer.setInLobbyMenu(true);
        }
    }

}

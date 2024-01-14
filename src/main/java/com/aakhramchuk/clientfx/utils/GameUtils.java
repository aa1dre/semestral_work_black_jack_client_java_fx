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
import java.util.List;

public class GameUtils {
    private static final Logger logger = LogManager.getLogger(GameUtils.class);

    /**
     * Starts a game, sends the appropriate command to the server, and evaluates the response.
     *
     * @throws InterruptedException If the thread is interrupted during execution.
     * @throws IOException          If an I/O error occurs while communicating with the server.
     */
    public static void startGame() throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String gameActionOpcode = connectionObject.getConfig().getString("message.game_action_opcode");
        String startGameCommand = connectionObject.getConfig().getString("message.game_start_command");
        Configuration config = connectionObject.getConfig();

        evaluateGameAction(gameActionOpcode, startGameCommand, config);
        MainContainer.setOurTurnEvaluated(true);
    }


    /**
     * Passes the turn in the current game, sends the corresponding command to the server, and evaluates the response.
     *
     * @throws InterruptedException If the thread is interrupted during execution.
     * @throws IOException          If an I/O error occurs while communicating with the server.
     */
    public static void passAction() throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String gameActionOpcode = connectionObject.getConfig().getString("message.game_action_opcode");
        String startGameCommand = connectionObject.getConfig().getString("message.game_pass_command");
        Configuration config = connectionObject.getConfig();

        evaluateGameAction(gameActionOpcode, startGameCommand, config);
        MainContainer.setOurTurnEvaluated(true);
    }

    /**
     * Takes the turn in the current game, sends the corresponding command to the server, and evaluates the response.
     *
     * @throws InterruptedException If the thread is interrupted during execution.
     * @throws IOException          If an I/O error occurs while communicating with the server.
     */
    public static void takeAction() throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String gameActionOpcode = connectionObject.getConfig().getString("message.game_action_opcode");
        String startGameCommand = connectionObject.getConfig().getString("message.game_take_command");
        Configuration config = connectionObject.getConfig();

        evaluateGameAction(gameActionOpcode, startGameCommand, config);
        MainContainer.setOurTurnEvaluated(true);
    }

    /**
     * Private helper method to evaluate a game action command.
     *
     * @param gameActionOpcode The opcode for game actions.
     * @param command           The specific game action command to be evaluated.
     * @param config            The configuration object containing game-related settings.
     * @throws InterruptedException If the thread is interrupted during execution.
     * @throws IOException          If an I/O error occurs while communicating with the server.
     */
    private static void evaluateGameAction(String gameActionOpcode, String command, Configuration config) throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String passCommand = connectionObject.getConfig().getString("message.game_pass_command");
        String takeCommand = connectionObject.getConfig().getString("message.game_take_command");
        String startCommand = connectionObject.getConfig().getString("message.game_start_command");
        String endGameCommand = connectionObject.getConfig().getString("message.game_end_command");

        MainContainer.setInGame(true);
        MainContainer.setOurTurnEvaluated(false);

        // Create the message to be sent to the server
        String sentMessage = Utils.createMessage(config, gameActionOpcode, command);
        logger.info("QUEUED: " + sentMessage);
        MainContainer.getOutgoingMessageQueue().put(sentMessage);

        MainContainer.setAwaitingResponse(true);
        String response = MainContainer.getIncomingMessageQueue().take();

        logger.info("SENT: " + sentMessage);

        logger.info("RECEIVED: " + response);

        String messageToDeserialize;

        // Check if the response starts with the same command
        if (response.startsWith(command)) {
            messageToDeserialize = response.substring(command.length() + 1);
        } else {
            messageToDeserialize = response;
        }

        // Deserialize the received message
        DeserializedMessage deserializedReceivedMessage = Utils.confirmAndDeserializeErrorMessage(connectionObject, gameActionOpcode, messageToDeserialize, false);
        if (deserializedReceivedMessage.isSucess()) {
            if (deserializedReceivedMessage.getMessageType().equals(command)) {
                // Process game start command response
                String message = deserializedReceivedMessage.getMessage().substring(1);
                message = message.substring(message.indexOf(';') + 1);
                if (LobbyManager.getCurrentLobby().getGameObject() == null) {
                    LobbyManager.getCurrentLobby().setGameObject(new GameObject(Utils.parseStartGamePlayers(message)));
                } else {
                    LobbyManager.getCurrentLobby().getGameObject().setPlayers(Utils.parseStartGamePlayers(deserializedReceivedMessage.getMessage().substring(1)));
                }
                FxManager.changeCurrentSceneToGameScene();
                // Handle any pending game actions
                while (!MainContainer.getGameQueue().isEmpty()) {
                    Utils.handleServerMessage(MainContainer.getGameQueue().take(), "message.game_action_opcode");
                }
            } else if (deserializedReceivedMessage.getMessageType().equals(endGameCommand)) {
                // Handle end game scenario
                endGame(deserializedReceivedMessage.getMessage());
            }
        } else {
            MainContainer.setOurTurnEvaluated(true);
            if (passCommand.equals(command)) {
                Alert dataAlert = FxUtils.createWarningAlert(config.getString("text.alert_title.warning"),
                        connectionObject.getConfig().getString("text.alert_header_text.error_in_game_pass_process"),
                        deserializedReceivedMessage.getMessage());
                dataAlert.showAndWait();
            } else if (takeCommand.equals(command)) {
                Alert dataAlert = FxUtils.createWarningAlert(config.getString("text.alert_title.warning"),
                        connectionObject.getConfig().getString("text.alert_header_text.error_in_game_take_process"),
                        deserializedReceivedMessage.getMessage());
                dataAlert.showAndWait();
            } else if (startCommand.equals(command)) {
                MainContainer.setInGame(false);
                Alert dataAlert = FxUtils.createWarningAlert(config.getString("text.alert_title.warning"),
                        connectionObject.getConfig().getString("text.alert_header_text.error_in_game_start_process"),
                        deserializedReceivedMessage.getMessage());
                dataAlert.showAndWait();

            }
        }
    }

    /**
     * Handles the end of a game, processes the game result message, and updates the game state accordingly.
     *
     * @param message The game result message received from the server.
     * @throws IOException If an I/O error occurs while processing the game result.
     */
    public static void endGame(String message) throws IOException {
        if (message != null) {
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

            if (Platform.isFxApplicationThread()) {
                // Update players and winners in the current lobby's game object
                LobbyManager.getCurrentLobby().getGameObject().updatePlayers(players, true);
            } else {
                // Run the update on the JavaFX application thread
                Platform.runLater(() -> LobbyManager.getCurrentLobby().getGameObject().updatePlayers(players, true));
            }

            LobbyManager.getCurrentLobby().getGameObject().getWinners().addAll(winersList);
            MainContainer.setOurTurnEvaluated(true);

            if (Platform.isFxApplicationThread()) {
                // Change the current scene to the game end scene
                FxManager.changeCurrentSceneToGameEndScene();
            } else {
                // Run the scene change on the JavaFX application thread
                Platform.runLater(() -> {
                    try {
                        FxManager.changeCurrentSceneToGameEndScene();
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                });
            }
        }
    }


}

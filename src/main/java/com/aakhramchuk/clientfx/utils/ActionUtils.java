package com.aakhramchuk.clientfx.utils;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.*;
import javafx.scene.control.Alert;
import org.apache.commons.configuration2.Configuration;

import java.io.IOException;

import static com.aakhramchuk.clientfx.objects.Constants.*;

public class ActionUtils {


    /**
     * Logs out the user from the application.
     *
     * @throws InterruptedException If the thread is interrupted during the operation.
     * @throws IOException          If an I/O error occurs during the operation.
     */
    public static void logout() throws InterruptedException, IOException {
        // Retrieve configuration and necessary data
        String logoutOpcode = MainContainer.getConnectionObject().getConfig().getString("message.logout_opcode");
        String logoutCommand = MainContainer.getConnectionObject().getConfig().getString("message.leave_logout_command");
        Configuration config = MainContainer.getConnectionObject().getConfig();

        // Create and send logout message
        String sentMessage = Utils.createMessage(config, logoutOpcode, logoutCommand);
        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(logoutOpcode, sentMessage);

        // Handle the response
        if (deserializedReceivedMessage.isSucess()) {
            MainContainer.setUser(null);
            // Show success alert
            Alert alert = FxUtils.createInformationAlert(config.getString("text.alert_title.information"),
                    config.getString("text.alert_header_text.information_about_logout"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
            ServerUtils.stopSchedulerServices();
            FxManager.changeCurrentSceneToLoginScene();
        } else {
            // Show error alert
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_in_logout_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
        }

    }

    /**
     * Logs in the user to the application.
     *
     * @param isReconectingLogin True if this is a reconnection after an internet connection issue; otherwise, false.
     * @throws IOException          If an I/O error occurs during the operation.
     * @throws InterruptedException If the thread is interrupted during the operation.
     */
    public static void login(boolean isReconectingLogin) throws IOException, InterruptedException {
        // Retrieve configuration and necessary data
        String loginOpcode = MainContainer.getConnectionObject().getConfig().getString("message.login_opcode");
        Configuration config = MainContainer.getConnectionObject().getConfig();

        // Create and send login message
        String sentMessage = Utils.createMessage(config, loginOpcode, MainContainer.getUser().toStringLogin());
        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(loginOpcode, sentMessage);

        // Handle the response
        if (!deserializedReceivedMessage.isSucess()) {
            MainContainer.setUser(null);
            // Show error alertaa
            Alert dataAlert = FxUtils.createErrorAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.error"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_login_process"),
                    deserializedReceivedMessage.getMessage());
            dataAlert.showAndWait();
            if (isReconectingLogin) {
                FxManager.changeCurrentSceneToLoginScene();
            }
        } else {
            // Deserialize and update lobbies list
            String messageType = Utils.deserializeLoginStateAndUpdateLobbiesList(config, deserializedReceivedMessage);
            ServerUtils.startSchedulerServices();
            if (isReconectingLogin) {
                FxUtils.youLoggedInAuthomaticlyDueToInternetConnectionIssue(MainContainer.getUser());
            }
            if (MENU_PREFIX_VALUE.equals(messageType)) {
                // Change scene to the main menu
                FxContainer.setCurrentScene(FxContainer.getCurrentStage().getScene());
                FxContainer.getCurrentStage().setScene(FxManager.getMainMenuScene());
            } else if(LOBBY_PREFIX_VALE.equals(messageType)) {
                // Change scene to the lobby
                FxManager.changeCurrentSceneToLobbyScene();
            } else if (GAME_PREFIX_VALUE.equals(messageType)) {
                // Change scene to the game
                FxManager.changeCurrentSceneToGameScene();
            }
        }
    }

    /**
     * Creates a lobby with the specified parameters.
     *
     * @param lobbyName          The name of the lobby to create.
     * @param lobbyMaxCountOfPlayers The maximum count of players allowed in the lobby.
     * @param hasPassword        True if the lobby has a password; otherwise, false.
     * @param password           The lobby's password.
     * @return True if the lobby creation is successful; otherwise, false.
     * @throws InterruptedException If the thread is interrupted during the operation.
     */
    public static boolean createLobby(String lobbyName, int lobbyMaxCountOfPlayers, boolean hasPassword, String password) throws InterruptedException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String lobbyJoinOpcode = connectionObject.getConfig().getString("message.lobby_create_opcode");
        Configuration config = connectionObject.getConfig();

        // Create lobby creation message
        String lobbyCreateString = (hasPassword ? Lobby.toCreateString(lobbyName, lobbyMaxCountOfPlayers, true, password) : Lobby.toCreateStringWithoutPassword(lobbyName, lobbyMaxCountOfPlayers));

        // Send lobby creation message and handle the response
        String sentMessage = Utils.createMessage(config, lobbyJoinOpcode, lobbyCreateString);
        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(lobbyJoinOpcode, sentMessage);

        if (deserializedReceivedMessage.isSucess()) {
            // Parse and update lobbies
            Utils.parseAndUpdateLobbies(deserializedReceivedMessage.getMessage());
            FxUtils.showSuccessLobbyCreationAlert();
            return true;
        } else {
            // Show error alert
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_lobby_creation_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Performs a lobby action based on the provided operation code, such as joining or leaving a lobby.
     *
     * @param operationCode The operation code indicating the type of lobby action.
     * @param selectedLobby The lobby on which the action is performed.
     * @param password      The lobby's password if required, or an empty string if not.
     * @throws InterruptedException If the thread is interrupted during the operation.
     * @throws IOException          If an I/O error occurs during the operation.
     */
    public static void actionLobby(String operationCode, Lobby selectedLobby, String password) throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        boolean isJoin = Constants.JOIN_LOBBY_OPCODE_CONFIG_VALUE.equals(operationCode);
        String lobbyActionOpcode = connectionObject.getConfig().getString(operationCode);
        Configuration config = connectionObject.getConfig();
        String LobbyActionString = selectedLobby.toActionString(password);

        // Create and send lobby action message
        String sentMessage = Utils.createMessage(config, lobbyActionOpcode, LobbyActionString);
        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(lobbyActionOpcode, sentMessage);

        // Handle the response
        if (!deserializedReceivedMessage.isSucess()) {
            if (!isJoin) {
            // Show error alert for lobby delete process
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_in_lobby_delete_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
            } else {
                // Show error alert for lobby join process
                Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                        config.getString("text.alert_header_text.error_in_lobby_join_process"),
                        deserializedReceivedMessage.getMessage());
                alert.showAndWait();
            }
        } else {
            if (isJoin) {
                // Successfully joined the lobby
                selectedLobby = Utils.parseLobby(deserializedReceivedMessage.getMessage().substring(LOBBY_PREFIX_VALE.length()), true, false);
                FxUtils.showSucessLobbyJoinAlert();
                LobbyManager.setCurrentLobby(selectedLobby);
                FxUtils.closeCurrentModalWindowIfExist();
                FxManager.changeCurrentSceneToLobbyScene();
            } else {
                // Successfully left the lobby
                FxUtils.showSuccessLobbyDeleteAlert();
                LobbyManager.setCurrentLobby(null);
                Utils.deserializeStateAndUpdateLobbiesList(config, deserializedReceivedMessage);
                FxUtils.closeCurrentModalWindowIfExist();
            }
        }
    }

    /**
     * Leaves the current lobby and returns to the main menu.
     *
     * @throws InterruptedException If the thread is interrupted during the operation.
     * @throws IOException          If an I/O error occurs during the operation.
     */
    public static void leaveLobby() throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String logoutOpcode = connectionObject.getConfig().getString("message.logout_opcode");
        String logoutExitLobbyCommand = connectionObject.getConfig().getString("message.leave_exit_lobby_command");
        Configuration config = connectionObject.getConfig();

        // Create and send lobby leave message
        String sentMessage = Utils.createMessage(config, logoutOpcode, logoutExitLobbyCommand);
        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(logoutOpcode, sentMessage);

        // Handle the response
        if (!deserializedReceivedMessage.isSucess()) {
            // Show error alert for lobby leave process
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_in_lobby_leave_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
        } else {
            // Show success alert for lobby leave process
            FxUtils.showSuccessLobbyLeaveAlert();
            LobbyManager.setCurrentLobby(null);
            Utils.deserializeStateAndUpdateLobbiesList(config, deserializedReceivedMessage);
            FxManager.changeCurrentSceneToMainMenuScene();
        }
    }

}

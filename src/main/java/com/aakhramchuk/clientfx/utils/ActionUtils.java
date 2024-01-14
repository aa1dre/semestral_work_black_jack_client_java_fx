package com.aakhramchuk.clientfx.utils;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.*;
import javafx.scene.control.Alert;
import org.apache.commons.configuration2.Configuration;

import java.io.IOException;

import static com.aakhramchuk.clientfx.objects.Constants.LOBBY_PREFIX_VALE;

public class ActionUtils {


    public static void logout() throws InterruptedException, IOException {
        String logoutOpcode = MainContainer.getConnectionObject().getConfig().getString("message.logout_opcode");
        String logoutCommand = MainContainer.getConnectionObject().getConfig().getString("message.leave_logout_command");
        Configuration config = MainContainer.getConnectionObject().getConfig();

        String sentMessage = Utils.createMessage(config, logoutOpcode, logoutCommand);

        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(logoutOpcode, sentMessage);

        if (deserializedReceivedMessage.isSucess()) {
            MainContainer.setUser(null);
            Alert alert = FxUtils.createInformationAlert(config.getString("text.alert_title.information"),
                    config.getString("text.alert_header_text.information_about_logout"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
            ServerUtils.stopSchedulerServices();
            FxManager.changeCurrentSceneToLoginScene();
        } else {
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_in_logout_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
        }

    }

    public static void login(boolean isReconectingLogin) throws IOException, InterruptedException {
        String loginOpcode = MainContainer.getConnectionObject().getConfig().getString("message.login_opcode");
        Configuration config = MainContainer.getConnectionObject().getConfig();

        String sentMessage = Utils.createMessage(config, loginOpcode, MainContainer.getUser().toStringLogin());

        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(loginOpcode, sentMessage);
        if (!deserializedReceivedMessage.isSucess()) {
            if (!isReconectingLogin) {
                MainContainer.setUser(null);
            }
            Alert dataAlert = FxUtils.createErrorAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.error"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_login_process"),
                    deserializedReceivedMessage.getMessage());
            dataAlert.showAndWait();
        } else {
            String messageType = Utils.deserializeLoginStateAndUpdateLobbiesList(config, deserializedReceivedMessage);
            ServerUtils.startSchedulerServices();
            if (isReconectingLogin) {
                FxUtils.youLoggedInAuthomaticlyDueToInternetConnectionIssue(MainContainer.getUser());
            }
            if ("MENU".equals(messageType)) {
                FxContainer.setCurrentScene(FxContainer.getCurrentStage().getScene());
                FxContainer.getCurrentStage().setScene(FxManager.getMainMenuScene());
            } else if("LOBBY".equals(messageType)) {
                FxManager.changeCurrentSceneToLobbyScene();
            } else if ("GAME".equals(messageType)) {
                FxManager.changeCurrentSceneToGameScene();
            }
        }
    }

    public static boolean createLobby(String lobbyName, int lobbyMaxCountOfPlayers, boolean hasPassword, String password) throws InterruptedException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String lobbyJoinOpcode = connectionObject.getConfig().getString("message.lobby_create_opcode");
        Configuration config = connectionObject.getConfig();

        String lobbyCreateString = (hasPassword ? Lobby.toCreateString(lobbyName, lobbyMaxCountOfPlayers, true, password) : Lobby.toCreateStringWithoutPassword(lobbyName, lobbyMaxCountOfPlayers));

        String sentMessage = Utils.createMessage(config, lobbyJoinOpcode, lobbyCreateString);

        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(lobbyJoinOpcode, sentMessage);

        if (deserializedReceivedMessage.isSucess()) {
            Utils.parseAndUpdateLobbies(deserializedReceivedMessage.getMessage());
            FxUtils.showSuccessLobbyCreationAlert();
            return true;
        } else {
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_lobby_creation_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    public static void actionLobby(String operationCode, Lobby selectedLobby, String password) throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        boolean isJoin = Constants.JOIN_LOBBY_OPCODE_CONFIG_VALUE.equals(operationCode);
        String lobbyActionOpcode = connectionObject.getConfig().getString(operationCode);
        Configuration config = connectionObject.getConfig();
        String LobbyActionString = selectedLobby.toActionString(password);


        String sentMessage = Utils.createMessage(config, lobbyActionOpcode, LobbyActionString);
        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(lobbyActionOpcode, sentMessage);

        if (!deserializedReceivedMessage.isSucess()) {
            if (!isJoin) {
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_in_lobby_delete_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
            } else {
                Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                        config.getString("text.alert_header_text.error_in_lobby_join_process"),
                        deserializedReceivedMessage.getMessage());
                alert.showAndWait();
            }
        } else {
            if (isJoin) {
                selectedLobby = Utils.parseLobby(deserializedReceivedMessage.getMessage().substring(LOBBY_PREFIX_VALE.length()), true, false);
                FxUtils.showSucessLobbyJoinAlert();
                LobbyManager.setCurrentLobby(selectedLobby);
                FxUtils.closeCurrentModalWindowIfExist();
                FxManager.changeCurrentSceneToLobbyScene();
            } else {
                FxUtils.showSuccessLobbyDeleteAlert();
                LobbyManager.setCurrentLobby(null);
                Utils.deserializeStateAndUpdateLobbiesList(config, deserializedReceivedMessage);
                FxUtils.closeCurrentModalWindowIfExist();
            }
        }
    }

    public static void leaveLobby() throws InterruptedException, IOException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String logoutOpcode = connectionObject.getConfig().getString("message.logout_opcode");
        String logoutExitLobbyCommand = connectionObject.getConfig().getString("message.leave_exit_lobby_command");
        Configuration config = connectionObject.getConfig();

        String sentMessage = Utils.createMessage(config, logoutOpcode, logoutExitLobbyCommand);
        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(logoutOpcode, sentMessage);

        if (!deserializedReceivedMessage.isSucess()) {
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_in_lobby_leave_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
        } else {
            FxUtils.showSuccessLobbyLeaveAlert();
            LobbyManager.setCurrentLobby(null);
            Utils.deserializeStateAndUpdateLobbiesList(config, deserializedReceivedMessage);
            FxManager.changeCurrentSceneToMainMenuScene();
        }
    }

}

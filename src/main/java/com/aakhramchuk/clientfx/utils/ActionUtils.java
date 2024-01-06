package com.aakhramchuk.clientfx.utils;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.*;
import javafx.scene.control.Alert;
import org.apache.commons.configuration2.Configuration;

import java.io.IOException;
import java.util.Scanner;

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
            FxContainer.getCurrentStage().setScene(FxManager.getLoginScene());
        } else {
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_in_logout_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
        }

    }

    public static boolean createLobby(String lobbyName, int lobbyMaxCountOfPlayers, boolean hasPassword, String password) throws InterruptedException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        String lobbyJoinOpcode = connectionObject.getConfig().getString("message.lobby_create_opcode");
        Configuration config = connectionObject.getConfig();
        Scanner scanner = connectionObject.getScanner();

        String lobbyCreateString = (hasPassword ? Lobby.toCreateString(lobbyName, lobbyMaxCountOfPlayers, true, password) : Lobby.toCreateStringWithoutPassword(lobbyName, lobbyMaxCountOfPlayers));

        String sentMessage = Utils.createMessage(config, lobbyJoinOpcode, lobbyCreateString);

        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(lobbyJoinOpcode, sentMessage);

        if (deserializedReceivedMessage.isSucess()) {
            Utils.parseAndUpdateLobbies(deserializedReceivedMessage.getMessage());
            Alert alert = FxUtils.createInformationAlert(config.getString("text.alert_title.information"),
                    config.getString("text.alert_header_text.information_about_lobby_creation"),
                    config.getString("text.alert_content_text.information_about_lobby_creation"));
            alert.showAndWait();
            return true;
        } else {
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_lobby_creation_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    public static void actionLobby(String operationCode, Lobby selectedLobby, String password) throws InterruptedException {
        ConnectionObject connectionObject = MainContainer.getConnectionObject();
        boolean isJoin = Constants.JOIN_LOBBY_OPCODE_CONFIG_VALUE.equals(operationCode);
        String lobbyActionOpcode = connectionObject.getConfig().getString(operationCode);
        Configuration config = connectionObject.getConfig();
        String LobbyActionString = selectedLobby.toActionString(password);


        String sentMessage = Utils.createMessage(config, lobbyActionOpcode, LobbyActionString);
        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(lobbyActionOpcode, sentMessage);

        if (!deserializedReceivedMessage.isSucess()) {
            Alert alert = FxUtils.createErrorAlert(config.getString("text.alert_title.error"),
                    config.getString("text.alert_header_text.error_in_lobby_delete_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
        } else {
            if (isJoin) {
//                Utils.parseLobby(deserializedReceivedMessage.getMessage().substring("LOBBY".length()), true, false);
//                LobbyManager.setCurrentLobby(lobby);
//                inSelectLobbyMenu = false;
//                startGameLobby();
            } else {
                Alert alert = FxUtils.createInformationAlert(config.getString("text.alert_title.information"),
                        config.getString("text.alert_header_text.information_about_lobby_deletion"),
                        config.getString("text.alert_content_text.information_about_lobby_deletion"));
                alert.showAndWait();
                LobbyManager.setCurrentLobby(null);
                Utils.deserializeStateAndUpdateLobbiesList(config, deserializedReceivedMessage);
                if (FxContainer.getCurrentModalWindow() != null) {
                    FxContainer.getCurrentModalWindow().close();
                }
            }
        }
    }

}

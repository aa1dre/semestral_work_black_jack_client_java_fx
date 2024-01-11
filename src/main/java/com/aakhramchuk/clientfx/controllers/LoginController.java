package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.utils.FxUtils;
import com.aakhramchuk.clientfx.utils.ServerUtils;
import com.aakhramchuk.clientfx.utils.Utils;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.DeserializedMessage;
import com.aakhramchuk.clientfx.objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.apache.commons.configuration2.Configuration;

import java.io.IOException;
import java.util.function.UnaryOperator;

public class LoginController {
    @FXML
    private TextField loginTf;

    @FXML
    private PasswordField passwordTf;

    @FXML
    private Button loginButton;

    private static String email;
    private static String login;
    private static String verifiablePassword;


    @FXML
    public void initialize() {
        MainContainer.setInSelectLobbyMenu(false);
        MainContainer.setInGameEndMenu(false);
        MainContainer.setInGame(false);
        MainContainer.setInLobbyMenu(false);

        FxUtils.applyValidation(loginTf);
        FxUtils.applyValidation(passwordTf);
    }

    @FXML
    public void loginButtonAction(ActionEvent action) throws IOException, InterruptedException {
        String loginOpcode = MainContainer.getConnectionObject().getConfig().getString("message.login_opcode");
        Configuration config = MainContainer.getConnectionObject().getConfig();

        if(loginTf.getText().isEmpty() || loginTf.getText().isBlank()) {
            FxUtils.showEmptyLoginAlert();
            return;
        }

        if (passwordTf.getText().isEmpty() || passwordTf.getText().isBlank()) {
            FxUtils.showEmptyPasswordAlert();
            return;
        }

        MainContainer.setUser(new User(loginTf.getText(), passwordTf.getText()));
        String sentMessage = Utils.createMessage(config, loginOpcode, MainContainer.getUser().toStringLogin());

        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(loginOpcode, sentMessage);

        if (!deserializedReceivedMessage.isSucess()) {
            MainContainer.setUser(null);

            Alert dataAlert = FxUtils.createErrorAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.error"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_login_process"),
                    deserializedReceivedMessage.getMessage());
            dataAlert.showAndWait();
        } else {
            String messageType = Utils.deserializeLoginStateAndUpdateLobbiesList(config, deserializedReceivedMessage);
            ServerUtils.startSchedulerServices();
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

    @FXML
    public void hrefAction(ActionEvent action) throws IOException {
        FxManager.createRegistrationModalWindow();
    }
}
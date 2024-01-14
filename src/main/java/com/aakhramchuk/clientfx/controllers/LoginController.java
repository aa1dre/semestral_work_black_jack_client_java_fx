package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.utils.ActionUtils;
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

        if(loginTf.getText().isEmpty() || loginTf.getText().isBlank()) {
            FxUtils.showEmptyLoginAlert();
            return;
        }

        if (passwordTf.getText().isEmpty() || passwordTf.getText().isBlank()) {
            FxUtils.showEmptyPasswordAlert();
            return;
        }

        MainContainer.setUser(new User(loginTf.getText(), passwordTf.getText()));
        ActionUtils.login(false);
    }

    @FXML
    public void hrefAction(ActionEvent action) throws IOException {
        FxManager.createRegistrationModalWindow();
    }
}
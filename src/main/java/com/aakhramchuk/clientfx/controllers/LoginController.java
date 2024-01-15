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

    /**
     * Initialize the login screen.
     * Set the initial states for the different containers and apply validation to text fields.
     */
    @FXML
    public void initialize() {
        MainContainer.setInSelectLobbyMenu(false);
        MainContainer.setInGameEndMenu(false);
        MainContainer.setInGame(false);
        MainContainer.setInLobbyMenu(false);

        // Apply validation to text fields for user input.
        FxUtils.applyValidation(loginTf);
        FxUtils.applyValidation(passwordTf);
    }

    /**
     * Handle the login button click event to attempt user login.
     * Validates user input, sets the user's credentials, and initiates the login process.
     *
     * @param action The ActionEvent triggered by the button click.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
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

        if (MainContainer.isConnected()) {
            MainContainer.setUser(new User(loginTf.getText(), passwordTf.getText()));
            ActionUtils.login(false);
        }
    }

    /**
     * Handle the hyperlink action to navigate to the registration screen.
     *
     * @param action The ActionEvent triggered by the hyperlink click.
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    public void hrefAction(ActionEvent action) throws IOException {
        // Open the registration modal window when the hyperlink is clicked.
        FxManager.createRegistrationModalWindow();
    }
}
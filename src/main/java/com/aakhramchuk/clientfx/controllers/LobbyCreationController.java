package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.objects.LobbyManager;
import com.aakhramchuk.clientfx.utils.ActionUtils;
import com.aakhramchuk.clientfx.utils.FxUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;


import java.io.*;

public class LobbyCreationController {

    @FXML
    private VBox vBox;

    @FXML
    private TextField nameTf;

    @FXML
    private TextField maxCountOfPlayersTf;

    @FXML
    private CheckBox passwordChbx;

    @FXML
    private PasswordField passwordTf;

    @FXML
    private Label passwordLbl;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    /**
     * Handle the cancel button click event to close the current modal window.
     *
     * @param event The ActionEvent triggered by the cancel button.
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    public void cancelButtonAction(ActionEvent event) throws IOException {
        FxUtils.closeCurrentModalWindowIfExist();
    }

    /**
     * Handle the confirm button click event to create a lobby with specified parameters.
     *
     * @param event The ActionEvent triggered by the confirm button.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void registrationButtonAction(ActionEvent event) throws IOException, InterruptedException {
        // Validate and process lobby creation parameters
        if (nameTf.getText().isEmpty() || nameTf.getText().isBlank()) {
            FxUtils.showEmptyNameAlert();
            return;
        }

        if (maxCountOfPlayersTf.getText().isEmpty() || !maxCountOfPlayersTf.getText().matches("\\d+") || maxCountOfPlayersTf.getText().length() > 8 || Integer.parseInt(maxCountOfPlayersTf.getText()) <= 0) {
            FxUtils.showEmptyMaxCountOfPlayersAlert();
            return;
        }

        if (passwordChbx.isSelected()) {
            if (passwordTf.getText().isEmpty() || passwordTf.getText().isBlank()) {
                FxUtils.showEmptyPasswordAlert();
                return;
            }
        }

        if (MainContainer.isConnected()) {
            if (ActionUtils.createLobby(nameTf.getText(), Integer.parseInt(maxCountOfPlayersTf.getText()), passwordChbx.isSelected(), passwordChbx.isSelected() ? passwordTf.getText() : null)) {
                FxUtils.closeCurrentModalWindowIfExist();
            }
        }

    }


    /**
     * Initialize the lobby creation screen.
     */
    @FXML
    public void initialize() {
        // Apply input validation to text fields
        FxUtils.applyValidation(nameTf);
        FxUtils.applyValidation(maxCountOfPlayersTf);
        FxUtils.applyValidation(passwordTf);

        // Initialize password-related fields based on the checkbox selection
        passwordTf.setDisable(!passwordChbx.isSelected());
        passwordTf.setVisible(passwordChbx.isSelected());
        passwordLbl.setOpacity(passwordChbx.isSelected() ? 1.0 : 0.0);

        // Add a listener to toggle password-related fields based on the checkbox selection
        passwordChbx.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            passwordTf.setDisable(!isNowSelected);
            passwordTf.setVisible(isNowSelected);
            passwordLbl.setOpacity(isNowSelected ? 1.0 : 0.0);
        });

        // Ensure that maxCountOfPlayersTf only accepts numeric input
        maxCountOfPlayersTf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxCountOfPlayersTf.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Set a background image for the VBox
        FxUtils.setBackgroundImage(vBox);
    }


}

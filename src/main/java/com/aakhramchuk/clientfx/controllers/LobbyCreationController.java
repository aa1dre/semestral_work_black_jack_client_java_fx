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

    @FXML
    public void cancelButtonAction(ActionEvent event) throws IOException {
        FxUtils.closeCurrentModalWindowIfExist();
    }

    @FXML
    public void registrationButtonAction(ActionEvent event) throws IOException, InterruptedException {
        if (nameTf.getText().isEmpty() || nameTf.getText().isBlank()) {
            FxUtils.showEmptyNameAlert();
            return;
        }

        if (maxCountOfPlayersTf.getText().isEmpty() || !maxCountOfPlayersTf.getText().matches("\\d+") || Integer.parseInt(maxCountOfPlayersTf.getText()) <= 0) {
            FxUtils.showEmptyMaxCountOfPlayersAlert();
            return;
        }

        if (passwordChbx.isSelected()) {
            if (passwordTf.getText().isEmpty() || passwordTf.getText().isBlank()) {
                FxUtils.showEmptyPasswordAlert();
                return;
            }
        }

        if (ActionUtils.createLobby(nameTf.getText(), Integer.parseInt(maxCountOfPlayersTf.getText()), passwordChbx.isSelected(), passwordChbx.isSelected() ? passwordTf.getText() : null)) {
            FxUtils.closeCurrentModalWindowIfExist();
        }

    }

    @FXML
    public void initialize() {
        FxUtils.applyValidation(nameTf);
        FxUtils.applyValidation(maxCountOfPlayersTf);
        FxUtils.applyValidation(passwordTf);

        passwordTf.setDisable(!passwordChbx.isSelected());
        passwordTf.setVisible(passwordChbx.isSelected());
        passwordLbl.setOpacity(passwordChbx.isSelected() ? 1.0 : 0.0);

        passwordChbx.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            passwordTf.setDisable(!isNowSelected);
            passwordTf.setVisible(isNowSelected);
            passwordLbl.setOpacity(isNowSelected ? 1.0 : 0.0);
        });

        maxCountOfPlayersTf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maxCountOfPlayersTf.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        FxUtils.setBackgroundImage(vBox);
    }


}

package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.objects.Lobby;
import com.aakhramchuk.clientfx.utils.ActionUtils;
import com.aakhramchuk.clientfx.utils.FxUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.*;

import java.io.IOException;

import static com.aakhramchuk.clientfx.objects.Constants.DELETE_LOBBY_OPCODE_CONFIG_VALUE;
import static com.aakhramchuk.clientfx.objects.Constants.JOIN_LOBBY_OPCODE_CONFIG_VALUE;

public class EnterPasswordController {
    @FXML
    private VBox vBox;

    @FXML
    private PasswordField passwordTf;

    private Lobby lobby;

    private boolean isJoin;

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setIsJoin(boolean isJoin) {
        this.isJoin = isJoin;
    }


    @FXML
    public void cancelButtonAction(ActionEvent event) throws IOException {
        FxUtils.closeCurrentModalWindowIfExist();
    }

    @FXML
    public void confirmBtnAction(ActionEvent event) throws IOException, InterruptedException {
        if (passwordTf.getText().isEmpty() || passwordTf.getText().isBlank()) {
            FxUtils.showEmptyPasswordAlert();
            return;
        }

        if (isJoin) {
            ActionUtils.actionLobby(JOIN_LOBBY_OPCODE_CONFIG_VALUE, lobby, passwordTf.getText());
        } else {
            ActionUtils.actionLobby(DELETE_LOBBY_OPCODE_CONFIG_VALUE, lobby, passwordTf.getText());
        }

    }

    @FXML
    public void initialize() {
        FxUtils.applyValidation(passwordTf);
        FxUtils.setBackgroundImage(vBox);
    }
}

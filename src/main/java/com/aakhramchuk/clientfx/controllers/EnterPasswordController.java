package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.objects.Lobby;
import com.aakhramchuk.clientfx.utils.ActionUtils;
import com.aakhramchuk.clientfx.utils.FxUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.aakhramchuk.clientfx.objects.Constants.DELETE_LOBBY_OPCODE_CONFIG_VALUE;

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
        FxContainer.getCurrentModalWindow().close();
    }

    @FXML
    public void confirmBtnAction(ActionEvent event) throws IOException, InterruptedException {
        if (passwordTf.getText().isEmpty() || passwordTf.getText().isBlank()) {
            Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_password_field"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_password_field"));
            dataAlert.showAndWait();
            return;
        }

        if (isJoin) {

        } else {
            ActionUtils.actionLobby(DELETE_LOBBY_OPCODE_CONFIG_VALUE, lobby, passwordTf.getText());
        }

    }

    @FXML
    public void initialize() {
        try {
            InputStream is = getClass().getResourceAsStream("/Images/background.jpg");
            if (is == null) {
                throw new FileNotFoundException("Cannot find background image");
            }

            Image image = new Image(is);
            BackgroundImage bgImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            vBox.setBackground(new Background(bgImage));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

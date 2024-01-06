package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.objects.DeserializedMessage;
import com.aakhramchuk.clientfx.objects.User;
import com.aakhramchuk.clientfx.utils.ActionUtils;
import com.aakhramchuk.clientfx.utils.FxUtils;
import com.aakhramchuk.clientfx.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.apache.commons.configuration2.Configuration;

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
        FxContainer.getCurrentModalWindow().close();
    }

    @FXML
    public void registrationButtonAction(ActionEvent event) throws IOException, InterruptedException {
        if (nameTf.getText().isEmpty() || nameTf.getText().isBlank()) {
            Alert alert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_name_field"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_name_field"));
            alert.showAndWait();
            return;
        }

        if (maxCountOfPlayersTf.getText().isEmpty() || !maxCountOfPlayersTf.getText().matches("\\d+") || Integer.parseInt(maxCountOfPlayersTf.getText()) <= 0) {
            Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_max_players_field"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_max_players_field"));
            dataAlert.showAndWait();
            return;
        }

        if (passwordChbx.isSelected()) {
            if (passwordTf.getText().isEmpty() || passwordTf.getText().isBlank()) {
                Alert alert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                        MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_password_field"),
                        MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_password_field"));
                alert.showAndWait();
                return;
            }
        }

        if (ActionUtils.createLobby(nameTf.getText(), Integer.parseInt(maxCountOfPlayersTf.getText()), passwordChbx.isSelected(), passwordChbx.isSelected() ? passwordTf.getText() : null)) {
            FxContainer.getCurrentModalWindow().close();
        }

    }

    @FXML
    public void initialize() {
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

package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.objects.DeserializedMessage;
import com.aakhramchuk.clientfx.objects.User;
import com.aakhramchuk.clientfx.utils.FxUtils;
import com.aakhramchuk.clientfx.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.apache.commons.configuration2.Configuration;

import java.io.*;

public class RegistationController {

    @FXML
    private VBox vBox;

    @FXML
    private TextField nameTf;

    @FXML
    private TextField surnameTf;

    @FXML
    private TextField loginTf;

    @FXML
    private PasswordField passwordTf;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button registrationBtn;

    @FXML
    public void cancelBtnAction(ActionEvent event) throws IOException {
        FxContainer.getCurrentModalWindow().close();
    }

    @FXML
    public void registrationButtonAction(ActionEvent event) throws IOException, InterruptedException {
        String registrationOpcode = MainContainer.getConnectionObject().getConfig().getString("message.registration_opcode");
        Configuration config = MainContainer.getConnectionObject().getConfig();

        if (nameTf.getText().isEmpty() || nameTf.getText().isBlank()) {
            Alert alert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_name_field"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_name_field"));
            alert.showAndWait();
            return;
        }

        if (surnameTf.getText().isEmpty() || surnameTf.getText().isBlank()) {
            Alert alert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_surname_field"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_surname_field"));
            alert.showAndWait();
            return;
        }

        if (loginTf.getText().isEmpty() || loginTf.getText().isBlank()) {
            Alert alert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_login_field"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_login_field"));
            alert.showAndWait();
            return;
        }

        if (passwordTf.getText().isEmpty() || passwordTf.getText().isBlank()) {
            Alert alert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_password_field"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_password_field"));
            alert.showAndWait();
            return;
        }

        User user = new User(loginTf.getText(), passwordTf.getText(), nameTf.getText(), surnameTf.getText());
        String sentMessage = Utils.createMessage(config, registrationOpcode, user.toStringRegistration());
        DeserializedMessage deserializedReceivedMessage = Utils.sendMesageAndTakeResponse(registrationOpcode, sentMessage);

        if (!deserializedReceivedMessage.isSucess()) {
            Alert alert = FxUtils.createErrorAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.error"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_registration_process"),
                    deserializedReceivedMessage.getMessage());
            alert.showAndWait();
        } else {
            Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_registration"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_registration"));
            alert.showAndWait();
            FxContainer.getCurrentModalWindow().close();
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

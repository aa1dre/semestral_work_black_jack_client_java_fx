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
        FxUtils.closeCurrentModalWindowIfExist();
    }

    @FXML
    public void registrationButtonAction(ActionEvent event) throws IOException, InterruptedException {
        String registrationOpcode = MainContainer.getConnectionObject().getConfig().getString("message.registration_opcode");
        Configuration config = MainContainer.getConnectionObject().getConfig();

        if (nameTf.getText().isEmpty() || nameTf.getText().isBlank()) {
            FxUtils.showEmptyNameAlert();
            return;
        }

        if (surnameTf.getText().isEmpty() || surnameTf.getText().isBlank()) {
            FxUtils.showEmptySurnameAlert();
            return;
        }

        if (loginTf.getText().isEmpty() || loginTf.getText().isBlank()) {
            FxUtils.showEmptyLoginAlert();
            return;
        }

        if (passwordTf.getText().isEmpty() || passwordTf.getText().isBlank()) {
            FxUtils.showEmptyPasswordAlert();
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
            FxUtils.showSuccessRegistrationAlert();
            FxUtils.closeCurrentModalWindowIfExist();
        }
    }

    @FXML
    public void initialize() {
        FxUtils.applyValidation(nameTf);
        FxUtils.applyValidation(surnameTf);
        FxUtils.applyValidation(loginTf);
        FxUtils.applyValidation(passwordTf);
        FxUtils.setBackgroundImage(vBox);
    }

}

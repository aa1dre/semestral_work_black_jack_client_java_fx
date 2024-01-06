package com.aakhramchuk.clientfx.utils;

import javafx.scene.control.Alert;

public class FxUtils {

    public static Alert createErrorAlert(String title, String headerText, String contentText) {
        return createAlert(Alert.AlertType.ERROR, title, headerText, contentText);
    }

    public static Alert createWarningAlert(String title, String headerText, String contentText) {
        return createAlert(Alert.AlertType.WARNING, title, headerText, contentText);
    }

    public static Alert createInformationAlert(String title, String headerText, String contentText) {
        return createAlert(Alert.AlertType.INFORMATION, title, headerText, contentText);
    }

    private static Alert createAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        return alert;
    }
}

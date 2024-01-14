package com.aakhramchuk.clientfx.utils;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.objects.User;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.function.UnaryOperator;

public class FxUtils {

    private static final Logger logger = LogManager.getLogger(FxUtils.class);

    public static Alert createErrorAlert(String title, String headerText, String contentText) {
        return createAlert(Alert.AlertType.ERROR, title, headerText, contentText);
    }

    public static Alert createWarningAlert(String title, String headerText, String contentText) {
        return createAlert(Alert.AlertType.WARNING, title, headerText, contentText);
    }

    public static Alert createInformationAlert(String title, String headerText, String contentText) {
        return createAlert(Alert.AlertType.INFORMATION, title, headerText, contentText);
    }

    public static void showEmptyPasswordAlert() {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_password_field"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_password_field"));
        dataAlert.showAndWait();
    }

    public static void showDisconnectAlert(User dissconectedUser) {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.player_disconnected"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.player_disconnected") + " " + dissconectedUser.toString());
        dataAlert.showAndWait();
    }

    public static void showTurnAlert() {
        Alert dataAlert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.your_turn"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.your_turn"));
        dataAlert.showAndWait();
    }

    public static void showDisconnectEndAlert(User dissconectedUser) {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.player_disconnected_game_end"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.player_disconnected_game_end") + " " + dissconectedUser.toString());
        dataAlert.showAndWait();
    }

    public static void youLoggedInAuthomaticlyDueToInternetConnectionIssue(User automaticallyAuthenticatedUser) {
        Alert dataAlert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.logged_in_automaticly"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.logged_in_automaticly") + " " + automaticallyAuthenticatedUser.toString());
        dataAlert.showAndWait();
    }

    public static void showGameLeaveAlert(User dissconectedUser) {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.player_leave_game_end"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.player_leave_game_end") + " " + dissconectedUser.toString());
        dataAlert.showAndWait();
    }

    public static void showEmptyLoginAlert() {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_login_field"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_login_field"));
        dataAlert.showAndWait();
    }

    public static void showEmptyMaxCountOfPlayersAlert() {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_max_players_field"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_max_players_field"));
        dataAlert.showAndWait();
    }

    public static void showEmptyNameAlert() {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_name_field"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_name_field"));
        dataAlert.showAndWait();
    }

    public static void showSuccessLobbyCreationAlert() {
        Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_lobby_creation"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_lobby_creation"));
        alert.showAndWait();
    }

    public static void showSuccessLobbyLeaveAlert() {
        Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_lobby_leave"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_lobby_leave"));
        alert.showAndWait();
    }

    public static void showSuccessLobbyDeleteAlert() {
        Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_lobby_deletion"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_lobby_deletion"));
        alert.showAndWait();
    }

    public static void showSucessLobbyJoinAlert() {
        Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_lobby_join"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_lobby_join"));
        alert.showAndWait();
    }

    public static void showErrorInLobbyDeleteProcessAlert() {
        Alert alert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_lobby_delete_process"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_lobby_delete_process_select"));
        alert.showAndWait();
    }

    public static void showEmptySurnameAlert() {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_surname_field"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_surname_field"));
        dataAlert.showAndWait();
    }

    public static void showSuccessRegistrationAlert() {
        Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_registration"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_registration"));
        alert.showAndWait();
    }

    private static Alert createAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        return alert;
    }

    public static boolean closeCurrentModalWindowIfExist() {
        if (FxContainer.getCurrentModalWindow() != null) {
            FxContainer.getCurrentModalWindow().close();
            return true;
        }

        return false;
    }

    public static void applyValidation(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = createFilter(textField);
        StringConverter<String> converter = new DefaultStringConverter();
        TextFormatter<String> formatter = new TextFormatter<>(converter, "", filter);

        textField.setTextFormatter(formatter);
    }

    private static UnaryOperator<TextFormatter.Change> createFilter(TextField textField) {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 100) {
                showTooltip(textField, MainContainer.getConnectionObject().getConfig().getString("text.tooltip_warning_max_length_100"));
                return null;
            } else if (newText.matches(".*[\\]\\[;\\\\].*")) {
                showTooltip(textField, MainContainer.getConnectionObject().getConfig().getString("text.tooltip_prohibited_characters"));
                return null;
            }
            hideTooltip(textField);
            return change;
        };
    }

    private static void showTooltip(TextField textField, String message) {
        Tooltip tooltip = textField.getTooltip();
        if (tooltip == null) {
            tooltip = new Tooltip();
            tooltip.setShowDelay(Duration.millis(100));
            textField.setTooltip(tooltip);
        }
        tooltip.setText(message);
        tooltip.show(textField, textField.getScene().getWindow().getX() + textField.getLayoutX(),
                textField.getScene().getWindow().getY() + textField.getLayoutY() + textField.getHeight());
    }

    private static void hideTooltip(TextField textField) {
        if (textField.getTooltip() != null) {
            textField.getTooltip().hide();
        }
    }

    public static void setBackgroundImage(VBox vBox) {
        try {
            InputStream is = FxUtils.class.getResourceAsStream("/Images/background.jpg");
            if (is == null) {
                throw new FileNotFoundException("Cannot find background image");
            }

            Image image = new Image(is);
            BackgroundImage bgImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            vBox.setBackground(new Background(bgImage));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
    }

    public static void setBackgroundImage(BorderPane borderPane) {
        try {
            InputStream is = FxUtils.class.getResourceAsStream("/Images/background.jpg");
            if (is == null) {
                throw new FileNotFoundException("Cannot find background image");
            }

            Image image = new Image(is);
            BackgroundImage bgImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            borderPane.setBackground(new Background(bgImage));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
    }
}

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

    /**
     * Creates an error alert with the specified title, header text, and content text.
     *
     * @param title       The title of the alert.
     * @param headerText  The header text of the alert.
     * @param contentText The content text of the alert.
     * @return An error alert with the given properties.
     */
    public static Alert createErrorAlert(String title, String headerText, String contentText) {
        return createAlert(Alert.AlertType.ERROR, title, headerText, contentText);
    }

    /**
     * Creates a warning alert with the specified title, header text, and content text.
     *
     * @param title       The title of the alert.
     * @param headerText  The header text of the alert.
     * @param contentText The content text of the alert.
     * @return A warning alert with the given properties.
     */
    public static Alert createWarningAlert(String title, String headerText, String contentText) {
        return createAlert(Alert.AlertType.WARNING, title, headerText, contentText);
    }

    /**
     * Creates an information alert with the specified title, header text, and content text.
     *
     * @param title       The title of the alert.
     * @param headerText  The header text of the alert.
     * @param contentText The content text of the alert.
     * @return An information alert with the given properties.
     */
    public static Alert createInformationAlert(String title, String headerText, String contentText) {
        return createAlert(Alert.AlertType.INFORMATION, title, headerText, contentText);
    }

    /**
     * Displays a warning alert for an empty password field.
     */
    public static void showEmptyPasswordAlert() {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_password_field"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_password_field"));
        dataAlert.showAndWait();
    }

    /**
     * Displays a warning alert to indicate that a player has disconnected.
     *
     * @param dissconectedUser The user who has disconnected.
     */
    public static void showDisconnectAlert(User dissconectedUser) {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.player_disconnected"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.player_disconnected") + " " + dissconectedUser.toString());
        dataAlert.showAndWait();
    }

    /**
     * Displays an information alert to indicate that it's the user's turn.
     */
    public static void showTurnAlert() {
        Alert dataAlert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.your_turn"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.your_turn"));
        dataAlert.showAndWait();
    }

    /**
     * Displays a warning alert to indicate that a player has disconnected at the end of the game.
     *
     * @param dissconectedUser The user who has disconnected.
     */
    public static void showDisconnectEndAlert(User dissconectedUser) {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.player_disconnected_game_end"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.player_disconnected_game_end") + " " + dissconectedUser.toString());
        dataAlert.showAndWait();
    }

    /**
     * Displays an information alert to indicate that the user has logged in automatically due to an internet connection issue.
     *
     * @param automaticallyAuthenticatedUser The user who was automatically authenticated.
     */
    public static void youLoggedInAuthomaticlyDueToInternetConnectionIssue(User automaticallyAuthenticatedUser) {
        Alert dataAlert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.logged_in_automaticly"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.logged_in_automaticly") + " " + automaticallyAuthenticatedUser.toString());
        dataAlert.showAndWait();
    }

    /**
     * Displays a warning alert to indicate that a player has left the game.
     *
     * @param dissconectedUser The user who has left the game.
     */
    public static void showGameLeaveAlert(User dissconectedUser) {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.player_leave_game_end"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.player_leave_game_end") + " " + dissconectedUser.toString());
        dataAlert.showAndWait();
    }

    /**
     * Displays a warning alert for an empty login field.
     */
    public static void showEmptyLoginAlert() {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_login_field"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_login_field"));
        dataAlert.showAndWait();
    }

    /**
     * Displays a warning alert for an empty maximum count of players field.
     */
    public static void showEmptyMaxCountOfPlayersAlert() {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_max_players_field"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_max_players_field"));
        dataAlert.showAndWait();
    }

    /**
     * Displays a warning alert for an empty name field.
     */
    public static void showEmptyNameAlert() {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_name_field"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_name_field"));
        dataAlert.showAndWait();
    }

    /**
     * Displays an information alert to indicate the success of lobby creation.
     */
    public static void showSuccessLobbyCreationAlert() {
        Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_lobby_creation"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_lobby_creation"));
        alert.showAndWait();
    }

    /**
     * Displays an information alert to indicate the success of leaving a lobby.
     */
    public static void showSuccessLobbyLeaveAlert() {
        Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_lobby_leave"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_lobby_leave"));
        alert.showAndWait();
    }

    /**
     * Displays an information alert to indicate the success of deleting a lobby.
     */
    public static void showSuccessLobbyDeleteAlert() {
        Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_lobby_deletion"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_lobby_deletion"));
        alert.showAndWait();
    }

    /**
     * Displays an information alert to indicate the success of joining a lobby.
     */
    public static void showSucessLobbyJoinAlert() {
        Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_lobby_join"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_lobby_join"));
        alert.showAndWait();
    }

    /**
     * Displays a warning alert to indicate an error in the lobby deletion process.
     */
    public static void showErrorInLobbyDeleteProcessAlert() {
        Alert alert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_lobby_delete_process"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_lobby_delete_process_select"));
        alert.showAndWait();
    }

    /**
     * Displays a warning alert for an empty surname field.
     */
    public static void showEmptySurnameAlert() {
        Alert dataAlert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_surname_field"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_surname_field"));
        dataAlert.showAndWait();
    }

    /**
     * Displays an information alert to indicate the success of user registration.
     */
    public static void showSuccessRegistrationAlert() {
        Alert alert = FxUtils.createInformationAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.information"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.information_about_registration"),
                MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.information_about_registration"));
        alert.showAndWait();
    }

    /**
     * Creates and returns an Alert dialog with the specified alert type, title, header text, and content text.
     *
     * @param alertType   The type of the alert (e.g., ERROR, WARNING, INFORMATION).
     * @param title       The title of the alert dialog.
     * @param headerText  The header text displayed in the alert dialog.
     * @param contentText The main content text displayed in the alert dialog.
     * @return An Alert dialog with the specified properties.
     */
    private static Alert createAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        return alert;
    }

    /**
     * Closes the current modal window if it exists.
     *
     * @return True if the modal window was closed; false if it doesn't exist.
     */
    public static boolean closeCurrentModalWindowIfExist() {
        if (FxContainer.getCurrentModalWindow() != null) {
            FxContainer.getCurrentModalWindow().close();
            return true;
        }

        return false;
    }

    /**
     * Applies validation to the given text field, restricting input based on length and prohibited characters.
     *
     * @param textField The text field to apply validation to.
     */
    public static void applyValidation(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = createFilter(textField);
        StringConverter<String> converter = new DefaultStringConverter();
        TextFormatter<String> formatter = new TextFormatter<>(converter, "", filter);

        textField.setTextFormatter(formatter);
    }


    /**
     * Creates a filter for text input validation, restricting length and prohibiting certain characters.
     *
     * @param textField The text field to apply the filter to.
     * @return The filter for text input validation.
     */
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

    /**
     * Displays a tooltip with the specified message for the given TextField.
     *
     * @param textField The TextField to show the tooltip for.
     * @param message   The message to display in the tooltip.
     */
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

    /**
     * Hides the tooltip for the given TextField if it is currently displayed.
     *
     * @param textField The TextField to hide the tooltip for.
     */
    private static void hideTooltip(TextField textField) {
        if (textField.getTooltip() != null) {
            textField.getTooltip().hide();
        }
    }


    /**
     * Sets a background image for the given VBox.
     *
     * @param vBox The VBox to set the background image for.
     */
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

    /**
     * Sets a background image for the given BorderPane.
     *
     * @param borderPane The BorderPane to set the background image for.
     */
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

package com.aakhramchuk.clientfx.managers;

import com.aakhramchuk.clientfx.BlackJackApplication;
import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.controllers.EnterPasswordController;
import com.aakhramchuk.clientfx.objects.Lobby;
import com.aakhramchuk.clientfx.utils.FxUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class FxManager {

    /**
     * Creates a modal window with the specified scene and title.
     *
     * @param scene The JavaFX scene to display in the modal window.
     * @param title The title of the modal window.
     * @throws IOException If an I/O error occurs.
     */
    public static void createModalWindow(Scene scene, String title) throws IOException {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
            }
        });

        window.setResizable(false);
        window.setScene(scene);
        window.setTitle(title);

        FxContainer.setCurrentModalWindow(window);

        window.showAndWait();
    }

    /**
     * Creates a registration modal window, allowing users to register.
     *
     * @throws IOException If an I/O error occurs.
     */
    public static void createRegistrationModalWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(BlackJackApplication.class.getResource("registration.fxml"));

        createModalWindow(new Scene(loader.load()), "Registration");
    }

    /**
     * Creates a modal window for entering a password to join a lobby.
     *
     * @param lobby   The lobby for which the password is required.
     * @param isJoin  Indicates if the user is attempting to join the lobby (true) or create it (false).
     * @throws IOException If an I/O error occurs.
     */
    public static void createEnterPasswordModalWindow(Lobby lobby, boolean isJoin) throws IOException {
        FXMLLoader loader = new FXMLLoader(BlackJackApplication.class.getResource("enterPassword.fxml"));
        Parent root = loader.load();
        EnterPasswordController enterPasswordController = loader.getController();
        enterPasswordController.setLobby(lobby);
        enterPasswordController.setIsJoin(isJoin);

        createModalWindow(new Scene(root), "Enter password");
    }

    /**
     * Creates a modal window for creating a lobby.
     *
     * @throws IOException If an I/O error occurs.
     */
    public static void createLobbyCreationModalWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(BlackJackApplication.class.getResource("lobbyCreation.fxml"));

        createModalWindow(new Scene(loader.load()), "Lobby creation");
    }

    /**
     * Changes the current scene to the game end scene.
     *
     * @throws IOException If an I/O error occurs.
     */
    public static void changeCurrentSceneToGameEndScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(BlackJackApplication.class.getResource("gameEnd.fxml"));
        FxContainer.setCurrentScene(new Scene(loader.load(), 800, 600));

        Scene newScene = FxContainer.getCurrentScene();
        Stage currentStage = FxContainer.getCurrentStage();

        currentStage.setScene(newScene);

        currentStage.sizeToScene();
        currentStage.setResizable(false);
        currentStage.setMinWidth(800);
        currentStage.setMinHeight(500);
    }

    /**
     * Retrieves the main menu scene.
     *
     * @return The main menu scene.
     * @throws IOException If an I/O error occurs.
     */
    public static Scene getMainMenuScene() throws IOException {
        FXMLLoader fxmlMain = new FXMLLoader(BlackJackApplication.class.getResource("mainMenu.fxml"));
        FxContainer.setCurrentScene(new Scene(fxmlMain.load(), 800, 500));
        return FxContainer.getCurrentScene();
    }

    /**
     * Retrieves the game scene.
     *
     * @return The game scene.
     * @throws IOException If an I/O error occurs.
     */
    public static Scene getGameScene() throws IOException {
        FXMLLoader fxmlMain = new FXMLLoader(BlackJackApplication.class.getResource("game.fxml"));
        FxContainer.setCurrentScene(new Scene(fxmlMain.load(), FxContainer.getCurrentScene().getWidth(), FxContainer.getCurrentScene().getHeight()));
        return FxContainer.getCurrentScene();
    }

    /**
     * Changes the current scene to the game scene.
     *
     * @throws IOException If an I/O error occurs.
     */
    public static void changeCurrentSceneToGameScene() throws IOException {
        FxContainer.getCurrentStage().setScene(getGameScene());
        FxContainer.getCurrentStage().setResizable(false);
        FxContainer.getCurrentStage().setMinWidth(1240);
        FxContainer.getCurrentStage().setMinHeight(900);
    }

    /**
     * Changes the current scene to the main menu scene.
     *
     * @throws IOException If an I/O error occurs.
     */
    public static void changeCurrentSceneToMainMenuScene() throws IOException {
        Scene newScene = getMainMenuScene();
        Stage currentStage = FxContainer.getCurrentStage();

        currentStage.setScene(newScene);

        currentStage.sizeToScene();
        currentStage.setResizable(false);
        currentStage.setMinWidth(800);
        currentStage.setMinHeight(500);
    }

    /**
     * Changes the current scene to the login scene.
     *
     * @throws IOException If an I/O error occurs.
     */
    public static void changeCurrentSceneToLoginScene() throws IOException {
        Scene newScene = getLoginScene();
        Stage currentStage = FxContainer.getCurrentStage();

        currentStage.setScene(newScene);

        currentStage.sizeToScene();
        currentStage.setResizable(false);
        currentStage.setMinWidth(800);
        currentStage.setMinHeight(500);
    }


    /**
     * Retrieves the lobby menu scene with the specified width and height.
     *
     * @param width  The width of the lobby menu scene.
     * @param height The height of the lobby menu scene.
     * @return The lobby menu scene.
     * @throws IOException If an I/O error occurs.
     */
    public static Scene getLobbyMenuScene(double width, double height) throws IOException {
        FXMLLoader fxmlMain = new FXMLLoader(BlackJackApplication.class.getResource("lobbyMenu.fxml"));
        FxContainer.setCurrentScene(new Scene(fxmlMain.load(), width, height));
        return FxContainer.getCurrentScene();
    }

    /**
     * Changes the current scene to the lobby scene.
     *
     * @throws IOException If an I/O error occurs.
     */
    public static void changeCurrentSceneToLobbyScene() throws IOException {
        Scene newScene = getLobbyMenuScene(800, 500);
        Stage currentStage = FxContainer.getCurrentStage();

        currentStage.setScene(newScene);

        currentStage.sizeToScene();
        currentStage.setResizable(false);
        currentStage.setMinWidth(800);
        currentStage.setMinHeight(500);
    }

    /**
     * Retrieves the login scene.
     *
     * @return The login scene.
     * @throws IOException If an I/O error occurs.
     */
    public static Scene getLoginScene() throws IOException {
        FXMLLoader fxmlLogin = new FXMLLoader(BlackJackApplication.class.getResource("login.fxml"));
        FxContainer.setCurrentScene(new Scene(fxmlLogin.load(), 800, 500));
        return FxContainer.getCurrentScene();
    }
}

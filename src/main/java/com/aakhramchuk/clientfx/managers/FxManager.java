package com.aakhramchuk.clientfx.managers;

import com.aakhramchuk.clientfx.BlackJackApplication;
import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.controllers.EnterPasswordController;
import com.aakhramchuk.clientfx.objects.Lobby;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class FxManager {

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

    public static void createRegistrationModalWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(BlackJackApplication.class.getResource("registration.fxml"));

        createModalWindow(new Scene(loader.load()), "Registration");
    }

    public static void createEnterPasswordModalWindow(Lobby lobby, boolean isJoin) throws IOException {
        FXMLLoader loader = new FXMLLoader(BlackJackApplication.class.getResource("enterPassword.fxml"));
        Parent root = loader.load();
        EnterPasswordController enterPasswordController = loader.getController();
        enterPasswordController.setLobby(lobby);
        enterPasswordController.setIsJoin(isJoin);

        createModalWindow(new Scene(root), "Enter password");
    }


    public static void createLobbyCreationModalWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(BlackJackApplication.class.getResource("lobbyCreation.fxml"));

        createModalWindow(new Scene(loader.load()), "Lobby creation");
    }

    public static Scene getMainMenuScene() throws IOException {
        FXMLLoader fxmlMain = new FXMLLoader(BlackJackApplication.class.getResource("mainMenu.fxml"));
        FxContainer.setCurrentScene(new Scene(fxmlMain.load(), FxContainer.getCurrentScene().getWidth(), FxContainer.getCurrentScene().getHeight()));
        return FxContainer.getCurrentScene();
    }

    public static Scene getLoginScene() throws IOException {
        FXMLLoader fxmlLogin = new FXMLLoader(BlackJackApplication.class.getResource("login.fxml"));
        FxContainer.setCurrentScene(new Scene(fxmlLogin.load(), 800, 500));
        return FxContainer.getCurrentScene();
    }
}

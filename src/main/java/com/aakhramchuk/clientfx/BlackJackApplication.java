package com.aakhramchuk.clientfx;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.ConnectionObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BlackJackApplication extends Application {

    private static final Logger logger = LogManager.getLogger(BlackJackApplication.class);

    @Override
    public void start(Stage stage) throws IOException {
        stage.setResizable(false);
        stage.setScene(FxManager.getLoginScene());
        stage.setOnCloseRequest(event -> {
            closeApplication();
        });
        stage.show();

        FxContainer.setCurrentStage(stage);
    }

    public static void main(String[] args) {
        connectToServerAndRunApplication();
    }

    public static void closeApplication() {
        // Stop JavaFX application
        Platform.exit();
        // Terminate JVM
        System.exit(0);
    }

    private static void connectToServerAndRunApplication() {
        Configurations configs = new Configurations();

        try {
            Configuration config = configs.properties("config.properties");
            String hostname = config.getString("server.hostname");
            int port = config.getInt("server.port");

            try (Socket socket = new Socket(hostname, port);
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 Scanner scanner = new Scanner(System.in)) {

                MainContainer.setConnectionObject(new ConnectionObject(writer, reader, scanner, config));
                startServerListener();
                launch();

            } catch (IOException ex) {
                logger.error("Client Exception: ", ex);
            }
        } catch (Exception e) {
            logger.error("Configuration error: " + e.getMessage(), e);
        }

    }
    public static void startServerListener() {
        Thread listenerThread = new Thread(BlackJackApplication::listenToServer);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private static void listenToServer() {
        try {
            String messageFromServer;
            while ((messageFromServer = MainContainer.getConnectionObject().getReader().readLine()) != null) {
                if (MainContainer.isAwaitingResponse()) {
                    MainContainer.getMessageQueue().put(messageFromServer);
                    MainContainer.setAwaitingResponse(false);
                } else {
//                    if (inGame) {
//                        if (LobbyManager.getCurrentLobby().getGameObject() == null) {
//                            gameQueue.put(messageFromServer);
//                        } else {
//                            if (!ourTurnEvaluated) {
//                                gameQueue.put(messageFromServer);
//                            } else {
//                                logger.info("RECEIVED PING: " + messageFromServer);
//                                handleServerMessage(messageFromServer, "message.game_action_opcode");
//                            }
//                        }
//                    } else {
//                        logger.info("RECEIVED PING: " + messageFromServer);
//                        handleServerMessage(messageFromServer, "message.lobby_create_opcode");
//                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error reading from server: ", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Listener thread interrupted", e);
        }
    }
}
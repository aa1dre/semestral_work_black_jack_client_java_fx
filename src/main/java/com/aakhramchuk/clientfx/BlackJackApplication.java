package com.aakhramchuk.clientfx;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.ConnectionObject;
import com.aakhramchuk.clientfx.utils.ServerUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
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

    /**
     * The start method is called when the JavaFX application is launched.
     * It initializes the main application window and sets the initial scene to the login scene.
     *
     * @param stage The primary stage for this application.
     * @throws IOException If an I/O error occurs.
     */
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

    /**
     * The main method of the application.
     * It is the entry point of the application and is called when the program starts.
     * It initiates the connection to the server and starts the application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        connectToServerAndRunApplication();
    }

    /**
     * Closes the application.
     * It stops any scheduler services and exits the JavaFX application.
     */
    public static void closeApplication() {
        ServerUtils.stopSchedulerServices();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Connects to the server and runs the application.
     * It reads the server configuration from the "config.properties" file, establishes a socket connection,
     * and sets up the necessary components for communication with the server.
     */
    private static void connectToServerAndRunApplication() {
        logger.debug("Receiving configuration from config.properties");
        Configurations configs = new Configurations();
        logger.debug("Configuration received successfully");


        try {
            Configuration config = configs.properties("config.properties");
            String hostname = config.getString("server.hostname");
            int port = config.getInt("server.port");
            logger.debug("Connecting to server: " + hostname + ":" + port + "");
            try (Socket socket = new Socket(hostname, port);
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 Scanner scanner = new Scanner(System.in)) {

                MainContainer.setConnectionObject(new ConnectionObject(socket, writer, reader, scanner, config));

                ServerUtils.tryPingServer();

                MainContainer.setConnected(true);

                ServerUtils.startServerListener();

                ServerUtils.startProcessingOutgoingMessages();

                launch();

            } catch (IOException ex) {
                logger.error("Client Exception: ", ex);
            }
        } catch (Exception e) {
            logger.error("Configuration error: " + e.getMessage(), e);
        }

    }

}
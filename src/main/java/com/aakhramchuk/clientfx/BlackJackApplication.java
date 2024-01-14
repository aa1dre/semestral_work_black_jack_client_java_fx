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
        ServerUtils.stopSchedulerServices();
        Platform.exit();
        System.exit(0);
    }

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
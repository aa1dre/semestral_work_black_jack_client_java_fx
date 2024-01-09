package com.aakhramchuk.clientfx;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.ConnectionObject;
import com.aakhramchuk.clientfx.objects.LobbyManager;
import com.aakhramchuk.clientfx.utils.Utils;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BlackJackApplication extends Application {

    private static ScheduledExecutorService scheduler;
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
        if (scheduler != null) {
            scheduler.shutdown();
        }
        Platform.exit();
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

                MainContainer.setConnectionObject(new ConnectionObject(socket, writer, reader, scanner, config));

                startServerListener();

                Thread outgoingMessageProcessorThread = new Thread(BlackJackApplication::processOutgoingMessages);
                outgoingMessageProcessorThread.setDaemon(true);
                outgoingMessageProcessorThread.start();

                startPingService();
                startPingResponseCheck();

                launch();

            } catch (IOException ex) {
                logger.error("Client Exception: ", ex);
            }
        } catch (Exception e) {
            logger.error("Configuration error: " + e.getMessage(), e);
        }

    }

    private static void closeCurrentConnection() {
        try {
            ConnectionObject currentConnection = MainContainer.getConnectionObject();
            if (currentConnection.getSocket() != null && !currentConnection.getSocket().isClosed()) {
                currentConnection.getSocket().close();
            }
        } catch (IOException e) {
            logger.error("Error closing the current connection", e);
        }
    }

    public static void startServerListener() {
        Thread listenerThread = new Thread(BlackJackApplication::listenToServer);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private static void processOutgoingMessages() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String message = MainContainer.getOutgoingMessageQueue().take();
                ConnectionObject connectionObject = MainContainer.getConnectionObject();
                connectionObject.getWriter().print(message);
                connectionObject.getWriter().flush();
                logger.info("SENT: " + message);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Outgoing message processor thread interrupted", e);
        }
    }

    private static void startPingService() {
        String pingOpcode = MainContainer.getConnectionObject().getConfig().getString("message.ping_opcode");
        String pingCommand = MainContainer.getConnectionObject().getConfig().getString("message.ping_command");
        Configuration config = MainContainer.getConnectionObject().getConfig();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (MainContainer.isConnected()) {
                    logger.info("PING");
                    MainContainer.getOutgoingMessageQueue().put(Utils.createMessage(config, pingOpcode, pingCommand));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Ping Service interrupted", e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private static void attemptReconnect() {
        int maxReconnectAttempts = MainContainer.getConnectionObject().getConfig().getInt("server.reconnect_attempts");

        for (int attempt = 0; attempt < maxReconnectAttempts; attempt++) {
            try {
                closeCurrentConnection();

                String hostname = MainContainer.getConnectionObject().getConfig().getString("server.hostname");
                int port = MainContainer.getConnectionObject().getConfig().getInt("server.port");
                Socket newSocket = new Socket(hostname, port);
                PrintWriter newWriter = new PrintWriter(newSocket.getOutputStream(), true);
                BufferedReader newReader = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));

                ConnectionObject newConnection = new ConnectionObject(newSocket, newWriter, newReader, MainContainer.getConnectionObject().getScanner(), MainContainer.getConnectionObject().getConfig());
                MainContainer.setConnectionObject(newConnection);

                MainContainer.getOutgoingMessageQueue().clear();

                startServerListener();

                logger.info("Reconnected to the server (Attempt " + (attempt + 1) + ")");
                MainContainer.setConnected(true);
                return;
            } catch (Exception e) {
                logger.error("Reconnect attempt " + (attempt + 1) + " failed: ", e);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    if (scheduler != null) {
                        scheduler.shutdownNow();
                    }
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        if (scheduler != null) {
            scheduler.shutdownNow();
        }

        MainContainer.setConnected(false);
    }


    private static void startPingResponseCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();

            if ((currentTime - MainContainer.getLastPingResponseTime()) > 10000 && MainContainer.isConnected()) {
                logger.warn("No ping response from server for 5 seconds. Attempting to reconnect.");
                attemptReconnect();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private static void listenToServer() {
        String pongResponse = MainContainer.getConnectionObject().getConfig().getString("message.pong_response");
        try {
            String messageFromServer;
                while ((messageFromServer = MainContainer.getConnectionObject().getReader().readLine()) != null) {
                if (MainContainer.isAwaitingResponse()) {
                    MainContainer.getIncomingMessageQueue().put(messageFromServer);
                    MainContainer.setAwaitingResponse(false);
                } else if (messageFromServer.equals(pongResponse)) {
                    MainContainer.updateLastPingResponseTime();
                    logger.info("RECEIVED: " + messageFromServer);
                    logger.info("PONG");
                } else {
                    if (MainContainer.isInGame()) {
                        if (LobbyManager.getCurrentLobby().getGameObject() == null) {
                            MainContainer.getGameQueue().put(messageFromServer);
                        } else {
                            if (!MainContainer.isOurTurnEvaluated()) {
                                MainContainer.getGameQueue().put(messageFromServer);
                            } else {
                                logger.info("RECEIVED PING: " + messageFromServer);
                                Utils.handleServerMessage(messageFromServer, "message.game_action_opcode");
                            }
                        }
                    } else {
                        logger.info("RECEIVED PING: " + messageFromServer);
                        Utils.handleServerMessage(messageFromServer, "message.lobby_create_opcode");
                    }
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
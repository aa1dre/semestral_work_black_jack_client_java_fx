package com.aakhramchuk.clientfx.utils;

import com.aakhramchuk.clientfx.BlackJackApplication;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.objects.ConnectionObject;
import com.aakhramchuk.clientfx.objects.LobbyManager;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerUtils {

    private static ScheduledExecutorService scheduler;

    private static final Logger logger = LogManager.getLogger(ServerUtils.class);

    public static void startPingService() {
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

    public static void startPingResponseCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();

            if ((currentTime - MainContainer.getLastPingResponseTime()) > 10000 && MainContainer.isConnected()) {
                logger.warn(currentTime - MainContainer.getLastPingResponseTime());
                logger.warn(currentTime);
                logger.warn(MainContainer.getLastPingResponseTime());
                logger.warn("No ping response from server for 8 seconds. Attempting to reconnect.");
                attemptReconnect();
            }
        }, 2, 10000, TimeUnit.SECONDS);
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
                if (maxReconnectAttempts/2 < attempt) {

                }
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

    public static void startProcessingOutgoingMessages() {
        Thread outgoingMessageProcessorThread = new Thread(ServerUtils::processOutgoingMessages);
        outgoingMessageProcessorThread.setDaemon(true);
        outgoingMessageProcessorThread.start();
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

    public static void startSchedulerServices() {
        startPingService();
        startPingResponseCheck();
    }

    public static void stopSchedulerServices() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    public static void startServerListener() {
        Thread listenerThread = new Thread(ServerUtils::listenToServer);
        listenerThread.setDaemon(true);
        listenerThread.start();
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
                            logger.info("RECEIVED PING: " + messageFromServer);
                            MainContainer.getGameQueue().put(messageFromServer);
                        } else {
                            if (!MainContainer.isOurTurnEvaluated()) {
                                logger.info("RECEIVED PING: " + messageFromServer);
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

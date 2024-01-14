package com.aakhramchuk.clientfx.utils;

import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.ConnectionObject;
import com.aakhramchuk.clientfx.objects.LobbyManager;
import javafx.application.Platform;
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
import java.util.concurrent.atomic.AtomicInteger;

public class ServerUtils {

    private static ScheduledExecutorService schedulerPing = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledExecutorService schedulerPong = Executors.newSingleThreadScheduledExecutor();
    private static boolean isPingServiceRunning = false;
    private static boolean isPingResponseCheckRunning = false;

    private static final Logger logger = LogManager.getLogger(ServerUtils.class);
    private static Thread serverListenerThread;
    private static Thread outgoingMessageProcessorThread;

    public static void stopServerListener() {
        if (serverListenerThread != null) {
            serverListenerThread.interrupt(); // Прервите поток, если он ожидает ввода
            try {
                serverListenerThread.join(); // Дождитесь, пока поток завершится
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting for server listener thread to finish", e);
            }
        }
    }

    public static void stopOutgoingMessageProcessor() {
        if (outgoingMessageProcessorThread != null) {
            outgoingMessageProcessorThread.interrupt(); // Прервите поток, если он ожидает ввода
            try {
                outgoingMessageProcessorThread.join(); // Дождитесь, пока поток завершится
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting for outgoing message processor thread to finish", e);
            }
        }
    }

    public static void startPingService() {
        if (schedulerPing == null || schedulerPing.isShutdown()) {
            schedulerPing = Executors.newSingleThreadScheduledExecutor();
        }

        if (!isPingServiceRunning) {
            String pingOpcode = MainContainer.getConnectionObject().getConfig().getString("message.ping_opcode");
            String pingCommand = MainContainer.getConnectionObject().getConfig().getString("message.ping_command");
            Configuration config = MainContainer.getConnectionObject().getConfig();

            schedulerPing.scheduleAtFixedRate(() -> {
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

            isPingServiceRunning = true;
        }
    }

    public static void startPingResponseCheck() {
        if (schedulerPong == null || schedulerPong.isShutdown()) {
            schedulerPong = Executors.newSingleThreadScheduledExecutor();
        }

        if (!isPingResponseCheckRunning) {
            schedulerPong.scheduleAtFixedRate(() -> {
                long currentTime = System.currentTimeMillis();

                if ((currentTime - MainContainer.getLastPingResponseTime()) > 10000 && MainContainer.isConnected()) {
                    logger.warn(currentTime - MainContainer.getLastPingResponseTime());
                    logger.warn(currentTime);
                    logger.warn(MainContainer.getLastPingResponseTime());
                    logger.warn("No ping response from server for 8 seconds. Attempting to reconnect.");
                    Platform.runLater(() -> {
                        try {
                            FxManager.changeCurrentSceneToLoginScene();
                        } catch (IOException e) {
                            logger.error("Error while attempting to reconnect", e);
                        }
                    });
                    attemptReconnect();
                }
            }, 2, 10, TimeUnit.SECONDS);

            isPingResponseCheckRunning = true;
        }
    }

    private static void attemptReconnect() {
        int maxReconnectAttempts = MainContainer.getConnectionObject().getConfig().getInt("server.reconnect_attempts");

           for (int attempt = 0; attempt < maxReconnectAttempts; attempt++) {
               try {
                   stopPingService();
                   stopServerListener();
                   stopOutgoingMessageProcessor();
                   closeCurrentConnection();
                   MainContainer.getOutgoingMessageQueue().clear();
                   MainContainer.getIncomingMessageQueue().clear();
                   MainContainer.getGameQueue().clear();
                   MainContainer.setConnected(false);

                   String hostname = MainContainer.getConnectionObject().getConfig().getString("server.hostname");
                   int port = MainContainer.getConnectionObject().getConfig().getInt("server.port");
                   Socket newSocket = new Socket(hostname, port);
                   PrintWriter newWriter = new PrintWriter(newSocket.getOutputStream(), true);
                   BufferedReader newReader = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));

                   ConnectionObject newConnection = new ConnectionObject(newSocket, newWriter, newReader, MainContainer.getConnectionObject().getScanner(), MainContainer.getConnectionObject().getConfig());
                   MainContainer.setConnectionObject(newConnection);
                   MainContainer.setConnected(true);

                   startProcessingOutgoingMessages();
                   startServerListener();

                   logger.info("Reconnected to the server (Attempt " + (attempt) + ")");
                   if (maxReconnectAttempts / 2 >= attempt) {
                       logger.info("Automatic login attempt (Attempt " + (attempt) + ")");
                       Platform.runLater(() -> {
                           try {
                               ActionUtils.login(true);
                           } catch (IOException e) {
                               logger.error("Error while attempting to login automatically", e);
                           } catch (InterruptedException e) {
                               logger.error("Interrupted while attempting to login automatically", e);
                           }
                       });
                   } else {
                       MainContainer.setUser(null);
                   }
                   return;
               } catch (Exception e) {
                   MainContainer.setConnected(false);
                   logger.error("Reconnect attempt " + (attempt) + " failed: ", e);
               }

               try {
                   Thread.sleep(8000);
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
                   logger.error("Interrupted while attempting to reconnect", e);
               }
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

    public static void startProcessingOutgoingMessages() {
        if (outgoingMessageProcessorThread == null || outgoingMessageProcessorThread.isInterrupted()) {
            outgoingMessageProcessorThread = new Thread(ServerUtils::processOutgoingMessages);
            outgoingMessageProcessorThread.setDaemon(true);
            outgoingMessageProcessorThread.start();
        }
    }

    private static void processOutgoingMessages() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (!MainContainer.getOutgoingMessageQueue().isEmpty()) {
                    String message = MainContainer.getOutgoingMessageQueue().take();
                    ConnectionObject connectionObject = MainContainer.getConnectionObject();
                    connectionObject.getWriter().print(message);
                    connectionObject.getWriter().flush();
                    logger.info("SENT: " + message);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Outgoing message processor thread interrupted", e);
        }
    }

    public static void startSchedulerServices() {
        startPingService();
        startPingResponseCheck();
    }

    public static void stopSchedulerServices() {
        stopPingService();
        stopPongService();
    }

    public static void stopPingService() {
        if (schedulerPing != null) {
            schedulerPing.shutdownNow();
            isPingServiceRunning = false;
        }
    }

    public static void stopPongService() {
        if (schedulerPong != null) {
            schedulerPong.shutdownNow();
            isPingResponseCheckRunning = false;
        }
    }

    public static void startServerListener() {
        serverListenerThread = new Thread(ServerUtils::listenToServer);
        serverListenerThread.setDaemon(true);
        serverListenerThread.start();
    }

    private static void listenToServer() {
        String pongResponse = MainContainer.getConnectionObject().getConfig().getString("message.pong_response");
        try {
            String messageFromServer;
            while (!Thread.currentThread().isInterrupted() && (messageFromServer = MainContainer.getConnectionObject().getReader().readLine()) != null) {
                logger.info("RECEIVED PING: " + messageFromServer);
                if (MainContainer.isAwaitingResponse()) {
                    MainContainer.getIncomingMessageQueue().put(messageFromServer);
                    MainContainer.setAwaitingResponse(false);
                } else if (messageFromServer.equals(pongResponse)) {
                    MainContainer.updateLastPingResponseTime();
                    logger.info("PONG");
                } else {
                    if (MainContainer.isInGame()) {
                        if (LobbyManager.getCurrentLobby().getGameObject() == null || !MainContainer.isOurTurnEvaluated()) {
                            MainContainer.getGameQueue().put(messageFromServer);
                        } else {
                            Utils.handleServerMessage(messageFromServer, "message.game_action_opcode");
                        }
                    } else {
                        Utils.handleServerMessage(messageFromServer, "message.lobby_create_opcode");
                    }
                }
            }
        } catch (IOException e) {
            if (MainContainer.isConnected()) {
                logger.error("Error reading from server: ", e);
            } else {
                logger.info("Listener thread stopped because listening was set to false");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Listener thread interrupted", e);
        }
    }
}

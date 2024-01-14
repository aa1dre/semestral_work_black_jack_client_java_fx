package com.aakhramchuk.clientfx.utils;

import com.aakhramchuk.clientfx.BlackJackApplication;
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerUtils {

    // Scheduler services for ping and pong functionalities
    private static ScheduledExecutorService schedulerPing = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledExecutorService schedulerPong = Executors.newSingleThreadScheduledExecutor();
    private static boolean isPingServiceRunning = false;
    private static boolean isPingResponseCheckRunning = false;

    private static final Logger logger = LogManager.getLogger(ServerUtils.class);

    // Threads for listening to the server and processing outgoing messages
    private static Thread serverListenerThread;
    private static Thread outgoingMessageProcessorThread;


    /**
     * Stops the server listener thread.
     * This method interrupts the serverListenerThread if it's waiting for input and joins it until it finishes.
     */
    public static void stopServerListener() {
        if (serverListenerThread != null) {
            serverListenerThread.interrupt(); // Interrupt the thread if it's waiting for input
            try {
                serverListenerThread.join(); // Wait for the thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting for server listener thread to finish", e);
            }
        }
    }

    /**
     * Stops the outgoing message processor thread.
     * This method interrupts the outgoingMessageProcessorThread if it's waiting for input and joins it until it finishes.
     */
    public static void stopOutgoingMessageProcessor() {
        if (outgoingMessageProcessorThread != null) {
            outgoingMessageProcessorThread.interrupt(); // Interrupt the thread if it's waiting for input
            try {
                outgoingMessageProcessorThread.join(); // Wait for the thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting for outgoing message processor thread to finish", e);
            }
        }
    }

    /**
     * Starts the ping service to regularly send ping messages to the server.
     * If the ping service is not already running, it schedules a task to send ping messages at fixed intervals.
     */
    public static void startPingService() {
        if (schedulerPing == null || schedulerPing.isShutdown()) {
            schedulerPing = Executors.newSingleThreadScheduledExecutor();
        }

        if (!isPingServiceRunning) {
            String pingOpcode = MainContainer.getConnectionObject().getConfig().getString("message.ping_opcode");
            String pingCommand = MainContainer.getConnectionObject().getConfig().getString("message.ping_command");
            Configuration config = MainContainer.getConnectionObject().getConfig();

            // Schedule a task to send ping messages at fixed intervals
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

    /**
     * Starts the ping response check service.
     * This service regularly checks if the ping response has been received within a certain time frame and attempts reconnection if necessary.
     */
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
                    try {
                        attemptReconnect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, 2, 10, TimeUnit.SECONDS);

            isPingResponseCheckRunning = true;
        }
    }

    /**
     * Attempts to reconnect to the server with a specified number of attempts.
     * This method manages the reconnection logic including stopping existing connections, clearing queues, and re-establishing new connections.
     * @throws InterruptedException If the thread is interrupted while sleeping between reconnect attempts.
     */
    private static void attemptReconnect() throws InterruptedException {
        int maxReconnectAttempts = MainContainer.getConnectionObject().getConfig().getInt("server.reconnect_attempts");
        for (int attempt = 1; attempt <= maxReconnectAttempts; attempt++) {
            try {
                // Stop existing connections
                stopServerListener();
                stopPingService();
                stopOutgoingMessageProcessor();
                closeCurrentConnection();
                MainContainer.getOutgoingMessageQueue().clear();
                MainContainer.getIncomingMessageQueue().clear();
                MainContainer.getGameQueue().clear();
                MainContainer.setConnected(false);

                String hostname = MainContainer.getConnectionObject().getConfig().getString("server.hostname");
                int port = MainContainer.getConnectionObject().getConfig().getInt("server.port");

                InetAddress serverAddr = InetAddress.getByName(hostname);
                if (serverAddr.isReachable(5000)) { // Check if the server is reachable
                    Socket newSocket = new Socket();
                    newSocket.connect(new InetSocketAddress(hostname, port), 5000);

                    PrintWriter newWriter = new PrintWriter(newSocket.getOutputStream(), true);
                    BufferedReader newReader = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));

                    // Create a new connection object
                    ConnectionObject newConnection = new ConnectionObject(newSocket, newWriter, newReader, MainContainer.getConnectionObject().getScanner(), MainContainer.getConnectionObject().getConfig());
                    MainContainer.setConnectionObject(newConnection);

                    // Send ping message to server
                    String pingOpcode = MainContainer.getConnectionObject().getConfig().getString("message.ping_opcode");
                    String pingCommand = MainContainer.getConnectionObject().getConfig().getString("message.ping_command");
                    Configuration config = MainContainer.getConnectionObject().getConfig();
                    String pongResponse = MainContainer.getConnectionObject().getConfig().getString("message.pong_response");
                    String pingMessage = Utils.createMessage(config, pingOpcode, pingCommand);

                    logger.info("Sending ping to server : " + pingMessage);

                    Thread.sleep(250);
                    newWriter.print(pingMessage);
                    newWriter.flush();
                    Thread.sleep(250);

                    String response = newReader.readLine();
                    if (response != null && response.equals(pongResponse)) {
                        MainContainer.setConnected(true);
                        startProcessingOutgoingMessages();
                        startServerListener();
                        logger.info("Reconnected to the server (Attempt " + attempt + ")");
                        handleLoginAttempt(attempt);
                        // Stop the ping response check service and restart it in login attempt
                        stopPongService();
                        break;
                    } else {
                        // No pong response from server, connection failed
                        MainContainer.setConnected(false);
                        logger.error("No pong response from server, connection failed.");
                        handleFailedConnection(attempt, maxReconnectAttempts);
                        Thread.sleep(5000);
                    }
                } else {
                    // Server is not reachable
                    logger.error("Server is not reachable.");
                    handleFailedConnection(attempt, maxReconnectAttempts);
                    Thread.sleep(5000);
                }
            } catch (IOException e) {
                // Connection failed
                MainContainer.setConnected(false);
                logger.error("Reconnect attempt " + attempt + " failed: ");
                handleFailedConnection(attempt, maxReconnectAttempts);
                Thread.sleep(5000);
            }
        }
    }

    /**
     * Handles automatic login attempts after a successful reconnection.
     * This method logs an automatic login attempt and runs the login action on the JavaFX application thread.
     * @param attempt The current attempt number for the reconnection.
     * @throws InterruptedException If the thread is interrupted during the process.
     */
    private static void handleLoginAttempt(int attempt) throws InterruptedException {
            // Attempt to login automatically
            logger.info("Automatic login attempt (Attempt " + attempt + ")");
            Platform.runLater(() -> {
                try {
                    ActionUtils.login(true);
                } catch (IOException | InterruptedException e) {
                    logger.error("Error while attempting to login automatically");
                }
            });
    }

    /**
     * Handles failed connection scenarios.
     * This method logs an error message and closes the application if the maximum number of reconnection attempts is reached.
     * @param attempt The current attempt number for the reconnection.
     * @param maxReconnectAttempts The maximum number of reconnection attempts.
     */
    private static void handleFailedConnection(int attempt, int maxReconnectAttempts) {
        if (attempt >= maxReconnectAttempts) {
            logger.error("Failed to reconnect to the server after " + attempt + " attempts. Closing the application.");
            BlackJackApplication.closeApplication();
        }
    }

    /**
     * Closes the current connection to the server.
     * This method safely closes the socket and its associated I/O streams.
     */
    private static void closeCurrentConnection() {
        try {
            ConnectionObject currentConnection = MainContainer.getConnectionObject();
            if (currentConnection != null) {
                if (currentConnection.getSocket() != null) {
                    if (!currentConnection.getSocket().isClosed()) {
                        if (currentConnection.getWriter() != null) {
                            currentConnection.getWriter().close();
                        }
                        if (currentConnection.getReader() != null) {
                            currentConnection.getReader().close();
                        }

                        currentConnection.getSocket().close();
                        logger.info("Connection successfully closed.");
                    } else {
                        logger.info("Socket is already closed.");
                    }
                } else {
                    logger.warn("Socket is null.");
                }
            } else {
                logger.warn("No current connection to close.");
            }
        } catch (IOException e) {
            logger.error("Error closing the current connection", e);
        }
    }

    /**
     * Starts processing outgoing messages from a queue.
     * This method creates and starts a thread for processing and sending messages from the outgoing message queue.
     */
    public static void startProcessingOutgoingMessages() {
        if (outgoingMessageProcessorThread == null || outgoingMessageProcessorThread.isInterrupted()) {
            outgoingMessageProcessorThread = new Thread(ServerUtils::processOutgoingMessages);
            outgoingMessageProcessorThread.setDaemon(true);
            outgoingMessageProcessorThread.start();
        }
    }

    /**
     * Processes outgoing messages.
     * This method continuously takes messages from the outgoing queue and sends them through the current connection.
     */
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

    /**
     * Starts services for sending ping messages to the server and checking for ping responses.
     */
    public static void startSchedulerServices() {
        startPingService();
        startPingResponseCheck();
    }

    /**
     * Stops services for sending ping messages to the server and checking for ping responses.
     */
    public static void stopSchedulerServices() {
        stopPingService();
        stopPongService();
    }

    /**
     * Stops the ping response check service if it is running.
     */
    public static void stopPingService() {
        if (schedulerPing != null) {
            schedulerPing.shutdownNow();
            isPingServiceRunning = false;
        }
    }

    /**
     * Stops the ping response check service if it is running.
     */
    public static void stopPongService() {
        if (schedulerPong != null) {
            schedulerPong.shutdownNow();
            isPingResponseCheckRunning = false;
        }
    }

    /**
     * Starts a server listener thread for listening to incoming messages from the server.
     */
    public static void startServerListener() {
        serverListenerThread = new Thread(ServerUtils::listenToServer);
        serverListenerThread.setDaemon(true);
        serverListenerThread.start();
    }

    /**
     * Listens to the server for incoming messages.
     * This method continuously reads messages from the server and processes them based on their type and current application state.
     * It distinguishes between different types of server messages such as pong responses, game-related messages, or lobby creation messages.
     *
     * The method operates within a loop that continues until the serverListenerThread is interrupted. It reads messages from the server's input stream. Each message is logged and then processed:
     * 1. If the application is awaiting a response (isAwaitingResponse is true), the message is added to the incoming message queue and the awaiting response flag is reset.
     * 2. If the message is a pong response, it updates the last ping response time.
     * 3. If the message is not a pong response, it checks if the application is currently in a game state.
     *    a. If in a game and certain conditions are met, the message is added to the game queue.
     *    b. Otherwise, the message is processed for game actions or lobby creation, depending on the application's state.
     *
     * The method handles IOExceptions that might occur during reading from the server, logging appropriate error or info messages.
     * InterruptedExceptions are also caught and handled to ensure the thread is correctly interrupted.
     */
    private static void listenToServer() {
        String pongResponse = MainContainer.getConnectionObject().getConfig().getString("message.pong_response");
        try {
            String messageFromServer;
            while (!serverListenerThread.isInterrupted() && (messageFromServer = MainContainer.getConnectionObject().getReader().readLine()) != null) {
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

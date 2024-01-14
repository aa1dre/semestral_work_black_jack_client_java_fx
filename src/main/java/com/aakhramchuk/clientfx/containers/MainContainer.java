package com.aakhramchuk.clientfx.containers;

import com.aakhramchuk.clientfx.objects.ConnectionObject;
import com.aakhramchuk.clientfx.objects.User;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class MainContainer {
    private static User user;
    private static final Object userLock = new Object();
    private static final BlockingQueue<String> incomingMessageQueue = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> outgoingMessageQueue = new ArrayBlockingQueue<>(100);
    private static final AtomicLong lastPingResponseTime = new AtomicLong(System.currentTimeMillis());
    private static final BlockingQueue<String> gameQueue = new ArrayBlockingQueue<>(100);

    private static final Object inSelectLobbyMenuLock = new Object();
    private static volatile boolean inSelectLobbyMenu = false;

    private static final Object inLobbyMenuLock = new Object();
    private static volatile boolean inLobbyMenu = false;

    private static final Object inGameLock = new Object();
    private static volatile boolean inGame = false;

    private static final Object inGameEndMenuLock = new Object();
    private static volatile boolean inGameEndMenu = false;

    private static final Object ourTurnEvaluatedLock = new Object();
    private static volatile boolean ourTurnEvaluated = true;

    private static ConnectionObject connectionObject;

    private static volatile boolean awaitingResponse = false;
    private static final Object responseLock = new Object();

    private static volatile boolean isConnected = true;


    /**
     * Set the current user.
     *
     * @param user The user to set.
     */
    public static void setUser(User user) {
        synchronized (userLock) {
            MainContainer.user = user;
        }
    }

    /**
     * Get the current user.
     *
     * @return The current user.
     */
    public static User getUser() {
        synchronized (userLock) {
            return user;
        }
    }

    /**
     * Set the status of being in the game end menu.
     *
     * @param status The status to set.
     */
    public static void setInGameEndMenu(boolean status) {
        synchronized (inGameEndMenuLock) {
            inGameEndMenu = status;
        }
    }

    /**
     * Check if the client is in the game end menu.
     *
     * @return True if in the game end menu, otherwise false.
     */
    public static boolean isInGameEndMenu() {
        synchronized (inGameEndMenuLock) {
            return inGameEndMenu;
        }
    }

    /**
     * Set the connection status.
     *
     * @param connected The connection status to set.
     */
    public static void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * Check if the client is connected.
     *
     * @return True if connected, otherwise false.
     */
    public static boolean isConnected() {
        return isConnected;
    }

    /**
     * Update the last ping response time to the current timestamp.
     */
    public static void updateLastPingResponseTime() {
        lastPingResponseTime.set(System.currentTimeMillis());
    }

    /**
     * Get the last ping response time.
     *
     * @return The last ping response time.
     */
    public static long getLastPingResponseTime() {
        return lastPingResponseTime.get();
    }

    /**
     * Set the connection object.
     *
     * @param connectionObject The connection object to set.
     */
    public static void setConnectionObject(ConnectionObject connectionObject) {
            MainContainer.connectionObject = connectionObject;
    }

    /**
     * Get the connection object.
     *
     * @return The connection object.
     */
    public static ConnectionObject getConnectionObject() {
            return connectionObject;
    }

    /**
     * Set the status of awaiting a response.
     *
     * @param status The status to set.
     */
    public static void setAwaitingResponse(boolean status) {
        synchronized (responseLock) {
            awaitingResponse = status;
        }
    }

    /**
     * Check if the client is awaiting a response.
     *
     * @return True if awaiting a response, otherwise false.
     */
    public static boolean isAwaitingResponse() {
        synchronized (responseLock) {
            return awaitingResponse;
        }
    }

    /**
     * Get the incoming message queue.
     *
     * @return The incoming message queue.
     */
    public static BlockingQueue<String> getIncomingMessageQueue() {
        return incomingMessageQueue;
    }

    /**
     * Get the outgoing message queue.
     *
     * @return The outgoing message queue.
     */
    public static BlockingQueue<String> getOutgoingMessageQueue() {
        return outgoingMessageQueue;
    }

    /**
     * Get the game queue.
     *
     * @return The game queue.
     */
    public static BlockingQueue<String> getGameQueue() {
        return gameQueue;
    }

    /**
     * Check if the client is in the select lobby menu.
     *
     * @return True if in the select lobby menu, otherwise false.
     */
    public static boolean isInSelectLobbyMenu() {
        synchronized (inSelectLobbyMenuLock) {
            return inSelectLobbyMenu;
        }
    }

    /**
     * Set the status of being in the select lobby menu.
     *
     * @param status The status to set.
     */
    public static void setInSelectLobbyMenu(boolean status) {
        synchronized (inSelectLobbyMenuLock) {
            inSelectLobbyMenu = status;
        }
    }

    /**
     * Check if the client is in the game.
     *
     * @return True if in the game, otherwise false.
     */
    public static boolean isInGame() {
        synchronized (inGameLock) {
            return inGame;
        }
    }

    /**
     * Set the status of being in the game.
     *
     * @param status The status to set.
     */
    public static void setInGame(boolean status) {
        synchronized (inGameLock) {
            inGame = status;
        }
    }

    /**
     * Check if the client is in the lobby menu.
     *
     * @return True if in the lobby menu, otherwise false.
     */
    public static boolean isInLobbyMenu() {
        synchronized (inLobbyMenuLock) {
            return inLobbyMenu;
        }
    }

    /**
     * Set the status of being in the lobby menu.
     *
     * @param status The status to set.
     */
    public static void setInLobbyMenu(boolean status) {
        synchronized (inLobbyMenuLock) {
            inLobbyMenu = status;
        }
    }

    /**
     * Check if it is our turn to evaluate.
     *
     * @return True if it is our turn to evaluate, otherwise false.
     */
    public static boolean isOurTurnEvaluated() {
        synchronized (ourTurnEvaluatedLock) {
            return ourTurnEvaluated;
        }
    }

    /**
     * Set the status of our turn evaluation.
     *
     * @param status The status to set.
     */
    public static void setOurTurnEvaluated(boolean status) {
        synchronized (ourTurnEvaluatedLock) {
            ourTurnEvaluated = status;
        }
    }
}

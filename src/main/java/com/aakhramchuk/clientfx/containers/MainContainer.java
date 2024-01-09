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

    private static final Object ourTurnEvaluatedLock = new Object();
    private static volatile boolean ourTurnEvaluated = false;

    private static ConnectionObject connectionObject;

    private static volatile boolean awaitingResponse = false;
    private static final Object responseLock = new Object();

    private static volatile boolean isConnected = true;


    public static void setUser(User user) {
        synchronized (userLock) {
            MainContainer.user = user;
        }
    }

    public static User getUser() {
        synchronized (userLock) {
            return user;
        }
    }

    public static void setConnected(boolean connected) {
        isConnected = connected;
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static void updateLastPingResponseTime() {
        lastPingResponseTime.set(System.currentTimeMillis());
    }

    public static long getLastPingResponseTime() {
        return lastPingResponseTime.get();
    }

    public static void setConnectionObject(ConnectionObject connectionObject) {
            MainContainer.connectionObject = connectionObject;
    }

    public static ConnectionObject getConnectionObject() {
            return connectionObject;
    }

    public static void setAwaitingResponse(boolean status) {
        synchronized (responseLock) {
            awaitingResponse = status;
        }
    }

    public static boolean isAwaitingResponse() {
        synchronized (responseLock) {
            return awaitingResponse;
        }
    }

    public static BlockingQueue<String> getIncomingMessageQueue() {
        return incomingMessageQueue;
    }

    public static BlockingQueue<String> getOutgoingMessageQueue() {
        return outgoingMessageQueue;
    }

    public static BlockingQueue<String> getGameQueue() {
        return gameQueue;
    }

    public static boolean isInSelectLobbyMenu() {
        synchronized (inSelectLobbyMenuLock) {
            return inSelectLobbyMenu;
        }
    }

    public static void setInSelectLobbyMenu(boolean status) {
        synchronized (inSelectLobbyMenuLock) {
            inSelectLobbyMenu = status;
        }
    }

    public static boolean isInGame() {
        synchronized (inGameLock) {
            return inGame;
        }
    }

    public static void setInGame(boolean status) {
        synchronized (inGameLock) {
            inGame = status;
        }
    }

    public static boolean isInLobbyMenu() {
        synchronized (inLobbyMenuLock) {
            return inLobbyMenu;
        }
    }

    public static void setInLobbyMenu(boolean status) {
        synchronized (inLobbyMenuLock) {
            inLobbyMenu = status;
        }
    }

    public static boolean isOurTurnEvaluated() {
        synchronized (ourTurnEvaluatedLock) {
            return ourTurnEvaluated;
        }
    }

    public static void setOurTurnEvaluated(boolean status) {
        synchronized (ourTurnEvaluatedLock) {
            ourTurnEvaluated = status;
        }
    }
}

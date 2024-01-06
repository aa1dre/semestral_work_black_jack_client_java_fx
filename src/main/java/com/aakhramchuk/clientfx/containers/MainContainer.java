package com.aakhramchuk.clientfx.containers;

import com.aakhramchuk.clientfx.objects.ConnectionObject;
import com.aakhramchuk.clientfx.objects.User;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MainContainer {
    private static User user;
    private static final Object userLock = new Object();
    private static final BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(10);
    private static final BlockingQueue<String> gameQueue = new ArrayBlockingQueue<>(10);
    private volatile boolean inSelectLobbyMenu = false;
    private volatile boolean inGame = false;
    private volatile boolean ourTurnEvaluated = false;

    private static ConnectionObject connectionObject;

    private static volatile boolean awaitingResponse = false;
    private static final Object responseLock = new Object();

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

    public static BlockingQueue<String> getMessageQueue() {
        return messageQueue;
    }
}

package com.aakhramchuk.clientfx.containers;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxContainer {
    private static Stage currentModalWindow;
    private static Stage currentStage;
    private static Scene currentScene;

    public static Stage getCurrentModalWindow() {
        return currentModalWindow;
    }

    public static void setCurrentModalWindow(Stage currentModalWindow) {
        FxContainer.currentModalWindow = currentModalWindow;
    }

    public static Stage getCurrentStage() {
        return currentStage;
    }

    public static void setCurrentStage(Stage currentStage) {
        FxContainer.currentStage = currentStage;
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static void setCurrentScene(Scene currentScene) {
        FxContainer.currentScene = currentScene;
    }
}

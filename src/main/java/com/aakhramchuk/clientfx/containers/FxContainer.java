package com.aakhramchuk.clientfx.containers;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxContainer {
    private static Stage currentModalWindow;
    private static Stage currentStage;
    private static Scene currentScene;

    /**
     * Returns the current modal window that is displayed
     * @return the current modal window
     */
    public static Stage getCurrentModalWindow() {
        return currentModalWindow;
    }

    /**
     * Sets the current modal window that is displayed
     * @param currentModalWindow the current modal window
     */
    public static void setCurrentModalWindow(Stage currentModalWindow) {
        FxContainer.currentModalWindow = currentModalWindow;
    }

    /**
     * Returns the current stage that is displayed
     * @return the current stage
     */
    public static Stage getCurrentStage() {
        return currentStage;
    }

    /**
     * Sets the current stage that is displayed
     * @param currentStage the current stage
     */
    public static void setCurrentStage(Stage currentStage) {
        FxContainer.currentStage = currentStage;
    }

    /**
     * Returns the current scene that is displayed
     * @return the current scene
     */
    public static Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Sets the current scene that is displayed
     * @param currentScene the current scene
     */
    public static void setCurrentScene(Scene currentScene) {
        FxContainer.currentScene = currentScene;
    }
}

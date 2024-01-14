package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.FxContainer;
import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.Lobby;
import com.aakhramchuk.clientfx.objects.LobbyManager;
import com.aakhramchuk.clientfx.utils.ActionUtils;
import com.aakhramchuk.clientfx.utils.FxUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;

import static com.aakhramchuk.clientfx.objects.Constants.DELETE_LOBBY_OPCODE_CONFIG_VALUE;
import static com.aakhramchuk.clientfx.objects.Constants.JOIN_LOBBY_OPCODE_CONFIG_VALUE;

public class MainMenuController {

    @FXML
    BorderPane borderPane;

    @FXML
    private Button joinLobbyBtn;

    @FXML
    private Button createLobbyBtn;

    @FXML
    private Button deleteLobbyBtn;

    @FXML
    private TableView<Lobby> lobbiesTW;

    @FXML
    private TableColumn<Lobby, Integer> lobbyIdColumn;

    @FXML
    private TableColumn<Lobby, String> lobbyNameColumn;

    @FXML
    private TableColumn<Lobby, String> lobbyCreatorColumn;

    @FXML
    private TableColumn<Lobby, Integer> lobbyPlayersColumn;

    @FXML
    private TableColumn<Lobby, Integer> lobbyMaxPlayersColumn;

    @FXML
    private TableColumn<Lobby, Boolean> lobbyPasswordColumn;

    @FXML
    private TableColumn<Lobby, Boolean> lobbyGameStartedColumn;

    private SelectionModel<Lobby> lobbyTableSelection;

    /**
     * Initialize the main menu screen.
     * Set the initial states for the different containers and apply background image.
     */
    @FXML
    public void initialize() {
        MainContainer.setInSelectLobbyMenu(true);
        MainContainer.setInLobbyMenu(false);
        MainContainer.setInGame(false);
        MainContainer.setInGameEndMenu(false);

        // Apply a background image to the BorderPane.
        FxUtils.setBackgroundImage(borderPane);

        initializeTable();
    }

    /**
     * Initialize the lobby table with appropriate cell factories and value factories.
     */
    private void initializeTable() {
        lobbyTableSelection = lobbiesTW.getSelectionModel();
        lobbiesTW.setEditable(false);

        lobbyIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        lobbyNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        lobbyCreatorColumn.setCellValueFactory(cellData -> cellData.getValue().creatorInfoProperty());
        lobbyPlayersColumn.setCellValueFactory(cellData -> cellData.getValue().currentPlayersProperty().asObject());
        lobbyMaxPlayersColumn.setCellValueFactory(cellData -> cellData.getValue().maxPlayersProperty().asObject());
        lobbyPasswordColumn.setCellValueFactory(cellData -> cellData.getValue().hasPasswordProperty());
        lobbyGameStartedColumn.setCellValueFactory(cellData -> cellData.getValue().gameStartedProperty());

        // Allow editing of the lobby name column.
        lobbyNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

    }

    /**
     * Handle the join lobby button action.
     * Validates the lobby selection and handles lobby join operations.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void joinLobbyBtnAction(ActionEvent event) throws IOException, InterruptedException {
        // Validate lobby selection
        if (lobbyTableSelection == null || lobbyTableSelection.isEmpty() || lobbyTableSelection.getSelectedItem() == null) {
            Alert alert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_lobby_join_process"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_lobby_join_process_select"));
            alert.showAndWait();
            return;
        }

        // Handle joining a lobby with or without a password
        if (lobbyTableSelection.getSelectedItem().getHasPassword()) {
            FxManager.createEnterPasswordModalWindow(lobbyTableSelection.getSelectedItem(), true);
        } else {
            ActionUtils.actionLobby(JOIN_LOBBY_OPCODE_CONFIG_VALUE, lobbyTableSelection.getSelectedItem(), null);
        }

        // Close the current modal window if it exists.
        FxUtils.closeCurrentModalWindowIfExist();
    }

    /**
     * Handle the create lobby button action.
     * Opens the lobby creation modal window.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    public void createLobbyBtnAction(ActionEvent event) throws IOException {
        FxManager.createLobbyCreationModalWindow();
    }

    /**
     * Handle the delete lobby button action.
     * Validates the lobby selection and handles lobby deletion operations.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void deleteLobbyBtnAction(ActionEvent event) throws IOException, InterruptedException {
        // Validate lobby selection.
        if (lobbyTableSelection == null || lobbyTableSelection.isEmpty() || lobbyTableSelection.getSelectedItem() == null) {
            FxUtils.showErrorInLobbyDeleteProcessAlert();
            return;
        }

        // Handle deleting a lobby with or without a password.
        if (lobbyTableSelection.getSelectedItem().getHasPassword()) {
            FxManager.createEnterPasswordModalWindow(lobbyTableSelection.getSelectedItem(), false);
        } else {
            ActionUtils.actionLobby(DELETE_LOBBY_OPCODE_CONFIG_VALUE, lobbyTableSelection.getSelectedItem(), null);
        }

        // Close the current modal window if it exists.
        FxUtils.closeCurrentModalWindowIfExist();
    }

    /**
     * Handle the logout button action.
     * Initiates the logout process.
     *
     * @param action The ActionEvent triggered by the button click.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void logoutAction(ActionEvent action) throws IOException, InterruptedException {
        ActionUtils.logout();
    }

    /**
     * Get the observable list of lobby items.
     *
     * @return ObservableList of Lobby objects.
     */
    public ObservableList<Lobby> getItems() {
        return LobbyManager.getLobbiesList() ;
    }
}

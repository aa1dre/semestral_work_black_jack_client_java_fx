package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.objects.Lobby;
import com.aakhramchuk.clientfx.objects.LobbyManager;
import com.aakhramchuk.clientfx.objects.User;
import com.aakhramchuk.clientfx.utils.ActionUtils;
import com.aakhramchuk.clientfx.utils.FxUtils;
import com.aakhramchuk.clientfx.utils.GameUtils;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;

import java.io.IOException;

public class LobbyMenuController {

    @FXML
    BorderPane borderPane;

    @FXML
    private Button startGameBtn;

    @FXML
    private Button leaveLobbyBtn;

    @FXML
    private TextArea lobbyInfoTextArea;

    @FXML
    private TableView<User> usersTW;

    @FXML
    private TableColumn<User, Number> userIdColumn;

    @FXML
    private TableColumn<User, String> userUsernameColumn;

    @FXML
    private TableColumn<User, String> userNameColumn;

    @FXML
    private TableColumn<User, String> userSurnameColumn;

    @FXML
    private TableColumn<User, Boolean> isCreatorColumn;

    @FXML
    private TableColumn<User, Boolean> isAdminColumn;

    @FXML
    private TableColumn<User, Boolean> isOnlineColumn;


    private SelectionModel<User> userTableSelection;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        // Set the screen flags
        MainContainer.setInSelectLobbyMenu(false);
        MainContainer.setInGame(false);
        MainContainer.setInLobbyMenu(true);
        MainContainer.setInGameEndMenu(false);

        // Bind lobby information to the TextArea
        bindLobbyInfoToTextArea(LobbyManager.getCurrentLobby());

        // Set a background image for the BorderPane
        FxUtils.setBackgroundImage(borderPane);

        // Initialize the user table
        initializeTable();
    }

    /**
     * Initializes the user table.
     */
    private void initializeTable() {
        userTableSelection = usersTW.getSelectionModel();
        usersTW.setEditable(false);

        // Set up the user table columns
        userIdColumn.setCellValueFactory(cellData ->
                Bindings.createIntegerBinding(
                        () -> usersTW.getItems().indexOf(cellData.getValue()) + 1,
                        usersTW.getItems()
                )
        );

        usersTW.setItems(LobbyManager.getCurrentLobby().getUsersList());

        userUsernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        userSurnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        isCreatorColumn.setCellValueFactory(cellData -> cellData.getValue().creatorProperty());
        isAdminColumn.setCellValueFactory(cellData -> cellData.getValue().adminProperty());
        isOnlineColumn.setCellValueFactory(cellData -> cellData.getValue().onlineProperty());

        // Enable editing for some columns
        userUsernameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userSurnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * Handle the "Start Game" button click event to start the game.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void startGameBtnAction(ActionEvent event) throws IOException, InterruptedException {
        GameUtils.startGame();
    }

    /**
     * Handle the "Leave Lobby" button click event to leave the lobby.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void leaveLobbyBtnAction(ActionEvent event) throws IOException, InterruptedException {
        ActionUtils.leaveLobby();
    }

    /**
     * Handle the "Logout" button click event to log out the user.
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
     * Bind lobby information to the TextArea to display it.
     * @param lobby The lobby to bind information from.
     */
    private void bindLobbyInfoToTextArea(Lobby lobby) {
        lobbyInfoTextArea.textProperty().bind(Bindings.createStringBinding(() ->
                        "Lobby [" + lobby.getId() + "]\n" +
                                "Lobby name: " + lobby.getName() + "\n" +
                                "Lobby password: " + (lobby.getHasPassword() ? "true" : "false") + "\n" +
                                "Lobby players: [" + lobby.getCurrentPlayers() + "/" + lobby.getMaxPlayers() + "]\n",
                lobby.idProperty(),
                lobby.nameProperty(),
                lobby.hasPasswordProperty(),
                lobby.currentPlayersProperty(),
                lobby.maxPlayersProperty()
        ));
    }
}

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

    @FXML
    public void initialize() {
        MainContainer.setInSelectLobbyMenu(false);
        MainContainer.setInGame(false);
        MainContainer.setInLobbyMenu(true);
        MainContainer.setInGameEndMenu(false);

        bindLobbyInfoToTextArea(LobbyManager.getCurrentLobby());

        FxUtils.setBackgroundImage(borderPane);

        initializeTable();
    }

    private void initializeTable() {
        userTableSelection = usersTW.getSelectionModel();
        usersTW.setEditable(false);

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

        userUsernameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userSurnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    @FXML
    public void startGameBtnAction(ActionEvent event) throws IOException, InterruptedException {
        GameUtils.startGame();
    }


    @FXML
    public void leaveLobbyBtnAction(ActionEvent event) throws IOException, InterruptedException {
        ActionUtils.leaveLobby();
    }

    @FXML
    public void logoutAction(ActionEvent action) throws IOException, InterruptedException {
        ActionUtils.logout();
    }

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

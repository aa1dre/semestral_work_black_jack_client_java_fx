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

    @FXML
    public void initialize() {
        try {
            InputStream is = getClass().getResourceAsStream("/Images/background.jpg");
            if (is == null) {
                throw new FileNotFoundException("Cannot find background image");
            }

            Image image = new Image(is);
            BackgroundImage bgImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            borderPane.setBackground(new Background(bgImage));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        initializeTable();
    }

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

        lobbyNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

    }

    @FXML
    public void joinLobbyBtnAction(ActionEvent event) throws IOException {
        FxContainer.getCurrentModalWindow().close();
    }

    @FXML
    public void createLobbyBtnAction(ActionEvent event) throws IOException {
        FxManager.createLobbyCreationModalWindow();
    }

    @FXML
    public void deleteLobbyBtnAction(ActionEvent event) throws IOException, InterruptedException {
        if (lobbyTableSelection == null || lobbyTableSelection.isEmpty() || lobbyTableSelection.getSelectedItem() == null) {
            Alert alert = FxUtils.createWarningAlert(MainContainer.getConnectionObject().getConfig().getString("text.alert_title.warning"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_header_text.error_in_lobby_delete_process"),
                    MainContainer.getConnectionObject().getConfig().getString("text.alert_content_text.error_in_lobby_delete_process_select"));
            alert.showAndWait();
            return;
        }

        if (lobbyTableSelection.getSelectedItem().getHasPassword()) {
            FxManager.createEnterPasswordModalWindow(lobbyTableSelection.getSelectedItem(), false);
        } else {
            ActionUtils.actionLobby(DELETE_LOBBY_OPCODE_CONFIG_VALUE, lobbyTableSelection.getSelectedItem(), null);
        }
        FxContainer.getCurrentModalWindow().close();
    }

    @FXML
    public void logoutAction(ActionEvent action) throws IOException, InterruptedException {
        ActionUtils.logout();
    }

    public ObservableList<Lobby> getItems() {
        return LobbyManager.getLobbiesList() ;
    }
}

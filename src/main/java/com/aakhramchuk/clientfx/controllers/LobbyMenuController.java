package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.Lobby;
import com.aakhramchuk.clientfx.objects.LobbyManager;
import com.aakhramchuk.clientfx.objects.User;
import com.aakhramchuk.clientfx.utils.ActionUtils;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LobbyMenuController {

    @FXML
    BorderPane borderPane;

    @FXML
    private Button startGameBtn;

    @FXML
    private Button leaveLobbyBtn;

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
    public void startGameBtnAction(ActionEvent event) throws IOException {
        FxManager.createLobbyCreationModalWindow();
    }


    @FXML
    public void leaveLobbyBtnAction(ActionEvent event) throws IOException, InterruptedException {
        ActionUtils.leaveLobby();
    }

    @FXML
    public void logoutAction(ActionEvent action) throws IOException, InterruptedException {
        ActionUtils.logout();
    }
}

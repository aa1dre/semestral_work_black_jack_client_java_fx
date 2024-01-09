package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.GamePlayer;
import com.aakhramchuk.clientfx.objects.LobbyManager;
import com.aakhramchuk.clientfx.objects.User;
import com.aakhramchuk.clientfx.utils.ActionUtils;
import com.aakhramchuk.clientfx.utils.FxUtils;
import com.aakhramchuk.clientfx.utils.GameUtils;
import com.aakhramchuk.clientfx.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GameController {
    @FXML
    BorderPane borderPane;

    @FXML
    private Button startGameBtn;

    @FXML
    private Button leaveLobbyBtn;

    @FXML
    private TableView<GamePlayer> gamePlayersTW;

    @FXML
    private TableColumn<GamePlayer, Number> gamePlayerIdColumn;

    @FXML
    private TableColumn<GamePlayer, String> gamePlayerUsernameColumn;

    @FXML
    private TableColumn<GamePlayer, String> gamePlayerNameColumn;

    @FXML
    private TableColumn<GamePlayer, String> gamePlayerSurnameColumn;

    @FXML
    private TableColumn<GamePlayer, Integer> gamePlayerCardsCountColumn;

    @FXML
    private TableColumn<GamePlayer, Boolean> gamePlayerTurnColumn;

    @FXML
    private TableColumn<GamePlayer, Boolean> gamePlayerOnlineColumn;

    @FXML
    private TextField cardsCountTf;

    @FXML
    private TextField cardsValueTf;

    @FXML
    private HBox cardsContainerHbx;

    private SelectionModel<GamePlayer> gamePlayerTableSelection;

    @FXML
    public void initialize() {
        MainContainer.setInSelectLobbyMenu(false);
        MainContainer.setInLobbyMenu(false);
        MainContainer.setInGame(true);


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

        LobbyManager.getCurrentLobby().getGameObject().getPlayers().forEach(player -> {
            if (player.getUsername().equals(MainContainer.getUser().getUsername())) {
                displayCards(player);
            }
        });

        initializeTable();

        LobbyManager.getCurrentLobby().getGameObject().getPlayers().forEach(player -> {
            player.cardsProperty().addListener((observable, oldValue, newValue) -> {
                if (player.getUsername().equals(MainContainer.getUser().getUsername())) {
                    displayCards(player);
                }
            });
        });

        bindPlayerPropertiesToTextFields();
    }

    private void bindPlayerPropertiesToTextFields() {
        LobbyManager.getCurrentLobby().getGameObject().getPlayers().forEach(player -> {
            if (player.getUsername().equals(MainContainer.getUser().getUsername())) {
                cardsCountTf.textProperty().bind(player.cardCountProperty().asString());
                cardsValueTf.textProperty().bind(player.cardsValueProperty().asString());
            }
        });
    }

    private void initializeTable() {
        gamePlayerTableSelection = gamePlayersTW.getSelectionModel();
        gamePlayersTW.setEditable(false);

        ObservableList<GamePlayer> filteredGamePlayers = LobbyManager.getCurrentLobby().getGameObject().getPlayers().filtered(
                gamePlayer -> !gamePlayer.getUsername().equals(MainContainer.getUser().getUsername())
        );

        gamePlayerIdColumn.setCellValueFactory(cellData ->
                Bindings.createIntegerBinding(
                        () -> gamePlayersTW.getItems().indexOf(cellData.getValue()) + 1,
                        gamePlayersTW.getItems()
                )
        );
        gamePlayersTW.setItems(filteredGamePlayers);

        gamePlayerUsernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        gamePlayerNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        gamePlayerSurnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        gamePlayerCardsCountColumn.setCellValueFactory(cellData -> cellData.getValue().cardCountProperty().asObject());
        gamePlayerTurnColumn.setCellValueFactory(cellData -> cellData.getValue().isCurrentPlayerProperty());
        gamePlayerOnlineColumn.setCellValueFactory(cellData -> cellData.getValue().isOnlineProperty());

        gamePlayerUsernameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        gamePlayerNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        gamePlayerSurnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    @FXML
    public void logoutAction(ActionEvent event) throws IOException, InterruptedException {
        ActionUtils.logout();
    }

    @FXML
    public void leaveLobbyAction(ActionEvent event) throws IOException, InterruptedException {
        ActionUtils.leaveLobby();
    }

    @FXML
    public void takeBtnAction(ActionEvent event) throws IOException, InterruptedException {
        GameUtils.takeAction();
    }

    @FXML
    public void passBtnAction(ActionEvent action) throws IOException, InterruptedException {
        GameUtils.passAction();
    }

    private void displayCards(GamePlayer player) {
        cardsContainerHbx.getChildren().clear();

        if (player.isCardsVisible()) {
            int cardCount = player.getCards().size();
            int cardHeight = calculateCardHeight(cardCount);
            int cardSpacing = calculateCardSpacing(cardCount);

            for (String cardCode : player.getCards()) {
                String cardPath = Utils.getCardImagePath(cardCode);
                InputStream is = getClass().getResourceAsStream(cardPath);
                if (is != null) {
                    Image image = new Image(is);
                    ImageView imageView = new ImageView(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(cardHeight);
                    HBox.setMargin(imageView, new Insets(0, cardSpacing, 0, 0));
                    cardsContainerHbx.getChildren().add(imageView);
                } else {
                    System.out.println("Card not found: " + cardPath);
                }
            }
        }
    }

    private int calculateCardHeight(int cardCount) {
        final int maxCardHeight = 300;
        final int minCardHeight = 20;

        if (cardCount <= 4) {
            return maxCardHeight;
        } else {
            int height = 1200 / cardCount;
            return Math.max(height, minCardHeight);
        }
    }

    private int calculateCardSpacing(int cardCount) {
        final int maxSpacing = 15;
        final int minSpacing = 2;

        if (cardCount <= 2) {
            return maxSpacing;
        } else {
            int spacing = maxSpacing - ((cardCount - 2));
            return Math.max(spacing, minSpacing);
        }
    }

}

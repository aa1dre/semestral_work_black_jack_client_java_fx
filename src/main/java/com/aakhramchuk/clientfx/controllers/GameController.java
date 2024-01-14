package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.objects.GamePlayer;
import com.aakhramchuk.clientfx.objects.LobbyManager;
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

    /**
     * Initialize the Game screen.
     */
    @FXML
    public void initialize() {
        // Set various flags to manage screen states
        MainContainer.setInSelectLobbyMenu(false);
        MainContainer.setInLobbyMenu(false);
        MainContainer.setInGameEndMenu(false);
        MainContainer.setInGame(true);

        // Set a background image for the borderPane
        FxUtils.setBackgroundImage(borderPane);

        // Display cards for the current player
        LobbyManager.getCurrentLobby().getGameObject().getPlayers().forEach(player -> {
            if (player.getUsername().equals(MainContainer.getUser().getUsername())) {
                displayCards(player);
            }
        });

        // Initialize the game player table
        initializeTable();

        // Update the card display when a player's cards change
        LobbyManager.getCurrentLobby().getGameObject().getPlayers().forEach(player -> {
            player.cardsProperty().addListener((observable, oldValue, newValue) -> {
                if (player.getUsername().equals(MainContainer.getUser().getUsername())) {
                    displayCards(player);
                }
            });
        });

        // Bind player properties to text fields
        bindPlayerPropertiesToTextFields();
    }

    /**
     * Bind the current player's card count and card value properties to text fields.
     */
    private void bindPlayerPropertiesToTextFields() {
        LobbyManager.getCurrentLobby().getGameObject().getPlayers().forEach(player -> {
            if (player.getUsername().equals(MainContainer.getUser().getUsername())) {
                cardsCountTf.textProperty().bind(player.cardCountProperty().asString());
                cardsValueTf.textProperty().bind(player.cardsValueProperty().asString());
            }
        });
    }

    /**
     * Initialize the game player table.
     */
    private void initializeTable() {
        // Set up the game player table
        gamePlayerTableSelection = gamePlayersTW.getSelectionModel();
        gamePlayersTW.setEditable(false);

        // Filter out the current player from the list of game players
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

        // Set up column cell value factories and cell factories
        gamePlayerUsernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        gamePlayerNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        gamePlayerSurnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        gamePlayerCardsCountColumn.setCellValueFactory(cellData -> cellData.getValue().cardCountProperty().asObject());
        gamePlayerTurnColumn.setCellValueFactory(cellData -> cellData.getValue().isCurrentPlayerProperty());
        gamePlayerOnlineColumn.setCellValueFactory(cellData -> cellData.getValue().isOnlineProperty());

        // Enable editing for some columns
        gamePlayerUsernameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        gamePlayerNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        gamePlayerSurnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * Handle the logout button click event.
     *
     * @param event The ActionEvent triggered by the logout button.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void logoutAction(ActionEvent event) throws IOException, InterruptedException {
        ActionUtils.logout();
    }

    /**
     * Handle the leave lobby button click event.
     *
     * @param event The ActionEvent triggered by the leave lobby button.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void leaveLobbyAction(ActionEvent event) throws IOException, InterruptedException {
        ActionUtils.leaveLobby();
    }

    /**
     * Handle the "Take" button click event in the game.
     *
     * @param event The ActionEvent triggered by the "Take" button.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void takeBtnAction(ActionEvent event) throws IOException, InterruptedException {
        GameUtils.takeAction();
    }

    /**
     * Handle the "Pass" button click event in the game.
     *
     * @param action The ActionEvent triggered by the "Pass" button.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void passBtnAction(ActionEvent action) throws IOException, InterruptedException {
        GameUtils.passAction();
    }

    /**
     * Display the cards for a specific player in the game.
     *
     * @param player The GamePlayer object representing the player.
     */
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

    /**
     * Calculate the height of the card display based on the number of cards.
     *
     * @param cardCount The number of cards to display.
     * @return The calculated height for displaying the cards.
     */
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

    /**
     * Calculate the spacing between displayed cards based on the number of cards.
     *
     * @param cardCount The number of cards to display.
     * @return The calculated spacing between cards.
     */
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

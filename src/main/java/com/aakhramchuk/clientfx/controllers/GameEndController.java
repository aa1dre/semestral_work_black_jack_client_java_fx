package com.aakhramchuk.clientfx.controllers;

import com.aakhramchuk.clientfx.containers.MainContainer;
import com.aakhramchuk.clientfx.managers.FxManager;
import com.aakhramchuk.clientfx.objects.GamePlayer;
import com.aakhramchuk.clientfx.objects.LobbyManager;
import com.aakhramchuk.clientfx.utils.FxUtils;
import com.aakhramchuk.clientfx.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;
import java.io.InputStream;

public class GameEndController {
    @FXML
    BorderPane borderPane;

    @FXML
    private Button closeBtn;

    @FXML
    private TableView<GamePlayer> gameEndPlayersTW;

    @FXML
    private Label resultLbl;

    @FXML
    private TableColumn<GamePlayer, Number> gamePlayerIdColumn;

    @FXML
    private TableColumn<GamePlayer, String> gamePlayerUsernameColumn;

    @FXML
    private TableColumn<GamePlayer, String> gamePlayerNameColumn;

    @FXML
    private TableColumn<GamePlayer, String> gamePlayerSurnameColumn;

    @FXML
    private TableColumn<GamePlayer, Integer> gamePlayerCardsValueColumn;

    @FXML
    private TableColumn<GamePlayer, String> gamePlayerCardsColumn;

    @FXML
    private TextField cardsCountTf;

    @FXML
    private TextField cardsValueTf;

    private SelectionModel<GamePlayer> gameEndPlayerTableSelection;

    /**
     * Initialize the Game End screen.
     */
    @FXML
    public void initialize() {
        // Set various flags to manage screen states
        MainContainer.setInSelectLobbyMenu(false);
        MainContainer.setInLobbyMenu(false);
        MainContainer.setInGame(false);
        MainContainer.setInGameEndMenu(true);

        // Set a background image for the borderPane
        FxUtils.setBackgroundImage(borderPane);

        // Initialize the game end table
        initializeTable();

        // Bind player properties to text fields
        bindPlayerPropertiesToTextFields();

        // Update the result label (WIN or LOOSE)
        updateResultLabel();
    }

    /**
     * Update the result label to display "WIN" or "LOOSE" based on the player's status.
     */
    private void updateResultLabel() {
        boolean isWinner = LobbyManager.getCurrentLobby().getGameObject().getWinners().stream()
                .anyMatch(winner -> winner.getUsername().equals(MainContainer.getUser().getUsername()));

        if (isWinner) {
            resultLbl.setText("WIN");
            resultLbl.setTextFill(javafx.scene.paint.Color.web("#00ff2b")); // Зеленый цвет
        } else {
            resultLbl.setText("LOOSE");
            resultLbl.setTextFill(javafx.scene.paint.Color.RED); // Красный цвет
        }
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
     * Initialize the game end table.
     */
    private void initializeTable() {
        gameEndPlayerTableSelection = gameEndPlayersTW.getSelectionModel();
        gameEndPlayersTW.setEditable(false);

        // Set up table columns and their value factories
        gamePlayerIdColumn.setCellValueFactory(cellData ->
                Bindings.createIntegerBinding(
                        () -> gameEndPlayersTW.getItems().indexOf(cellData.getValue()) + 1,
                        gameEndPlayersTW.getItems()
                )
        );

        gamePlayerUsernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        gamePlayerNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        gamePlayerSurnameColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        gamePlayerCardsValueColumn.setCellValueFactory(cellData -> cellData.getValue().cardsValueProperty().asObject());
        gamePlayerCardsColumn.setCellValueFactory(cellData -> cellData.getValue().cardsProperty().asString());

        // Customize the "Cards" column to display images
        gamePlayerCardsColumn.setCellFactory(column -> new TableCell<GamePlayer, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    // Create an HBox to display card images
                    HBox hbox = new HBox(5);
                    hbox.setAlignment(Pos.CENTER); // Center cards in HBox
                    GamePlayer player = getTableView().getItems().get(getIndex());
                    for (String cardCode : player.getCards()) {
                        String cardPath = Utils.getCardImagePath(cardCode);
                        InputStream is = getClass().getResourceAsStream(cardPath);
                        if (is != null) {
                            Image image = new Image(is, 100, 100, true, true); // Card dimensions
                            ImageView imageView = new ImageView(image);
                            hbox.getChildren().add(imageView);
                        } else {
                            System.out.println("Card image not found: " + cardPath);
                        }
                    }
                    // Create a ScrollPane to handle card overflow
                    ScrollPane scrollPane = new ScrollPane(hbox);
                    scrollPane.setFitToHeight(true); // Fit height to content
                    scrollPane.setPrefSize(200, 60); // Set preferred size for ScrollPane
                    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Show horizontal scrollbar as needed
                    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Never show vertical scrollbar
                    setGraphic(scrollPane); // Set ScrollPane as the cell's graphic
                }
            }
        });

        // Create a custom "Winner" column to display a boolean value as "True" or "False"
        TableColumn<GamePlayer, Boolean> winnerColumn = new TableColumn<>("Win");
        winnerColumn.setMinWidth(80);
        winnerColumn.setCellValueFactory(cellData ->
                Bindings.createBooleanBinding(() ->
                                LobbyManager.getCurrentLobby().getGameObject().getWinners().stream()
                                        .anyMatch(winner -> winner.getUsername().equals(cellData.getValue().getUsername())),
                        LobbyManager.getCurrentLobby().getGameObject().getWinners()
                )
        );
        gameEndPlayersTW.getColumns().add(winnerColumn);
        ObservableList<GamePlayer> players = LobbyManager.getCurrentLobby().getGameObject().getPlayers();
        gameEndPlayersTW.setItems(players);

        // Customize the "Winner" column to display "True" or "False"
        winnerColumn.setCellFactory(column -> new TableCell<GamePlayer, Boolean>() {
            @Override
            protected void updateItem(Boolean isWinner, boolean empty) {
                super.updateItem(isWinner, empty);
                if (empty || isWinner == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(isWinner ? "True" : "False");
                }
            }
        });
    }

    /**
     * Handle the close button click event to return to the lobby.
     *
     * @param action The ActionEvent triggered by the close button.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @FXML
    public void closeBtnAction(ActionEvent action) throws IOException, InterruptedException {
        // Clear the game object and navigate back to the lobby scene
        LobbyManager.getCurrentLobby().setGameObject(null);
        FxManager.changeCurrentSceneToLobbyScene();
    }


}

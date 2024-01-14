package com.aakhramchuk.clientfx.objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.util.List;

public class GamePlayer {
    private StringProperty username = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty surname = new SimpleStringProperty();
    private BooleanProperty cardsVisible = new SimpleBooleanProperty();
    private ListProperty<String> cards = new SimpleListProperty<>(FXCollections.observableArrayList());
    private IntegerProperty cardCount = new SimpleIntegerProperty();
    private BooleanProperty isCurrentPlayer = new SimpleBooleanProperty();
    private IntegerProperty cardsValue = new SimpleIntegerProperty(-1);
    private BooleanProperty isOnline = new SimpleBooleanProperty();

    public GamePlayer(String username, String name, String surname, boolean cardsVisible, List<String> cards, int cardCount, boolean isOnline) {
        setUsername(username);
        setName(name);
        setSurname(surname);
        setCardsVisible(cardsVisible);
        this.cards.setAll(cards);
        setCardCount(cardCount);
        setOnline(isOnline);
    }

    public void updatePlayer(GamePlayer player) {
        setCardsVisible(player.isCardsVisible());
        setCards(player.getCards());
        setCardCount(player.getCardCount());
        setCardsValue(player.getCardsValue());
        setOnline(player.isOnline());
    }

    // online property
    public boolean isOnline() { return isOnline.get(); }
    public void setOnline(boolean isOnline) { this.isOnline.set(isOnline); }
    public BooleanProperty isOnlineProperty() { return isOnline; }

    // Username property
    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    // Name property
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    // Surname property
    public String getSurname() { return surname.get(); }
    public void setSurname(String surname) { this.surname.set(surname); }
    public StringProperty surnameProperty() { return surname; }

    // CardsVisible property
    public boolean isCardsVisible() { return cardsVisible.get(); }
    public void setCardsVisible(boolean cardsVisible) { this.cardsVisible.set(cardsVisible); }
    public BooleanProperty cardsVisibleProperty() { return cardsVisible; }

    // Cards property
    public List<String> getCards() { return cards.get(); }
    public void setCards(List<String> cards) { this.cards.setAll(cards); }
    public ListProperty<String> cardsProperty() { return cards; }

    // CardCount property
    public int getCardCount() { return cardCount.get(); }
    public void setCardCount(int cardCount) { this.cardCount.set(cardCount); }
    public IntegerProperty cardCountProperty() { return cardCount; }

    // IsCurrentPlayer property
    public boolean isCurrentPlayer() { return isCurrentPlayer.get(); }
    public void setIsCurrentPlayer(boolean isCurrentPlayer) { this.isCurrentPlayer.set(isCurrentPlayer); }
    public BooleanProperty isCurrentPlayerProperty() { return isCurrentPlayer; }

    // CardsValue property
    public int getCardsValue() { return cardsValue.get(); }
    public void setCardsValue(int cardsValue) { this.cardsValue.set(cardsValue); }
    public IntegerProperty cardsValueProperty() { return cardsValue; }

}

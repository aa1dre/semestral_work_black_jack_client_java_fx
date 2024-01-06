package com.aakhramchuk.clientfx.objects;

import java.util.List;

public class GamePlayer {
    String login;
    String name;
    String surname;
    boolean cardsVisible;
    List<String> cards;
    int cardCount;
    boolean isCurrentPlayer;
    int cardsValue = -1; // Флаг, указывающий на текущего игрока

    public GamePlayer(String login, String name, String surname, boolean cardsVisible, List<String> cards, int cardCount) {
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.cardsVisible = cardsVisible;
        this.cards = cards;
        this.cardCount = cardCount;
    }

    public String getLogin() {
        return login;
    }

    public void setIsCurrentPlayer(boolean currentPlayer) {
        isCurrentPlayer = currentPlayer;
    }

    public boolean isCurrentPlayer() {
        return isCurrentPlayer;
    }

    public int getCardsValue() {
        return cardsValue;
    }

    public void setCardsValue(int cardsValue) {
        this.cardsValue = cardsValue;
    }

    @Override
    public String toString() {
        int lineWidth = 80;
        String cardInfo = (cardsVisible && (cards == null || !cards.isEmpty())) ?
                ("Cards: " + String.join(", ", cards) + (cardsValue != -1 ? " (Value: " + cardsValue + ")" : "")) :
                "Card count: " + cardCount;

        String currentPlayerInfo = isCurrentPlayer ? "-> " : "   ";

        String playerInfo = String.format("%-40s", currentPlayerInfo + login + " - " + name + " " + surname);
        String cardInfoFormatted = String.format("%40s", cardInfo);

        return playerInfo + cardInfoFormatted;
    }
}

package com.cs.cardwhere.Models;

public class Card {

    private String card;
    private int cardImage;

    public Card(String card, int cardImage){
        this.card = card;
        this.cardImage = cardImage;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public int getCardImage() {
        return cardImage;
    }

    public void setCardImage(int imgcard) {
        this.cardImage = imgcard;
    }
}

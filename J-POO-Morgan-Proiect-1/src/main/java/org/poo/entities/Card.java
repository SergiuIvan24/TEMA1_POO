package org.poo.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Card {
    private String cardNumber;
    private boolean blocked;

    public Card(String cardNumber) {
        this.cardNumber = cardNumber;
        this.blocked = false;
    }


    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    public abstract boolean canPerformTransaction();

    public abstract ObjectNode toJson(ObjectMapper mapper);
}

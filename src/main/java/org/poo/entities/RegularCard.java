package org.poo.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RegularCard extends Card{

    public RegularCard(String cardNumber) {
        super(cardNumber);
    }

    @Override
    public boolean canPerformTransaction() {
        return !isBlocked();
    }

    @Override
    public ObjectNode toJson(ObjectMapper mapper) {
        ObjectNode cardNode = mapper.createObjectNode();
        cardNode.put("cardNumber", getCardNumber());
        cardNode.put("status", isBlocked() ? "frozen" : "active");
        return cardNode;
    }
}

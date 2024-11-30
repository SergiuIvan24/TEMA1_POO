package org.poo.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.*;

public class CheckCardStatus implements Command {
    private String cardNumber;
    private final int timestamp;
    private UserRepo userRepo;

    public CheckCardStatus(String cardNumber, int timestamp, UserRepo userRepo) {
        this.cardNumber = cardNumber;
        this.timestamp = timestamp;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("command", "checkCardStatus");
        result.put("timestamp", timestamp);

        User user = userRepo.getUserByCardNumber(cardNumber);
        if (user == null) {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("timestamp", timestamp);
            errorNode.put("description", "Card not found");
            result.set("output", errorNode);
            output.add(result);
            return;
        }

        for (Account account : user.getAccounts()) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    if (account.getBalance() <= account.getMinimumBalance()) {
                        card.setBlocked(true);
                        Transaction frozenTransaction = new Transaction.Builder()
                                .setTimestamp(timestamp)
                                .setDescription("You have reached the minimum amount of funds, the card will be frozen")
                                .build();
                        account.addTransaction(frozenTransaction);
                    } else if (account.getBalance() - account.getMinimumBalance() <= 30) {
                        Transaction warningTransaction = new Transaction.Builder()
                                .setTimestamp(timestamp)
                                .setDescription("You have reached the minimum amount of funds, the card will be frozen")
                                .build();
                        account.addTransaction(warningTransaction);
                    }
                    return;
                }
            }
        }
    }


}

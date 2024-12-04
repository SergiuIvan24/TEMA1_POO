package org.poo.Commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.entities.*;

public class DeleteCard implements Command {
    private String email;
    private String cardNumber;
    private final int timestamp;
    private UserRepo userRepo;

    public DeleteCard(String email, String cardNumber, UserRepo userRepo, int timestamp) {
        this.email = email;
        this.cardNumber = cardNumber;
        this.userRepo = userRepo;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(ArrayNode output) {
        User user = userRepo.getUser(email);
        if (user == null) {
            return;
        }

        Account account = user.getAccountByCardNumber(cardNumber);
        if (account == null) {
            return;
        }

        Card card = account.getCard(cardNumber);
        if (card == null) {
           return;
        }
        account.removeCard(card);
        Transaction transaction = new Transaction.Builder()
                .setTimestamp(timestamp)
                .setDescription("The card has been destroyed")
                .setCard(card.getCardNumber())
                .setCardHolder(email)
                .setAccount(account.getIBAN())
                .build();

        account.addTransaction(transaction);
    }
}

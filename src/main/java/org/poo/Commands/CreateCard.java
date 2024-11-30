package org.poo.Commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.entities.*;
import org.poo.utils.Utils;

class CreateCard implements Command {
    private final String account;
    private final String email;
    private final UserRepo userRepo;
    private final int timestamp;

    public CreateCard(String account, String email, UserRepo userRepo, int timestamp) {
        this.account = account;
        this.email = email;
        this.userRepo = userRepo;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(ArrayNode output) {
        User user = userRepo.getUser(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found for email: " + email);
        }

        Account account = user.getAccount(this.account);
        if (account == null) {
            throw new IllegalArgumentException("Account not found for IBAN: " + this.account);
        }

        Card newCard = new RegularCard(Utils.generateCardNumber());
        account.addCard(newCard);

        Transaction transaction = new Transaction.Builder()
                .setTimestamp(timestamp)
                .setDescription("New card created")
                .setCard(newCard.getCardNumber())
                .setCardHolder(email)
                .setAccount(this.account)
                .build();

        account.addTransaction(transaction);
    }

}



package org.poo.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.*;
import org.poo.utils.Utils;

public final class PayOnline implements Command {
    private String cardNumber;
    private double amount;
    private String currency;
    private final int timestamp;
    private String description;
    private String commerciant;
    private String email;
    private UserRepo userRepo;

    public PayOnline(final String cardNumber, final double amount,
                     final String currency, final int timestamp,
                     final String description, final String commerciant,
                     final String email, final UserRepo userRepo) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.description = description;
        this.commerciant = commerciant;
        this.email = email;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("command", "payOnline");

        User user = userRepo.getUser(email);
        if (user == null) {
            return;
        }

        for (Account account : user.getAccounts()) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    if (card.isBlocked()) {
                        Transaction frozenTransaction = new Transaction.Builder()
                                .setTimestamp(timestamp)
                                .setDescription("The card is frozen")
                                .build();
                        account.addTransaction(frozenTransaction);
                        return;
                    }
                    double convertedAmount;
                    if (currency.equals(account.getCurrency())) {
                        convertedAmount = amount;
                    } else {
                        double rate = userRepo.getExchangeRate(currency, account.getCurrency());
                        convertedAmount = amount * rate;
                    }
                    double newBalance = account.getBalance() - convertedAmount;
                    if (newBalance >= account.getMinimumBalance() && card.canPerformTransaction()) {
                        account.setBalance(newBalance);

                        Transaction transaction = new Transaction.Builder()
                                .setTimestamp(timestamp)
                                .setDescription("Card payment")
                                .setAmount(convertedAmount)
                                .setCurrency(null)
                                .setCommerciant(commerciant)
                                .build();

                        account.addTransaction(transaction);

                        if (card.getCardType().equals("OneTimePayCard")) {
                            account.removeCard(card);

                            Transaction removeCardTransaction = new Transaction.Builder()
                                    .setTimestamp(timestamp)
                                    .setAccount(account.getIban())
                                    .setCard(card.getCardNumber())
                                    .setCardHolder(email)
                                    .setDescription("The card has been destroyed")
                                    .build();
                            account.addTransaction(removeCardTransaction);

                            OneTimePayCard newCard = new OneTimePayCard(Utils.generateCardNumber());

                            account.addCard(newCard);

                            Transaction addCardTransaction = new Transaction.Builder()
                                    .setTimestamp(timestamp)
                                    .setCardHolder(email)
                                    .setCard(newCard.getCardNumber())
                                    .setAccount(account.getIban())
                                    .setDescription("New card created")
                                    .build();
                            account.addTransaction(addCardTransaction);
                        }

                        return;
                    }

                    Transaction insufficientFundsTransaction = new Transaction.Builder()
                            .setTimestamp(timestamp)
                            .setDescription("Insufficient funds")
                            .build();

                    account.addTransaction(insufficientFundsTransaction);
                    return;
                }
            }
        }
        ObjectNode outputNode = result.putObject("output");
        outputNode.put("timestamp", timestamp);
        outputNode.put("description", "Card not found");
        result.put("timestamp", timestamp);
        output.add(result);
    }

}

package org.poo.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.*;

import java.text.DecimalFormat;

public class PayOnline implements Command {
    private String cardNumber;
    private double amount;
    private String currency;
    private final int timestamp;
    private String description;
    private String commerciant;
    private String email;
    private UserRepo userRepo;

    public PayOnline(String cardNumber, double amount, String currency, int timestamp, String description, String commerciant, String email, UserRepo userRepo) {
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
    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("command", "payOnline");
        result.put("timestamp", timestamp);

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

                    double convertedAmount = convertCurrency(amount, currency, account.getCurrency());
                    if (account.getBalance() >= convertedAmount && card.canPerformTransaction()) {
                        account.setBalance(account.getBalance() - convertedAmount);

                        Transaction transaction = new Transaction.Builder()
                                .setTimestamp(timestamp)
                                .setDescription("Card payment")
                                .setAmount(convertedAmount)
                                .setCommerciant(commerciant)
                                .build();

                        account.addTransaction(transaction);
                        if(card.getCardType().equals("OneTimePayCard")) {
                            ((OneTimePayCard) card).setUsed(true);
                            card.setBlocked(true);
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
        output.add(result);
    }

    private double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        try {
            double rate = userRepo.getExchangeRate(fromCurrency, toCurrency);
            return amount * rate;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Exchange rate not found for " + fromCurrency + " to " + toCurrency);
        }
    }
}

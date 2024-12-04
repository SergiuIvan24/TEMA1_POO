package org.poo.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.Account;
import org.poo.entities.Transaction;
import org.poo.entities.User;
import org.poo.entities.UserRepo;

public class SendMoney implements Command {
    private String senderIBAN;
    private String receiverIBAN;
    private double amount;
    private final int timestamp;
    private String senderEmail;
    private String description;
    private UserRepo userRepo;

    public SendMoney(String senderIBAN, String receiverIBAN, double amount, int timestamp, String email, String description, UserRepo userRepo) {
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.timestamp = timestamp;
        this.senderEmail = email;
        this.description = description;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(ArrayNode output) {
        User sender = userRepo.getUser(senderEmail);
        if (sender == null) {
            return;
        }

        Account senderAccount = sender.getAccount(senderIBAN);
        if (senderAccount == null) {
            return;
        }

        String actualReceiverIBAN = userRepo.getIBANByAlias(receiverIBAN);
        if (actualReceiverIBAN == null) {
            actualReceiverIBAN = receiverIBAN;
        }

        User receiver = userRepo.getUserByIBAN(actualReceiverIBAN);
        if (receiver == null) {
            return;
        }

        Account receiverAccount = receiver.getAccount(actualReceiverIBAN);
        if (receiverAccount == null) {
            return;
        }

        if (senderAccount.getBalance() < amount) {
            Transaction insufficientFundsTransaction = new Transaction.Builder()
                    .setTimestamp(timestamp)
                    .setDescription("Insufficient funds")
                    .build();
            senderAccount.addTransaction(insufficientFundsTransaction);
            return;
        }


        double convertedAmount;
        try {
            convertedAmount = userRepo.getExchangeRate(senderAccount.getCurrency(), receiverAccount.getCurrency()) * amount;
        } catch (IllegalArgumentException e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("error", e.getMessage());
            errorNode.put("timestamp", timestamp);
            output.add(errorNode);
            return;
        }

        String senderAmountWithCurrency = String.valueOf(amount) + " " + senderAccount.getCurrency();
        String receiverAmountWithCurrency = String.valueOf(convertedAmount) + " " + receiverAccount.getCurrency();

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + convertedAmount);

        Transaction senderTransaction = new Transaction.Builder()
                .setTimestamp(timestamp)
                .setDescription(description)
                .setSenderIBAN(senderIBAN)
                .setReceiverIBAN(receiverIBAN)
                .setAmountPlusCurrency(senderAmountWithCurrency)
                .setTransferType("sent")
                .build();
        senderAccount.addTransaction(senderTransaction);

        Transaction receiverTransaction = new Transaction.Builder()
                .setTimestamp(timestamp)
                .setDescription(description)
                .setSenderIBAN(senderIBAN)
                .setReceiverIBAN(receiverIBAN)
                .setAmountPlusCurrency(receiverAmountWithCurrency)
                .setTransferType("received")
                .build();
        receiverAccount.addTransaction(receiverTransaction);
    }
}

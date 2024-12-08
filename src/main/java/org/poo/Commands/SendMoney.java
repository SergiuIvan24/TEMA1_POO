package org.poo.Commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.entities.Account;
import org.poo.entities.Transaction;
import org.poo.entities.User;
import org.poo.entities.UserRepo;

public final class SendMoney implements Command {
    private String senderIBAN;
    private String receiverIBAN;
    private double amount;
    private final int timestamp;
    private String senderEmail;
    private String description;
    private UserRepo userRepo;

    public SendMoney(final String senderIBAN, final String receiverIBAN,
                     final double amount, final int timestamp,
                     final String email, final String description,
                     final UserRepo userRepo) {
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.timestamp = timestamp;
        this.senderEmail = email;
        this.description = description;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(final ArrayNode output) {
        User sender = userRepo.getUser(senderEmail);
        if (sender == null) {
            return;
        }

        Account senderAccount = sender.getAccount(senderIBAN);
        if (senderAccount == null) {
            return;
        }

        String realReceiverIBAN = userRepo.getIBANByAlias(receiverIBAN);
        if (realReceiverIBAN == null) {
            Account receiverAccountCheck = userRepo.getAccountByIBAN(receiverIBAN);
            if (receiverAccountCheck != null) {
                realReceiverIBAN = receiverIBAN;
            } else {
                return;
            }
        }

        User receiver = userRepo.getUserByIBAN(realReceiverIBAN);
        if (receiver == null) {
            return;
        }

        Account receiverAccount = receiver.getAccount(realReceiverIBAN);

        if (receiverAccount == null) {
            return;
        }
        double newSenderBalance = senderAccount.getBalance() - amount;

        if (senderAccount.getBalance() < amount) {
            Transaction insufficientFundsTransaction = new Transaction.Builder()
                    .setTimestamp(timestamp)
                    .setDescription("Insufficient funds")
                    .build();
            senderAccount.addTransaction(insufficientFundsTransaction);
            return;
        }

        double convertedAmount;
        convertedAmount = userRepo.getExchangeRate(senderAccount.getCurrency(),
                receiverAccount.getCurrency()) * amount;

        String senderAmountWithCurrency =
                String.valueOf(amount) + " " + senderAccount.getCurrency();
        String receiverAmountWithCurrency =
                String.valueOf(convertedAmount) + " " + receiverAccount.getCurrency();

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

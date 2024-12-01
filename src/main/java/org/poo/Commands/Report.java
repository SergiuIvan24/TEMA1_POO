package org.poo.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.Account;
import org.poo.entities.Transaction;
import org.poo.entities.User;
import org.poo.entities.UserRepo;

public class Report implements Command {
    private final int startTimestamp;
    private final int endTimestamp;
    private final String accountIBAN;
    private final int timestamp;
    private final UserRepo userRepo;

    public Report(int startTimestamp, int endTimestamp, String accountIBAN, UserRepo userRepo, int timestamp) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.accountIBAN = accountIBAN;
        this.userRepo = userRepo;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(ArrayNode output) {
        User user = userRepo.getUserByIBAN(accountIBAN);
        if (user == null) {
            throw new IllegalArgumentException("User not found for account IBAN: " + accountIBAN);
        }

        Account account = user.getAccount(accountIBAN);
        if (account == null) {
            throw new IllegalArgumentException("Account not found for IBAN: " + accountIBAN);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode reportNode = objectMapper.createObjectNode();
        reportNode.put("command", "report");

        ObjectNode accountDetails = objectMapper.createObjectNode();
        accountDetails.put("IBAN", account.getIBAN());
        accountDetails.put("balance", account.getBalance());
        accountDetails.put("currency", account.getCurrency());

        ArrayNode transactionsArray = objectMapper.createArrayNode();
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getTimestamp() >= startTimestamp && transaction.getTimestamp() <= endTimestamp) {
                transactionsArray.add(createReportOutput(transaction, objectMapper));
            }
        }

        accountDetails.set("transactions", transactionsArray);

        reportNode.set("output", accountDetails);
        reportNode.put("timestamp", timestamp);

        output.add(reportNode);
    }

    private ObjectNode createReportOutput(Transaction transaction, ObjectMapper objectMapper) {
        ObjectNode transactionNode = objectMapper.createObjectNode();
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        if (transaction.getAmount() != -1) {
            transactionNode.put("amount", transaction.getAmount());
        }
        if (transaction.getAmountPlusCurrency() != null) {
            transactionNode.put("amount", transaction.getAmountPlusCurrency());
        }
        if (transaction.getCurrency() != null) {
            transactionNode.put("currency", transaction.getCurrency());
        }
        if (transaction.getSenderIBAN() != null) {
            transactionNode.put("senderIBAN", transaction.getSenderIBAN());
        }
        if (transaction.getReceiverIBAN() != null) {
            transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
        }
        if (transaction.getCommerciant() != null) {
            transactionNode.put("commerciant", transaction.getCommerciant());
        }
        if (transaction.getCard() != null) {
            transactionNode.put("card", transaction.getCard());
        }
        if (transaction.getCardHolder() != null) {
            transactionNode.put("cardHolder", transaction.getCardHolder());
        }
        if (transaction.getAccount() != null) {
            transactionNode.put("account", transaction.getAccount());
        }
        if (transaction.getTransferType() != null) {
            transactionNode.put("transferType", transaction.getTransferType());
        }

        return transactionNode;
    }
}

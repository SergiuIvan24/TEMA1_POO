package org.poo.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.Account;
import org.poo.entities.Transaction;
import org.poo.entities.User;
import org.poo.entities.UserRepo;

public class PrintTransactions implements Command {
    private String email;
    private int timestamp;
    private UserRepo userRepo;

    public PrintTransactions(String email, int timestamp, UserRepo userRepo) {
        this.email = email;
        this.timestamp = timestamp;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(ArrayNode output) {
        User user = userRepo.getUser(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found for email: " + email);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "printTransactions");

        ArrayNode transactionsArray = objectMapper.createArrayNode();
        for (Account account : user.getAccounts()) {
            for (Transaction transaction : account.getTransactions()) {
                ObjectNode transactionNode = objectMapper.createObjectNode();
                transactionNode.put("timestamp", transaction.getTimestamp());
                transactionNode.put("description", transaction.getDescription());
                if (transaction.getSenderIBAN() != null) {
                    transactionNode.put("senderIBAN", transaction.getSenderIBAN());
                }
                if (transaction.getReceiverIBAN() != null) {
                    transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
                }
                if (transaction.getCurrency() != null) {
                    transactionNode.put("currency", transaction.getCurrency());
                }
                if (transaction.getAmount() != -1) {
                    transactionNode.put("amount", transaction.getAmount());
                }
                if(transaction.getAmountPlusCurrency() != null) {
                    transactionNode.put("amount", transaction.getAmountPlusCurrency());
                }
                if (transaction.getCommerciant() != null) {
                    transactionNode.put("commerciant", transaction.getCommerciant());
                }
                if (transaction.getTransferType() != null) {
                    transactionNode.put("transferType", transaction.getTransferType());
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
                if (transaction.getInvolvedAccounts() != null && !transaction.getInvolvedAccounts().isEmpty()) {
                    ArrayNode involvedAccountsNode = objectMapper.createArrayNode();
                    for (String iban : transaction.getInvolvedAccounts()) {
                        involvedAccountsNode.add(iban);
                    }
                    transactionNode.set("involvedAccounts", involvedAccountsNode);
                }

                transactionsArray.add(transactionNode);
            }
        }

        resultNode.set("output", transactionsArray);
        resultNode.put("timestamp", timestamp);
        output.add(resultNode);
    }



}

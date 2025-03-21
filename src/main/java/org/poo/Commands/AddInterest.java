package org.poo.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.*;

public final class AddInterest implements Command {
    private String accountIBAN;
    private final int timestamp;
    private UserRepo userRepo;


    public AddInterest(final String accountIBAN, final int timestamp, final UserRepo userRepo) {
        this.accountIBAN = accountIBAN;
        this.timestamp = timestamp;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(final ArrayNode output) {
        User user = userRepo.getUserByIBAN(accountIBAN);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("command", "addInterest");
        result.put("timestamp", timestamp);

        Account account = user.getAccount(accountIBAN);
        if (!account.getAccountType().equals("savings")) {
            ObjectNode errorOutput = objectMapper.createObjectNode();
            errorOutput.put("description", "This is not a savings account");
            errorOutput.put("timestamp", timestamp);
            result.set("output", errorOutput);
            output.add(result);
            errorOutput.put("timestamp", timestamp);
            return;
        }

        SavingsAccount savingsAccount = (SavingsAccount) account;
        double interestRate = savingsAccount.getInterestRate();
        double interest = savingsAccount.getBalance() * interestRate;

        savingsAccount.setBalance(savingsAccount.getBalance() + interest);

        Transaction transaction = new Transaction.Builder()
                .setTimestamp(timestamp)
                .setDescription("Interest earned")
                .setAmount(interest)
                .setAccount(accountIBAN)
                .build();
        savingsAccount.addTransaction(transaction);

    }
}

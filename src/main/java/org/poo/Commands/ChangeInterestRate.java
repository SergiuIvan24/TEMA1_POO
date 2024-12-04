package org.poo.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.Account;
import org.poo.entities.SavingsAccount;
import org.poo.entities.User;
import org.poo.entities.UserRepo;

public class ChangeInterestRate implements Command {
    private String accountIBAN;
    private double interestRate;
    private final int timestamp;
    private UserRepo userRepo;

    public ChangeInterestRate(String accountIBAN, double interestRate, int timestamp, UserRepo userRepo) {
        this.accountIBAN = accountIBAN;
        this.interestRate = interestRate;
        this.timestamp = timestamp;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("command", "changeInterestRate");
        result.put("timestamp", timestamp);
        User user = userRepo.getUserByIBAN(accountIBAN);
        if (user == null) {
            return;
        }

        Account account = user.getAccount(accountIBAN);
        if (account == null) {
            return;
        }
        if(!account.getAccountType().equals("savings")) {
            ObjectNode errorOutput = objectMapper.createObjectNode();
            errorOutput.put("description", "This is not a savings account");
            errorOutput.put("timestamp", timestamp);
            result.set("output", errorOutput);
            output.add(result);
            return;
        }

        try {
            account.setInterestRate(interestRate);
        } catch (UnsupportedOperationException e) {
        }
    }
}

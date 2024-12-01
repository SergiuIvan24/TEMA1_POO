package org.poo.Commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
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
        User user = userRepo.getUserByIBAN(accountIBAN);
        if (user == null) {
            return;
        }

        Account account = user.getAccount(accountIBAN);
        if (account == null) {
            return;
        }

        try {
            account.setInterestRate(interestRate);
        } catch (UnsupportedOperationException e) {
        }
    }
}

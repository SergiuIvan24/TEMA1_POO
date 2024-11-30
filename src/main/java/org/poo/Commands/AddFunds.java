package org.poo.Commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.entities.Account;
import org.poo.entities.User;
import org.poo.entities.UserRepo;

class AddFunds implements Command {
    private final String accountIBAN;
    private final double amount;
    private final UserRepo userRepo;
    private final int timestamp;

    public AddFunds(String accountIBAN, double amount, UserRepo userRepo, int timestamp) {
        this.accountIBAN = accountIBAN;
        this.amount = amount;
        this.userRepo = userRepo;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(ArrayNode output) {
            User user = userRepo.getUserByIBAN(accountIBAN);
            if (user == null) {
                throw new IllegalArgumentException("User not found for IBAN: " + accountIBAN);
            }

            Account account = user.getAccount(accountIBAN);
            if (account == null) {
                throw new IllegalArgumentException("Account not found for IBAN: " + accountIBAN);
            }

            account.setBalance(account.getBalance() + amount);

    }
}

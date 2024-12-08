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

    AddFunds(final String accountIBAN, final double amount,
                    final UserRepo userRepo, final int timestamp) {
        this.accountIBAN = accountIBAN;
        this.amount = amount;
        this.userRepo = userRepo;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(final ArrayNode output) {
            User user = userRepo.getUserByIBAN(accountIBAN);

            Account account = user.getAccount(accountIBAN);

            account.setBalance(account.getBalance() + amount);

    }
}

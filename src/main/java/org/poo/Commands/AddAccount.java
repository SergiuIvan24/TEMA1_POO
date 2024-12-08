package org.poo.Commands;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.entities.*;
import org.poo.utils.Utils;

class AddAccount implements Command {
    private final UserRepo userRepo;
    private final String email;
    private final String currency;
    private final String accountType;
    private final int timestamp;
    private final Double interestRate;

    AddAccount(final UserRepo userRepo, final String email, final String currency,
               final String accountType, final int timestamp, final Double interestRate) {
        this.userRepo = userRepo;
        this.email = email;
        this.currency = currency;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.interestRate = interestRate;
    }

    @Override
    public void execute(final ArrayNode output) {
        User user = userRepo.getUser(email);
        Account newAccount;

        if (accountType.equalsIgnoreCase("savings")) {
            newAccount = new SavingsAccount(
                    Utils.generateIBAN(),
                    currency,
                    0,
                    "savings",
                    interestRate
            );
        } else {
            newAccount = new ClassicAccount(
                    Utils.generateIBAN(),
                    currency,
                    0,
                    "classic"
            );
        }

        user.addAccount(newAccount);

        Transaction transaction = new Transaction.Builder()
                .setTimestamp(timestamp)
                .setDescription("New account created")
                .build();

        newAccount.addTransaction(transaction);
    }
}


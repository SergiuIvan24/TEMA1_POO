package org.poo.Commands;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.*;
import org.poo.utils.Utils;

class AddAccount implements Command {
    private final UserRepo userRepo;
    private final String email;
    private final String currency;
    private final String accountType;
    private final int timestamp;
    private Double interestRate;

    public AddAccount(UserRepo userRepo, String email, String currency, String accountType, int timestamp, Double interestRate) {
        this.userRepo = userRepo;
        this.email = email;
        this.currency = currency;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.interestRate = interestRate;
    }

    @Override
    public void execute(ArrayNode output) {
        User user = userRepo.getUser(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found for email: " + email);
        }

        Account newAccount;
        if (accountType.equalsIgnoreCase("savings")) {
            if (interestRate == null) {
                throw new IllegalArgumentException("Interest rate must be provided for savings accounts");
            }
            newAccount = new SavingsAccount(Utils.generateIBAN(), currency, 0, "savings", interestRate);
        } else if (accountType.equalsIgnoreCase("classic")) {
            newAccount = new ClassicAccount(Utils.generateIBAN(), currency, 0, "classic");
        } else {
            throw new IllegalArgumentException("Unknown account type: " + accountType);
        }

        user.addAccount(newAccount);

        Transaction transaction = new Transaction.Builder()
                .setTimestamp(timestamp)
                .setDescription("New account created")
                .build();

        newAccount.addTransaction(transaction);
    }
}

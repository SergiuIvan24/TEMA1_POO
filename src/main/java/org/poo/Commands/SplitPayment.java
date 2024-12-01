package org.poo.Commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.entities.Account;
import org.poo.entities.Transaction;
import org.poo.entities.User;
import org.poo.entities.UserRepo;

import java.util.ArrayList;
import java.util.List;

public class SplitPayment implements Command {
    private List<String> accountsIban;
    private double amount;
    private String currency;
    private final int timestamp;
    private UserRepo userRepo;

    public SplitPayment(List<String> accountsIban, double amount, String currency, int timestamp, UserRepo userRepo) {
        this.accountsIban = accountsIban;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(ArrayNode output) {
        double amountPerAccount = amount / accountsIban.size();
        List<Account> participatingAccounts = new ArrayList<>();
        boolean hasInsufficientFunds = false;
        List<String> involvedAccounts = new ArrayList<>(accountsIban);

        for (String iban : accountsIban) {
            Account account = findAccountByIban(iban);
            if (account == null) {
                hasInsufficientFunds = true;
                break;
            }
            double amountInAccountCurrency = amountPerAccount;
            if (!account.getCurrency().equals(currency)) {
                try {
                    amountInAccountCurrency = convertCurrency(amountPerAccount, currency, account.getCurrency());
                } catch (IllegalArgumentException e) {
                    hasInsufficientFunds = true;
                    break;
                }
            }
            if (account.getBalance() < amountInAccountCurrency) {
                hasInsufficientFunds = true;
                break;
            }
            participatingAccounts.add(account);
        }

        if (hasInsufficientFunds) {
            return;
        }

        for (Account account : participatingAccounts) {
            double amountInAccountCurrency = amountPerAccount;
            if (!account.getCurrency().equals(currency)) {
                amountInAccountCurrency = convertCurrency(amountPerAccount, currency, account.getCurrency());
            }
            account.setBalance(account.getBalance() - amountInAccountCurrency);

            Transaction successfulTransaction = new Transaction.Builder()
                    .setTimestamp(timestamp)
                    .setDescription(String.format("Split payment of %.2f %s", amount, currency))
                    .setCurrency(currency)
                    .setAmount(amountPerAccount)
                    .setTransferType(null)
                    .setInvolvedAccounts(involvedAccounts)
                    .build();
            account.addTransaction(successfulTransaction);
        }
    }

    /**
     * Metodă pentru a găsi un cont pe baza IBAN-ului.
     */
    private Account findAccountByIban(String iban) {
        for (User user : userRepo.getAllUsers()) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(iban)) {
                    return account;
                }
            }
        }
        return null;
    }

    /**
     * Metodă pentru a converti valuta folosind ratele din userRepo.
     */
    private double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        double rate = userRepo.getExchangeRate(fromCurrency, toCurrency);
        return amount / rate; // Divide instead of multiply
    }
}

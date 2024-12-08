package org.poo.Commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.entities.Account;
import org.poo.entities.Transaction;
import org.poo.entities.User;
import org.poo.entities.UserRepo;

import java.util.ArrayList;
import java.util.List;

public final class SplitPayment implements Command {
    private List<String> accountsIban;
    private double amount;
    private String currency;
    private final int timestamp;
    private UserRepo userRepo;

    public SplitPayment(final List<String> accountsIban, final double amount,
                        final String currency, final int timestamp, final UserRepo userRepo) {
        this.accountsIban = accountsIban;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(final ArrayNode output) {
        double amountPerAccount = amount / accountsIban.size();
        List<Account> allAccounts = new ArrayList<>();
        String notEnoughMoneyIban = null;
        boolean hasInsufficientFunds = false;

        for (int i = accountsIban.size() - 1; i >= 0; i--) {
            String iban = accountsIban.get(i);
            Account account = findAccountByIban(iban);
            double amountInAccountCurrency = amountPerAccount;
            amountInAccountCurrency = convertCurrency(amountPerAccount,
                    currency, account.getCurrency());
            if (account.getBalance() < amountInAccountCurrency) {
                notEnoughMoneyIban = iban;
                hasInsufficientFunds = true;
                break;
            }
            allAccounts.add(account);
        }

        if (hasInsufficientFunds) {
            for (String iban : accountsIban) {
                Account account = findAccountByIban(iban);
                if (account != null) {
                    Transaction errorTransaction = new Transaction.Builder()
                            .setTimestamp(timestamp)
                            .setDescription(String.format("Split payment of %.2f %s",
                                    amount, currency))
                            .setCurrency(currency)
                            .setAmount(amountPerAccount)
                            .setInvolvedAccounts(accountsIban)
                            .setError(String.format(
                                    "Account %s has insufficient funds for a split payment.",
                                    notEnoughMoneyIban))
                            .build();
                    account.addTransaction(errorTransaction);
                }
            }
            return;
        }

        for (Account account : allAccounts) {
            double amountInAccountCurrency = amountPerAccount;
            if (!account.getCurrency().equals(currency)) {
                amountInAccountCurrency = convertCurrency(amountPerAccount,
                        currency, account.getCurrency());
            }
            account.setBalance(account.getBalance() - amountInAccountCurrency);

            Transaction successfulTransaction = new Transaction.Builder()
                    .setTimestamp(timestamp)
                    .setDescription(String.format("Split payment of %.2f %s", amount, currency))
                    .setCurrency(currency)
                    .setAmount(amountPerAccount)
                    .setInvolvedAccounts(accountsIban)
                    .build();
            account.addTransaction(successfulTransaction);
        }
    }

    private Account findAccountByIban(final String iban) {
        for (User user : userRepo.getAllUsers()) {
            for (Account account : user.getAccounts()) {
                if (account.getIban().equals(iban)) {
                    return account;
                }
            }
        }
        return null;
    }

    private double convertCurrency(final double amount,
                                   final String fromCurrency, final String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        Double rate = userRepo.getExchangeRate(fromCurrency, toCurrency);
        if (rate != null) {
            return amount * rate;
        }
        Double inverseRate = userRepo.getExchangeRate(toCurrency, fromCurrency);
        if (inverseRate != null) {
            return amount / inverseRate;
        }
        return -1;
    }
}

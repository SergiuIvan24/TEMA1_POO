package org.poo.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        String errorIban = null;
        boolean hasInsufficientFunds = false;

        for (String iban : accountsIban) {
            Account account = findAccountByIban(iban);
            if (account == null) {
                errorIban = iban;
                hasInsufficientFunds = true;
                break;
            }
            double amountInAccountCurrency = amountPerAccount;
            if (!account.getCurrency().equals(currency)) {
                try {
                    amountInAccountCurrency = convertCurrency(amountPerAccount, currency, account.getCurrency());
                } catch (IllegalArgumentException e) {
                    errorIban = iban;
                    hasInsufficientFunds = true;
                    break;
                }
            }
            if (account.getBalance() < amountInAccountCurrency) {
                errorIban = iban;
                hasInsufficientFunds = true;
                break;
            }
            participatingAccounts.add(account);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();

        if (hasInsufficientFunds) {
            for (String iban : accountsIban) {
                Account account = findAccountByIban(iban);
                if (account != null) {
                    Transaction errorTransaction = new Transaction.Builder()
                            .setTimestamp(timestamp)
                            .setDescription(String.format("Split payment of %.2f %s", amount, currency))
                            .setCurrency(currency)
                            .setAmount(amountPerAccount)
                            .setInvolvedAccounts(accountsIban)
                            .setError(String.format("Account %s has insufficient funds for a split payment.", errorIban))
                            .build();
                    account.addTransaction(errorTransaction);
                }
            }

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
                    .setInvolvedAccounts(accountsIban)
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

        // Try to find a direct exchange rate from 'toCurrency' to 'fromCurrency'
        Double rate = userRepo.getExchangeRate(fromCurrency, toCurrency);
        if (rate != null) {
            return amount / rate; // Divide instead of multiply
        }

        // Try to find inverse rate
        Double inverseRate = userRepo.getExchangeRate(toCurrency, fromCurrency);
        if (inverseRate != null) {
            return amount * inverseRate; // Multiply
        }

        // If all else fails, throw an exception
        throw new IllegalArgumentException("Exchange rate from " + fromCurrency + " to " + toCurrency + " not found.");
    }

    private Double getRate(String baseCurrency, String targetCurrency) {
        if (baseCurrency.equals(targetCurrency)) {
            return 1.0;
        }

        // Try direct rate
        Double rate = userRepo.getExchangeRate(baseCurrency, targetCurrency);
        if (rate != null) {
            return rate;
        }

        // Try inverse rate
        Double inverseRate = userRepo.getExchangeRate(targetCurrency, baseCurrency);
        if (inverseRate != null) {
            return 1 / inverseRate;
        }

        return null;
    }


}

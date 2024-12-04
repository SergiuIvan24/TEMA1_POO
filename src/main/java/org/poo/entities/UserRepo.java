package org.poo.entities;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class UserRepo {
    private Map<String, User> users = new LinkedHashMap<>();
    private List<ExchangeRate> exchangeRates = new ArrayList<>();

    public UserRepo() {
    }

    public void addUser(User user) {
        users.put(user.getEmail(), user);
    }

    public User getUser(String email) {
        return users.get(email);
    }

    public void addExchangeRate(String from, String to, double rate) {
        exchangeRates.add(new ExchangeRate(from, to, rate));
    }

    public double getExchangeRate(String from, String to) {
        return getExchangeRate(from, to, new HashSet<>());
    }

    private double getExchangeRate(String from, String to, Set<String> visitedCurrencies) {
        if (from.equals(to)) {
            return 1.0;
        }

        visitedCurrencies.add(from);

        for (ExchangeRate rate : exchangeRates) {
            if (rate.getFrom().equals(from) && rate.getTo().equals(to)) {
                return rate.getRate();
            }
            if (rate.getFrom().equals(to) && rate.getTo().equals(from)) {
                return 1.0 / rate.getRate();
            }
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, Double> rates = new HashMap<>();
        queue.add(from);
        rates.put(from, 1.0);

        while (!queue.isEmpty()) {
            String currentCurrency = queue.poll();
            double currentRate = rates.get(currentCurrency);

            for (ExchangeRate rate : exchangeRates) {
                String nextCurrency = null;
                double nextRate = 0.0;

                if (rate.getFrom().equals(currentCurrency) && !visitedCurrencies.contains(rate.getTo())) {
                    nextCurrency = rate.getTo();
                    nextRate = rate.getRate();
                } else if (rate.getTo().equals(currentCurrency) && !visitedCurrencies.contains(rate.getFrom())) {
                    nextCurrency = rate.getFrom();
                    nextRate = 1.0 / rate.getRate();
                }

                if (nextCurrency != null) {
                    double totalRate = currentRate * nextRate;
                    if (nextCurrency.equals(to)) {
                        return totalRate;
                    }
                    if (!rates.containsKey(nextCurrency)) {
                        rates.put(nextCurrency, totalRate);
                        queue.add(nextCurrency);
                    }
                }
            }
        }

        throw new IllegalArgumentException("Exchange rate not found for " + from + " to " + to);
    }

    public String getIBANByAlias(String alias) {
        for (User user : users.values()) {
            for (Account account : user.getAccounts()) {
                if (alias.equals(account.getAlias())) {
                    return account.getIBAN();
                }
            }
        }
        return null;
    }

    public User getUserByIBAN(String IBAN) {
        for (User user : users.values()) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(IBAN)) {
                    return user;
                }
            }
        }
        return null;
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User getUserByCardNumber(String cardNumber) {
        for (User user : users.values()) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        return user;
                    }
                }
            }
        }
        return null;
    }

    public boolean deleteAccount(String email, String iban) {
        User user = getUser(email);
        if (user == null) {
            return false;
        }
        return user.getAccounts().removeIf(account -> account.getIBAN().equals(iban) && account.getBalance() == 0);
    }

    public ArrayNode toJson(ObjectMapper objectMapper) {
        ArrayNode usersArray = objectMapper.createArrayNode();
        for (User user : users.values()) {
            usersArray.add(user.toJson(objectMapper));
        }
        return usersArray;
    }

    public void printUsers() {
        users.values().forEach(System.out::println);
    }

    public Account getAccountByIBAN(String accountIBAN) {
        for (User user : users.values()) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(accountIBAN)) {
                    return account;
                }
            }
        }
        return null;
    }
}

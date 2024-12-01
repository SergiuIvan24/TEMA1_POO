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

        for (ExchangeRate rate : exchangeRates) {
            String intermediateCurrency = null;
            double rateToIntermediate = 0.0;

            if (rate.getFrom().equals(from) && !visitedCurrencies.contains(rate.getTo())) {
                intermediateCurrency = rate.getTo();
                rateToIntermediate = rate.getRate();
            } else if (rate.getTo().equals(from) && !visitedCurrencies.contains(rate.getFrom())) {
                intermediateCurrency = rate.getFrom();
                rateToIntermediate = 1.0 / rate.getRate();
            }

            if (intermediateCurrency != null) {
                try {
                    double rateFromIntermediate = getExchangeRate(intermediateCurrency, to, visitedCurrencies);
                    return rateToIntermediate * rateFromIntermediate;
                } catch (IllegalArgumentException e) {
                }
            }
        }

        throw new IllegalArgumentException("Exchange rate not found for " + from + " to " + to);
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
}

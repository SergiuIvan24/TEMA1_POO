package org.poo.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.entities.User;

import java.util.*;

public class UserRepo {
    private Map<String, User> users = new LinkedHashMap<>();

    public void addUser(User user) {
        users.put(user.getEmail(), user);
    }

    public User getUser(String email) {
        return users.get(email);
    }

    private List<ExchangeRate> exchangeRates = new ArrayList<>();

    public void addExchangeRate(String from, String to, double rate) {
        exchangeRates.add(new ExchangeRate(from, to, rate));
    }

    public double getExchangeRate(String from, String to) {
        System.out.println("Looking for exchange rate: " + from + " -> " + to);

        for (ExchangeRate rate : exchangeRates) {
            if (rate.getFrom().equals(from) && rate.getTo().equals(to)) {
                return rate.getRate();
            }
            if (rate.getFrom().equals(to) && rate.getTo().equals(from)) {
                double inverseRate = 1.0 / rate.getRate();
                return inverseRate;
            }
        }
        for (ExchangeRate rate : exchangeRates) {
            String intermediateCurrency = null;
            double rateToIntermediate = 0.0;

            if (rate.getFrom().equals(from)) {
                intermediateCurrency = rate.getTo();
                rateToIntermediate = rate.getRate();
            }

            else if (rate.getTo().equals(from)) {
                intermediateCurrency = rate.getFrom();
                rateToIntermediate = 1.0 / rate.getRate();
                System.out.println("Using inverse rate for intermediate: " + rate.getTo() + " -> " + rate.getFrom());
            }

            if (intermediateCurrency != null) {
                try {
                    double rateFromIntermediate = getExchangeRate(intermediateCurrency, to);
                    double totalRate = rateToIntermediate * rateFromIntermediate;
                    return totalRate;
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

    public void printUsers() {
        users.values().forEach(System.out::println);
    }

    public ArrayNode toJson(ObjectMapper objectMapper) {
        ArrayNode usersArray = objectMapper.createArrayNode();
        for (User user : users.values()) {
            usersArray.add(user.toJson(objectMapper));
        }
        return usersArray;
    }

    public boolean deleteAccount(String email, String iban) {
        User user = getUser(email);
        if (user == null) {
            return false;
        }
        return user.getAccounts().removeIf(account -> account.getIBAN().equals(iban) && account.getBalance() == 0);
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
}

package org.poo.entities;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public final class UserRepo {
    private Map<String, User> users = new LinkedHashMap<>();
    private List<ExchangeRate> exchangeRates = new ArrayList<>();

    public UserRepo() {
    }

    /**
     * Adauga un utilizator in lista de utilizatori
     * @param user utilizatorul care trebuie adaugat
     */
    public void addUser(final User user) {
        users.put(user.getEmail(), user);
    }
    /**
     * Gaseste un utilizator dupa email
     * @param email email-ul utilizatorului
     */
    public User getUser(final String email) {
        return users.get(email);
    }
    /**
     * Adauga un exchange rate in lista de exchange rates
     * @param from valuta de la care se face conversia
     * @param to valuta in care se face conversia
     * @param rate rata de schimb
     */
    public void addExchangeRate(final String from, final String to, final double rate) {
        exchangeRates.add(new ExchangeRate(from, to, rate));
    }
    /**
     * Returneaza rata de schimb dintre doua valute
     * @param from valuta de la care se face conversia
     * @param to valuta in care se face conversia
     */
    public double getExchangeRate(final String from, final String to) {
        return getExchangeRate(from, to, new HashSet<>());
    }
    /**
     * Returneaza rata de schimb dintre doua valute, cautand si conversii
     * indirecte/inverse
     * @param from valuta de la care se face conversia
     * @param to valuta in care se face conversia
     * @param visitedCurr set cu valutele vizitate
     */
    private double getExchangeRate(final String from, final String to,
                                   final Set<String> visitedCurr) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Currency values cannot be null");
        }
        if (from.equals(to)) {
            return 1.0;
        }

        visitedCurr.add(from);

        for (ExchangeRate rate : exchangeRates) {
            if (rate.getFrom().equals(from) && rate.getTo().equals(to)) {
                return rate.getRate();
            }
            if (rate.getFrom().equals(to) && rate.getTo().equals(from)) {
                return 1.0 / rate.getRate();
            }
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, Double> conversionRates = new HashMap<>();
        queue.add(from);
        conversionRates.put(from, 1.0);

        while (!queue.isEmpty()) {
            String currentCurrency = queue.remove();
            double currentRate = conversionRates.get(currentCurrency);

            for (ExchangeRate rate : exchangeRates) {
                if (rate.getFrom().equals(currentCurrency) && !visitedCurr.contains(rate.getTo())) {
                    double totalRate = currentRate * rate.getRate();
                    if (rate.getTo().equals(to)) {
                        return totalRate;
                    }
                    if (!conversionRates.containsKey(rate.getTo())) {
                        conversionRates.put(rate.getTo(), totalRate);
                        queue.add(rate.getTo());
                    }
                } else if (rate.getTo().equals(currentCurrency)
                        && !visitedCurr.contains(rate.getFrom())) {
                    double totalRate = currentRate * (1.0 / rate.getRate());
                    if (rate.getFrom().equals(to)) {
                        return totalRate;
                    }
                    if (!conversionRates.containsKey(rate.getFrom())) {
                        conversionRates.put(rate.getFrom(), totalRate);
                        queue.add(rate.getFrom());
                    }
                }
            }
        }

        return -1.0;
    }

    /**
     * Returneaza IBAN-ul unui cont dupa alias
     * @param alias alias-ul contului
     */
    public String getIBANByAlias(final String alias) {
        for (User user : users.values()) {
            for (Account account : user.getAccounts()) {
                if (alias.equals(account.getAlias())) {
                    return account.getIban();
                }
            }
        }
        return null;
    }
    /**
     * Returneaza un utilizator dupa IBAN
     * @param iban IBAN-ul contului
     */
    public User getUserByIBAN(final String iban) {
        for (User user : users.values()) {
            for (Account account : user.getAccounts()) {
                if (account.getIban().equals(iban)) {
                    return user;
                }
            }
        }
        return null;
    }
    /**
     * Returneaza toti utilizatorii
     */
    public Collection<User> getAllUsers() {
        return users.values();
    }
    /**
     * Returneaza un utilizator dupa numarul de card
     * @param cardNumber numarul de card
     */
    public User getUserByCardNumber(final String cardNumber) {
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
    /**
     * Sterge un cont dupa email si IBAN
     * @param email email-ul utilizatorului
     * @param iban IBAN-ul contului
     */
    public boolean deleteAccount(final String email, final String iban) {
        User user = getUser(email);
        if (user == null) {
            return false;
        }
        return user.getAccounts()
                .removeIf(account -> account.getIban().equals(iban)
                        && account.getBalance() == 0);
    }
    /**
     * Returneaza un obiect de tip ArrayNode care contine informatii despre utilizatori
     * @param objectMapper obiect de tip ObjectMapper
     */
    public ArrayNode toJson(final ObjectMapper objectMapper) {
        ArrayNode usersArray = objectMapper.createArrayNode();
        for (User user : users.values()) {
            usersArray.add(user.toJson(objectMapper));
        }
        return usersArray;
    }
    /**
     * Afiseaza toti utilizatorii
     */
    public void printUsers() {
        users.values().forEach(System.out::println);
    }
    /**
     * Returneaza un cont dupa IBAN
     * @param accountIBAN IBAN-ul contului
     */
    public Account getAccountByIBAN(final String accountIBAN) {
        for (User user : users.values()) {
            for (Account account : user.getAccounts()) {
                if (account.getIban().equals(accountIBAN)) {
                    return account;
                }
            }
        }
        return null;
    }
}

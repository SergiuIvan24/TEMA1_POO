package org.poo.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<Account> accounts;
    private HashMap<String, String> aliases = new HashMap<>();


    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void deleteAccount(String accountIBAN) {
        accounts.removeIf(account -> account.getIBAN().equals(accountIBAN));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Account getAccount(String iban) {
        for (Account account : accounts) {
            if (account.getIBAN().trim().equals(iban.trim())) {
                return account;
            }
        }
        return null;
    }

    public Account getAccountByCardNumber(String cardNumber) {
        for (Account account : accounts) {
            if (account.getCard(cardNumber) != null) {
                return account;
            }
        }
        return null;
    }

    public void setAlias(String alias, String IBAN) {
        boolean accountExists = accounts.stream()
                .anyMatch(account -> account.getIBAN().equals(IBAN));
        if (!accountExists) {
            throw new IllegalArgumentException("IBAN not found for this user");
        }
        aliases.put(alias, IBAN);
    }

    public String getIBANByAlias(String alias) {
        String IBAN = aliases.get(alias);
        if (IBAN == null) {
            throw new IllegalArgumentException("Alias not found");
        }
        return IBAN;
    }

    public HashMap<String, String> getAliases() {
        return aliases;
    }

    public ObjectNode toJson(ObjectMapper objectMapper) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("firstName", firstName);
        userNode.put("lastName", lastName);
        userNode.put("email", email);

        ArrayNode accountsArray = objectMapper.createArrayNode();
        for (Account account : accounts) {
            accountsArray.add(account.toJson(objectMapper));
        }
        userNode.set("accounts", accountsArray);

        return userNode;
    }
}

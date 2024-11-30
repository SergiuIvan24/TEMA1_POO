package org.poo.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    private String IBAN;
    private String currency;
    private double balance;
    private ArrayList<Card> cards = new ArrayList<Card>();
    private double minimumBalance;
    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public Account(String IBAN, String currency, double balance) {
        this.IBAN = IBAN;
        this.currency = currency;
        this.balance = balance;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public String getIBAN() {
        return IBAN;
    }

    public String getCurrency() {
        return currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Card getCard(String cardNumber) {
        for (Card card : cards) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        return null;
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setInterestRate(double interestRate) {
        throw new UnsupportedOperationException("Interest rate not supported for this account type.");
    }

    public ObjectNode toJson(ObjectMapper objectMapper) {
        ObjectNode accountNode = objectMapper.createObjectNode();
        accountNode.put("IBAN", IBAN);
        accountNode.put("balance", balance);
        accountNode.put("currency", currency);
        accountNode.put("type", this instanceof ClassicAccount ? "classic" : "savings");

        ArrayNode cardsArray = objectMapper.createArrayNode();
        for (Card card : cards) {
            cardsArray.add(card.toJson(objectMapper));
        }
        accountNode.set("cards", cardsArray);

        return accountNode;
    }

    public abstract String getAccountType();


}

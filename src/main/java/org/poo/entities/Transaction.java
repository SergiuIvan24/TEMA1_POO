package org.poo.entities;

import java.util.List;

public class Transaction {
    private final int timestamp;
    private final String description;
    private final String senderIBAN;
    private final String receiverIBAN;
    private final double amount;
    private final String currency;
    private final String transferType;
    private final String card;
    private final String cardHolder;
    private final String account;
    private final String commerciant;
    private final String amountPlusCurrency;
    private final List<String> involvedAccounts;

    private Transaction(Builder builder) {
        this.timestamp = builder.timestamp;
        this.description = builder.description;
        this.senderIBAN = builder.senderIBAN;
        this.receiverIBAN = builder.receiverIBAN;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.transferType = builder.transferType;
        this.card = builder.card;
        this.cardHolder = builder.cardHolder;
        this.account = builder.account;
        this.commerciant = builder.commerciant;
        this.amountPlusCurrency = builder.amountPlusCurrency;
        this.involvedAccounts = builder.involvedAccounts;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getSenderIBAN() {
        return senderIBAN;
    }

    public String getReceiverIBAN() {
        return receiverIBAN;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTransferType() {
        return transferType;
    }

    public String getCard() {
        return card;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public String getAccount() {
        return account;
    }

    public String getCommerciant() {
        return commerciant;
    }

    public String getAmountPlusCurrency() {
        return amountPlusCurrency;
    }

    public List<String> getInvolvedAccounts() {
        return involvedAccounts;
    }

    public static class Builder {
        private int timestamp;
        private String description;
        private String senderIBAN;
        private String receiverIBAN;
        private double amount = -1;
        private String currency;
        private String transferType;
        private String card;
        private String cardHolder;
        private String account;
        private String commerciant;
        private String amountPlusCurrency; // Câmpul pe care îl folosești
        private List<String> involvedAccounts;

        public Builder setTimestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setSenderIBAN(String senderIBAN) {
            this.senderIBAN = senderIBAN;
            return this;
        }

        public Builder setReceiverIBAN(String receiverIBAN) {
            this.receiverIBAN = receiverIBAN;
            return this;
        }

        public Builder setAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setTransferType(String transferType) {
            this.transferType = transferType;
            return this;
        }

        public Builder setCard(String card) {
            this.card = card;
            return this;
        }

        public Builder setCardHolder(String cardHolder) {
            this.cardHolder = cardHolder;
            return this;
        }

        public Builder setAccount(String account) {
            this.account = account;
            return this;
        }

        public Builder setCommerciant(String commerciant) {
            this.commerciant = commerciant;
            return this;
        }

        public Builder setAmountPlusCurrency(String amountPlusCurrency) {
            this.amountPlusCurrency = amountPlusCurrency;
            return this;
        }

        public Builder setInvolvedAccounts(List<String> involvedAccounts) {
            this.involvedAccounts = involvedAccounts;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }
}

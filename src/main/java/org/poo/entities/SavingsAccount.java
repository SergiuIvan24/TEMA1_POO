package org.poo.entities;

public final class SavingsAccount extends Account {
    private String accountType;
    private double interestRate;

    public SavingsAccount(final String iban, final String currency, final double balance,
                          final String accountType, final double interestRate) {
        super(iban, currency, balance);
        this.interestRate = interestRate;
        this.accountType = accountType;
    }

    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String getAccountType() {
        return accountType;
    }
}

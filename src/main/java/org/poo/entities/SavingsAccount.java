package org.poo.entities;

public class SavingsAccount extends Account{
    private String accountType;
    private double interestRate;

    public SavingsAccount(String IBAN, String currency, double balance, String accountType, double interestRate) {
        super(IBAN, currency, balance);
        this.interestRate = interestRate;
        this.accountType = accountType;
    }

    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String getAccountType() {
        return accountType;
    }
}

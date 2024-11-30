package org.poo.entities;

public class ClassicAccount extends Account{
    private String accountType;
    public ClassicAccount(String IBAN, String currency, double balance, String accountType) {
        super(IBAN, currency, balance);
        this.accountType = accountType;
    }

    @Override
    public String getAccountType() {
        return accountType;
    }
}

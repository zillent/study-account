package ru.zillent.study.account;


import java.util.HashMap;

import static ru.zillent.study.account.Currency.*;

public class Account {

    private String accountant;
    private HashMap<Currency, Integer> balance;

    public Account(String accountant) {
        this.accountant = accountant;
        this.balance = new HashMap<>();
    }

    public String getAccountant() {
        return accountant;
    }

    public void setAccountant(String accountant) {
        this.accountant = accountant;
    }

    public HashMap<Currency, Integer> getBalance() {
        return new HashMap<>(balance);
    }

    public void setBalance(Currency currency, int amount) {
        if (currency == null) throw new IllegalArgumentException("Wrong currency");
        if (amount<0) throw new IllegalArgumentException("Amount must be positive");
        balance.put(currency, amount);
    };

    public static void main(String[] args) {
        System.out.println("Test");
        Account account = new Account("Test");
        account.setBalance(RUB,33);
        account.setBalance(USD,33);
        System.out.println(account.getBalance());
    }
}

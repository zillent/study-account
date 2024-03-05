package ru.zillent.study.account;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import static ru.zillent.study.account.Currency.*;

public class Account {

    private String accountant;
    private HashMap<Currency, Integer> balance;

    private Deque<Command> commands = new ArrayDeque<>();

    public Account(String accountant) {
        this.accountant = accountant;
        this.balance = new HashMap<>();
    }

    public String getAccountant() {
        return accountant;
    }

    public void setAccountant(String accountant) {
        String oldAccountant = this.accountant;
        this.commands.push(() -> {this.accountant = oldAccountant;});
        this.accountant = accountant;
    }

    public HashMap<Currency, Integer> getBalance() {
        return new HashMap<>(balance);
    }

    public void setBalance(Currency currency, int amount) {
        if (currency == null) throw new IllegalArgumentException("Wrong currency");
        if (amount<0) throw new IllegalArgumentException("Amount must be positive");
        HashMap<Currency, Integer> oldBalance = new HashMap<>(this.balance);
        this.commands.push(() -> {this.balance = oldBalance;});
        balance.put(currency, amount);
    };

    public Account undo() throws NothingToUndo {
        if (this.commands.isEmpty()) throw new NothingToUndo();
        this.commands.pop().perform();
        return this;
    }

    public boolean isUndoable() {
        return !this.commands.isEmpty();
    }

    public void print() {
        System.out.println("Accountant: "+this.accountant);
        System.out.println("Balance: ");
        this.balance.forEach((K,V) -> {System.out.println(K+"=>"+V);});
    }
}

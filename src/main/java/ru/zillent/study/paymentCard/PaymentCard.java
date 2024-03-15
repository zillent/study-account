package ru.zillent.study.paymentCard;

import ru.zillent.study.account.Account;
import ru.zillent.study.account.Currency;

import java.util.HashMap;

public class PaymentCard {
    Account account = new Account("CARDHOLDER");
    private String cardNumber = "XXXX-XXXX-XXXX-XXXX";

    public PaymentCard(String cardNumber, Account account) {
        this.account = account;
    }

    public PaymentCard() {
    }

    public void makeTransaction(Currency currency, int amount) throws NotEnoughBalanceException {
        HashMap<Currency, Integer> balance = this.account.getBalance();
        if (balance.isEmpty()) throw new NotEnoughBalanceException();
        int balanceAmount = balance.getOrDefault(currency, 0);
        if (balanceAmount-amount < 0) throw new NotEnoughBalanceException();
        this.account.setBalance(currency, balanceAmount - amount);
    };

    public Integer getBalance(Currency currency) {
        HashMap<Currency, Integer> balance = this.account.getBalance();
        if (balance.isEmpty()) return 0;
        return balance.getOrDefault(currency, 0);
    }
}

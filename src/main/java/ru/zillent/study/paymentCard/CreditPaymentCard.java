package ru.zillent.study.paymentCard;

import ru.zillent.study.account.Account;
import ru.zillent.study.account.Currency;

import java.lang.reflect.Proxy;
import java.util.HashMap;

public class CreditPaymentCard extends PaymentCard implements Creditable {
    private HashMap<Currency, Integer> creditLimits = new HashMap<>();;
    private HashMap<Currency, Integer> debts = new HashMap<>();;

    public CreditPaymentCard() {
    }

    public CreditPaymentCard(String cardNumber, Account account) {
        super(cardNumber, account);
    }

    @Mutator
    @Override
    public void setLimit(Currency currency, int limit) {
        if (currency == null) throw new IllegalArgumentException("Wrong currency");
        if (limit<0) throw new IllegalArgumentException("Limit must be positive");
        this.creditLimits.put(currency, limit);
    }

    @Cache
    @Override
    public Integer getLimit(Currency currency) {
        if (this.creditLimits.isEmpty()) return 0;
        return this.creditLimits.getOrDefault(currency, 0);
    }

    @Override
    public Integer getBalance(Currency currency) {
        return super.getBalance(currency) + this.getLimit(currency);
    };

    public HashMap<Currency, Integer> getDebts() {
      return this.debts;
    };

    @Override
    public void makeTransaction(Currency currency, int amount) throws NotEnoughBalanceException {
        if (amount > this.getBalance(currency)) throw new NotEnoughBalanceException();
        int balanceAmount = this.account.getBalance().getOrDefault(currency, 0);
        if (amount > balanceAmount) {
            int newBalanceDebt = amount - balanceAmount;
            this.debts.put(currency, this.debts.getOrDefault(currency,0) + newBalanceDebt);
            this.setLimit(currency, this.getLimit(currency) - newBalanceDebt);
            this.account.setBalance(currency, balanceAmount+newBalanceDebt);
        }
        super.makeTransaction(currency, amount);
    }

    public Creditable getProxy() {
        Class cls = this.getClass();
        return (Creditable) Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class[]{Creditable.class},
                new CreditableInvocatonHandler(this)
                );
    }
}

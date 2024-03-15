package ru.zillent.study.paymentCard;

import ru.zillent.study.account.Account;

import static ru.zillent.study.account.Currency.RUB;

public class PaymentCardApp {
    public static void main(String[] args) {
        Account account = new Account("Owner");
        Object proxy = new CreditPaymentCard("X", account).getProxy();
        System.out.println(((Creditable) proxy).getLimit(RUB));
        System.out.println(((Creditable) proxy).getLimit(RUB));
        System.out.println(((Creditable) proxy).getLimit(RUB));
        ((Creditable) proxy).setLimit(RUB,35);
        System.out.println(((Creditable) proxy).getLimit(RUB));
    }
}

package ru.zillent.study.account;

import static ru.zillent.study.account.Currency.RUB;
import static ru.zillent.study.account.Currency.USD;

public class AccountApp {
    public static void main(String[] args) {
        System.out.println("Test");
        Account account = new Account("Test");
        account.setBalance(RUB,33);
        account.setBalance(USD,33);
        System.out.println(account.getBalance());
        account.print();
    }

}

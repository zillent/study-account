package ru.zillent.study.account;

import static ru.zillent.study.account.Currency.RUB;
import static ru.zillent.study.account.Currency.USD;

public class AccountApp {
    public static void main(String[] args) throws NothingToUndo {
        Account account = new Account("Test");
        account.setBalance(RUB,31);
        account.setBalance(USD,33);
        account.print();
        Loadable save = account.save();
        System.out.println(account.isUndoable());
        account = account.undo();
        account.print();
        save.load();
        account.print();
//        System.out.println(RUB==RUB);
    }

}

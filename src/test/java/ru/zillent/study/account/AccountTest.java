package ru.zillent.study.account;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

import static ru.zillent.study.account.Currency.*;

class AccountTest {
    String testAccountant = "Test accountant";


    @Test
    void getAccountant() {
        Account account = new Account(testAccountant);
        assertEquals(testAccountant, account.getAccountant());
    }

    @Test
    void setAccountant() {
        Account account = new Account(testAccountant);
        account.setAccountant(testAccountant+"1");
        assertEquals(testAccountant+"1", account.getAccountant());
    }

    @Test
    void setBalance() {
        Account account = new Account(testAccountant);
        assertTrue(account.getBalance().isEmpty());
        account.setBalance(RUB, 22);
        HashMap<Currency, Integer> balance = account.getBalance();
        assertFalse(balance.isEmpty());
        assertEquals(22, balance.get(RUB));
        assertNull(balance.get(USD));
        account.setBalance(RUB, 33);
        account.setBalance(USD, 11);
        balance = account.getBalance();
        assertEquals(33, balance.get(RUB));
        assertEquals(11, balance.get(USD));
    }


    @Test
    void WrongCurrencyConstraint() {
        Account account = new Account(testAccountant);
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {account.setBalance(null, 1);}
        );
        assertEquals("Wrong currency", thrown.getMessage());
    }

    @Test
    void AmountIsPositiveContraint() {
        Account account = new Account(testAccountant);
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {account.setBalance(RUB, -2);}
        );
        assertEquals("Amount must be positive", thrown.getMessage());
    }

    @Test
    void undo() throws NothingToUndo {
        Account account = new Account(testAccountant);
        assertThrows(NothingToUndo.class, () -> {account.undo();});
        account.setAccountant("NEW");
        assertEquals("NEW", account.getAccountant());
        assertEquals(testAccountant, account.undo().getAccountant());
        account.setBalance(EUR, 35);
        assertEquals(35, account.getBalance().get(EUR));
        assertTrue(account.undo().getBalance().isEmpty());
        account.setBalance(EUR, 34);
        account.setBalance(EUR, 33);
        account.setBalance(USD, 30);
        assertEquals(33, account.getBalance().get(EUR));
        assertEquals(30, account.getBalance().get(USD));
        Account undoAccount = account.undo();
        assertEquals(33, undoAccount.getBalance().get(EUR));
        assertNull(undoAccount.getBalance().get(USD));
        assertEquals(34, undoAccount.undo().getBalance().get(EUR));
    }

    @Test
    void isUndoable() throws NothingToUndo {
        Account account = new Account(testAccountant);
        assertFalse(account.isUndoable());
        account.setBalance(RUB, 27);
        assertTrue(account.isUndoable());
        assertFalse(account.undo().isUndoable());
    }

    @Test
    void saveAndLoad() {
        Account account = new Account(testAccountant);
        account.setBalance(RUB, 1);
        account.setBalance(EUR, 2);
        Loadable save = account.save();
        account.setAccountant("TEST1");
        account.setBalance(RUB, 10);
        Loadable save1 = account.save();
        account.setBalance(USD, 35);
        assertEquals("TEST1", account.getAccountant());
        assertEquals(10, account.getBalance().get(RUB));
        assertEquals(35, account.getBalance().get(USD));
        save.load();
        assertEquals(testAccountant, account.getAccountant());
        assertEquals(1, account.getBalance().get(RUB));
        assertNull(account.getBalance().get(USD));
        assertEquals(2, account.getBalance().get(EUR));
        save1.load();
        assertEquals("TEST1", account.getAccountant());
        assertEquals(10, account.getBalance().get(RUB));
        assertNull(account.getBalance().get(USD));
        assertEquals(2, account.getBalance().get(EUR));
    }
}
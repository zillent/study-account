package ru.zillent.study.account;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class AccountTest {
    String testAccountant = "Test accountant";

    Account account = new Account(testAccountant);

    @Test
    void getAccountant() {
        assertEquals(testAccountant, account.getAccountant());
    }

    @Test
    void setAccountant() {
        account.setAccountant(testAccountant+"1");
        assertEquals(testAccountant+"1", account.getAccountant());
    }

    @Test
    void getBalance() {
    }

    @Test
    void setBalance() {
    }
}
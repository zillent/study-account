package ru.zillent.study.paymentCard;

import org.junit.jupiter.api.Test;
import ru.zillent.study.account.Account;
import ru.zillent.study.paymentCard.CreditPaymentCard;
import ru.zillent.study.paymentCard.NotEnoughBalanceException;
import ru.zillent.study.paymentCard.PaymentCard;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static ru.zillent.study.account.Currency.*;

public class PaymentCardTest {
    String cardNumber = "XXXX XXXX XXXX XXXX";
    @Test
    public void makeTransactionTest() {
        Account account = new Account("AccountOwner");
        PaymentCard paymentCard  = new PaymentCard(cardNumber, account);
        assertThrows(
                NotEnoughBalanceException.class,
                () -> {paymentCard.makeTransaction(RUB, 35);}
        );
        assertThrows(
                NotEnoughBalanceException.class,
                () -> {paymentCard.makeTransaction(USD, 35);}
        );
        assertEquals(0, paymentCard.getBalance(RUB));
        assertEquals(0, paymentCard.getBalance(USD));
        account.setBalance(RUB, 100);
        assertThrows(
                NotEnoughBalanceException.class,
                () -> {paymentCard.makeTransaction(USD, 35);}
        );
        assertEquals(100, paymentCard.getBalance(RUB));
        assertEquals(0, paymentCard.getBalance(USD));
        assertDoesNotThrow(
                () -> {paymentCard.makeTransaction(RUB, 35);}
        );
        assertEquals(65, paymentCard.getBalance(RUB));
        assertEquals(0, paymentCard.getBalance(USD));
    }

    @Test
    public void makeZeroCreditTransactionTest() {
        Account account = new Account("AccountOwner");
        CreditPaymentCard paymentCard = new CreditPaymentCard(cardNumber, account);
        assertThrows(
                NotEnoughBalanceException.class,
                () -> {paymentCard.makeTransaction(RUB, 35);}
        );
        assertThrows(
                NotEnoughBalanceException.class,
                () -> {paymentCard.makeTransaction(USD, 35);}
        );
        assertEquals(0, paymentCard.getBalance(RUB));
        assertEquals(0, paymentCard.getBalance(USD));
        account.setBalance(RUB, 100);
        assertThrows(
                NotEnoughBalanceException.class,
                () -> {paymentCard.makeTransaction(USD, 35);}
        );
        assertEquals(100, paymentCard.getBalance(RUB));
        assertEquals(0, paymentCard.getBalance(USD));
        assertDoesNotThrow(
                () -> {paymentCard.makeTransaction(RUB, 35);}
        );
        assertEquals(65, paymentCard.getBalance(RUB));
        assertEquals(0, paymentCard.getBalance(USD));
    }

    @Test
    public void makeCreditTransactionTest() {
        Account account = new Account("AccountOwner");
        CreditPaymentCard paymentCard = new CreditPaymentCard(cardNumber, account);
        paymentCard.setLimit(RUB, 1000);
        assertEquals(1000, paymentCard.getBalance(RUB));
        assertDoesNotThrow(
                () -> {paymentCard.makeTransaction(RUB, 35);}
        );
        assertEquals(1000-35, paymentCard.getLimit(RUB));
        assertEquals(1000-35, paymentCard.getBalance(RUB));
        assertEquals(35, paymentCard.getDebts().get(RUB));
        assertThrows(
                NotEnoughBalanceException.class,
                () -> {paymentCard.makeTransaction(USD, 35);}
        );
        assertThrows(
                NotEnoughBalanceException.class,
                () -> {paymentCard.makeTransaction(RUB, 1000);}
        );
        assertEquals(1000-35, paymentCard.getBalance(RUB));
        assertEquals(1000-35, paymentCard.getLimit(RUB));
        assertEquals(35, paymentCard.getDebts().get(RUB));
        assertEquals(0, paymentCard.getBalance(USD));
        account.setBalance(RUB, 100);
        assertThrows(
                NotEnoughBalanceException.class,
                () -> {paymentCard.makeTransaction(USD, 35);}
        );
        assertEquals(1100-35, paymentCard.getBalance(RUB));
        assertEquals(0, paymentCard.getBalance(USD));
        assertDoesNotThrow(
                () -> {paymentCard.makeTransaction(RUB, 35);}
        );
        assertEquals(1100-70, paymentCard.getBalance(RUB));
        assertEquals(0, paymentCard.getBalance(USD));
        assertEquals(1000-35, paymentCard.getLimit(RUB));
        assertEquals(35, paymentCard.getDebts().get(RUB));
        assertDoesNotThrow(
                () -> {paymentCard.makeTransaction(RUB, 135);}
        );
        assertEquals(1100-70-135, paymentCard.getBalance(RUB));
        assertEquals(0, paymentCard.getBalance(USD));
        assertEquals(1000-35+100-135-35, paymentCard.getLimit(RUB));
        assertEquals(35-100+135+35, paymentCard.getDebts().get(RUB));
    }

    @Test
    void WrongCurrencyConstraint() {
        Account account = new Account("AccountOwner");
        CreditPaymentCard paymentCard = new CreditPaymentCard(cardNumber, account);
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {paymentCard.setLimit(null, 1);}
        );
        assertEquals("Wrong currency", thrown.getMessage());
    }

    @Test
    void LimitIsPositiveContraint() {
        Account account = new Account("AccountOwner");
        CreditPaymentCard paymentCard = new CreditPaymentCard(cardNumber, account);
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {paymentCard.setLimit(RUB, -2);}
        );
        assertEquals("Limit must be positive", thrown.getMessage());
    }

}

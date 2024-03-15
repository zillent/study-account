package ru.zillent.study.paymentCard;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static ru.zillent.study.account.Currency.RUB;

public class ProxyTest {

    @Test
    public void proxyingGetLimitTest() {
        CreditPaymentCard creditPaymentCardSpy = Mockito.spy(CreditPaymentCard.class);
        Creditable proxy = creditPaymentCardSpy.getProxy();
        //get limit without cache (total: 1 execution of getLimit)
        assertEquals(0, proxy.getLimit(RUB));
        //get limit from cache (total: 1 execution of getLimit)
        assertEquals(0, proxy.getLimit(RUB));
        //execute mutator method - erase cache
        proxy.setLimit(RUB, 23);
        //get limit from empty cache (total: 2 execution of getLimit)
        assertEquals(23, proxy.getLimit(RUB));
        //check total: 2 executions
        Mockito.verify(creditPaymentCardSpy, Mockito.times(2)).getLimit(Mockito.any());
    }

}

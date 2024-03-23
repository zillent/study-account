package ru.zillent.study.paymentCard;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static ru.zillent.study.account.Currency.RUB;
import static ru.zillent.study.account.Currency.USD;

public class CacheTest {

    @Test
    public void cachedGetLimitTest() {
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

    @Test
    public void cachedGetLimitWithParsTest() {
        CreditPaymentCard creditPaymentCardSpy = Mockito.spy(CreditPaymentCard.class);
        Creditable proxy = creditPaymentCardSpy.getProxy();
        //get limit without cache (0+1=1 executions of getLimit)
        assertEquals(0, proxy.getLimit(RUB));
        //get limit from cache (1+0=1 executions of getLimit)
        assertEquals(0, proxy.getLimit(RUB));
        //different pars! (1+1=2 executions of getLimit)
        assertEquals(0, proxy.getLimit(USD));
        //check prev cache is actual (2+0=2 executions of getLimit)
        assertEquals(0, proxy.getLimit(RUB));
        //check prev cache is actual (2+0=2 executions of getLimit)
        assertEquals(0, proxy.getLimit(USD));
        //execute mutator method - erase cache of RUB
        proxy.setLimit(RUB, 23);
        //get limit from empty cache (2+1=3 execution of getLimit)
        assertEquals(23, proxy.getLimit(RUB));
        //different pars. from cache (3+0=3 execution of getLimit)
        assertEquals(0, proxy.getLimit(USD));
        //check total: 3 executions
        Mockito.verify(creditPaymentCardSpy, Mockito.times(3)).getLimit(Mockito.any());
    }
    @Test
    public void cachedGetLimitTimeToLiveTest() throws InterruptedException {
        CreditPaymentCard creditPaymentCardSpy = Mockito.spy(CreditPaymentCard.class);
        Creditable proxy = creditPaymentCardSpy.getProxy();
        //get limit without cache (0+1=1 executions of getLimit)
        assertEquals(0, proxy.getLimit(RUB));
        //get limit from cache (1+0=1 executions of getLimit)
        assertEquals(0, proxy.getLimit(RUB));
        Thread.sleep(1050);
        //get limit after waiting time to live cache (1+1=2 executions of getLimit)
        assertEquals(0, proxy.getLimit(RUB));
        Thread.sleep(600);
        //get limit from cache (2+0=2 executions of getLimit)
        assertEquals(0, proxy.getLimit(RUB));
        //get limit from cache. ttl must be cleared in previous execution (2+0=2 executions of getLimit)
        Thread.sleep(600);
        assertEquals(0, proxy.getLimit(RUB));
        //check total: 2 executions
        Mockito.verify(creditPaymentCardSpy, Mockito.times(2)).getLimit(Mockito.any());
    }
}

package ru.zillent.study.paymentCard;

import ru.zillent.study.account.Currency;

public interface Creditable {

    public void setLimit(Currency currency, int limit);

    public int getLimit(Currency currency);
}

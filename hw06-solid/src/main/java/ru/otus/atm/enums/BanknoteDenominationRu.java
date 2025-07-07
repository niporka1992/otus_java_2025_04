package ru.otus.atm.enums;

import ru.otus.atm.interfaces.BanknoteDenomination;

public enum BanknoteDenominationRu implements BanknoteDenomination {
    RUB_5000(5000),
    RUB_2000(2000),
    RUB_1000(1000),
    RUB_500(500),
    RUB_200(200),
    RUB_100(100),
    RUB_50(50),
    RUB_10(10);

    private final int value;

    BanknoteDenominationRu(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}

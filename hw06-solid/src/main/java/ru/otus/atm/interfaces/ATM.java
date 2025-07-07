package ru.otus.atm.interfaces;

import java.util.Map;
import ru.otus.atm.enums.BanknoteDenominationRu;
import ru.otus.atm.exeptions.CannotDispenseExactAmountException;
import ru.otus.atm.exeptions.InvalidAmountException;
import ru.otus.atm.exeptions.NotEnoughMoneyException;

public interface ATM {
    void deposit(BanknoteDenominationRu denomination, int count) throws InvalidAmountException;

    Map<BanknoteDenominationRu, Integer> withdraw(int amount)
            throws NotEnoughMoneyException, InvalidAmountException, CannotDispenseExactAmountException;

    int getBalance();
}

package ru.otus.atm.interfaces;

import ru.otus.atm.exeptions.InvalidAmountException;
import ru.otus.atm.exeptions.NotEnoughMoneyException;

public interface BanknoteCell {

    int getCount();

    void addBanknotes(int count) throws InvalidAmountException;

    void removeBanknotes(int count) throws InvalidAmountException, NotEnoughMoneyException;

    int getAmount();
}

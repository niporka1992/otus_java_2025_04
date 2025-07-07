package ru.otus.atm.interfaces;

import java.util.List;
import java.util.Map;
import ru.otus.atm.enums.BanknoteDenominationRu;

public interface BanknoteStorage {
    void deposit(BanknoteDenominationRu denomination, int count);

    void withdraw(Map<BanknoteDenominationRu, Integer> plan);

    int getBalance();

    int getCount(BanknoteDenominationRu denomination);

    List<BanknoteDenominationRu> getAvailableDenominationsDesc();

    boolean hasDenomination(BanknoteDenominationRu denomination);
}

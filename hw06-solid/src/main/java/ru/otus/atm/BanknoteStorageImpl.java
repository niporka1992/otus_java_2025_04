package ru.otus.atm;

import java.util.*;
import ru.otus.atm.enums.BanknoteDenominationRu;
import ru.otus.atm.exeptions.InvalidAmountException;
import ru.otus.atm.interfaces.BanknoteCell;
import ru.otus.atm.interfaces.BanknoteStorage;

public class BanknoteStorageImpl implements BanknoteStorage {

    private final Map<BanknoteDenominationRu, BanknoteCell> cells;

    public BanknoteStorageImpl() {
        this.cells = new EnumMap<>(BanknoteDenominationRu.class);
        for (BanknoteDenominationRu denom : BanknoteDenominationRu.values()) {
            cells.put(denom, new BanknoteCellImpl(denom));
        }
    }

    @Override
    public void deposit(BanknoteDenominationRu denomination, int count) {
        if (count <= 0) {
            throw new InvalidAmountException("Invalid count: " + count);
        }
        BanknoteCell cell = cells.get(denomination);
        if (cell == null) {
            throw new InvalidAmountException("Unknown denomination: " + denomination);
        }
        cell.addBanknotes(count);
    }

    @Override
    public void withdraw(Map<BanknoteDenominationRu, Integer> plan) {
        for (var entry : plan.entrySet()) {
            cells.get(entry.getKey()).removeBanknotes(entry.getValue());
        }
    }

    @Override
    public int getBalance() {
        return cells.values().stream().mapToInt(BanknoteCell::getAmount).sum();
    }

    @Override
    public int getCount(BanknoteDenominationRu denomination) {
        return cells.get(denomination).getCount();
    }

    @Override
    public List<BanknoteDenominationRu> getAvailableDenominationsDesc() {
        return cells.keySet().stream()
                .sorted(Comparator.comparingInt(BanknoteDenominationRu::getValue)
                        .reversed())
                .toList();
    }

    @Override
    public boolean hasDenomination(BanknoteDenominationRu denomination) {
        return cells.containsKey(denomination);
    }
}

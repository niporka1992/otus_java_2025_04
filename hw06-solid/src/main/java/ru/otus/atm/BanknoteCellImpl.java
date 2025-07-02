package ru.otus.atm;

import ru.otus.atm.enums.BanknoteDenominationRu;
import ru.otus.atm.exeptions.InvalidAmountException;
import ru.otus.atm.exeptions.NotEnoughMoneyException;
import ru.otus.atm.interfaces.BanknoteCell;

public class BanknoteCellImpl implements BanknoteCell {
    private final BanknoteDenominationRu denomination;
    private int count;

    public BanknoteCellImpl(BanknoteDenominationRu denomination) {
        this.denomination = denomination;
        this.count = 0;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void addBanknotes(int count) {
        validatePositiveCount(count);
        this.count += count;
    }

    @Override
    public void removeBanknotes(int count) {
        validatePositiveCount(count);
        validateSufficientBanknotes(count);
        this.count -= count;
    }

    @Override
    public int getAmount() {
        return denomination.getValue() * count;
    }

    private void validatePositiveCount(int count) {
        if (count <= 0) {
            throw new InvalidAmountException("Count banknotes must be positive");
        }
    }

    private void validateSufficientBanknotes(int count) {
        if (this.count < count) {
            throw new NotEnoughMoneyException("Not enough banknotes of " + denomination);
        }
    }
}

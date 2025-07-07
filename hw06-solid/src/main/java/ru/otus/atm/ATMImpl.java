package ru.otus.atm;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import ru.otus.atm.enums.BanknoteDenominationRu;
import ru.otus.atm.exeptions.CannotDispenseExactAmountException;
import ru.otus.atm.exeptions.InvalidAmountException;
import ru.otus.atm.interfaces.ATM;
import ru.otus.atm.interfaces.BanknoteStorage;

public class ATMImpl implements ATM {

    private final BanknoteStorage storage;

    public ATMImpl(BanknoteStorage storage) {
        this.storage = storage;
    }

    @Override
    public void deposit(BanknoteDenominationRu denomination, int count) {
        storage.deposit(denomination, count);
    }

    @Override
    public Map<BanknoteDenominationRu, Integer> withdraw(int amount) {
        validateWithdrawAmount(amount);

        Map<BanknoteDenominationRu, Integer> plan = buildWithdrawPlan(amount);

        storage.withdraw(plan);

        return plan;
    }

    @Override
    public int getBalance() {
        return storage.getBalance();
    }

    // --- Приватные методы ---

    private void validateWithdrawAmount(int amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Invalid amount: " + amount);
        }
        if (amount > storage.getBalance()) {
            throw new CannotDispenseExactAmountException("Недостаточно средств в хранилище");
        }
    }

    private Map<BanknoteDenominationRu, Integer> buildWithdrawPlan(int amount) {
        Map<BanknoteDenominationRu, Integer> plan = new EnumMap<>(BanknoteDenominationRu.class);
        int remaining = amount;

        List<BanknoteDenominationRu> denominations = storage.getAvailableDenominationsDesc();

        for (BanknoteDenominationRu denom : denominations) {
            int countInCell = storage.getCount(denom);
            int countToUse = Math.min(remaining / denom.getValue(), countInCell);
            if (countToUse > 0) {
                plan.put(denom, countToUse);
                remaining -= countToUse * denom.getValue();
            }
        }

        if (remaining > 0) {
            throw new CannotDispenseExactAmountException("Невозможно выдать сумму точно");
        }

        return plan;
    }
}

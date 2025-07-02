package ru.otus.atm;

import java.util.*;
import ru.otus.atm.enums.BanknoteDenominationRu;
import ru.otus.atm.exeptions.CannotDispenseExactAmountException;
import ru.otus.atm.exeptions.InvalidAmountException;
import ru.otus.atm.exeptions.NotEnoughMoneyException;
import ru.otus.atm.interfaces.ATM;
import ru.otus.atm.interfaces.BanknoteCell;

public class ATMImpl implements ATM {

    private final Map<BanknoteDenominationRu, BanknoteCell> cells;
    private final List<BanknoteDenominationRu> denominationsDescending;

    public ATMImpl() {
        this.denominationsDescending = initDenominationsDescending();
        this.cells = initCells(denominationsDescending);
    }

    private static List<BanknoteDenominationRu> initDenominationsDescending() {
        return Arrays.stream(BanknoteDenominationRu.values())
                .sorted(Comparator.comparingInt(BanknoteDenominationRu::getValue)
                        .reversed())
                .toList();
    }

    private static Map<BanknoteDenominationRu, BanknoteCell> initCells(List<BanknoteDenominationRu> denominations) {
        Map<BanknoteDenominationRu, BanknoteCell> cells = new EnumMap<>(BanknoteDenominationRu.class);

        for (BanknoteDenominationRu denomination : denominations) {
            BanknoteCell cell = new BanknoteCellImpl(denomination);
            cells.put(denomination, cell);
        }
        return cells;
    }

    @Override
    public void deposit(BanknoteDenominationRu denomination, int count) {
        validatePositiveCount(count);
        BanknoteCell cell = getCellOrThrow(denomination);
        cell.addBanknotes(count);
    }

    @Override
    public void withdraw(int amount) throws NotEnoughMoneyException, InvalidAmountException {
        validatePositiveAmount(amount);
        int balance = getBalance();
        validateSufficientBalance(amount, balance);

        Map<BanknoteDenominationRu, Integer> withdrawalPlan = calculateWithdrawalPlan(amount);

        for (Map.Entry<BanknoteDenominationRu, Integer> entry : withdrawalPlan.entrySet()) {
            cells.get(entry.getKey()).removeBanknotes(entry.getValue());
        }
    }

    @Override
    public int getBalance() {
        return cells.values().stream().mapToInt(BanknoteCell::getAmount).sum();
    }

    private Map<BanknoteDenominationRu, Integer> calculateWithdrawalPlan(int amount)
            throws CannotDispenseExactAmountException {

        int remaining = amount;
        Map<BanknoteDenominationRu, Integer> plan = new EnumMap<>(BanknoteDenominationRu.class);

        for (BanknoteDenominationRu denomination : denominationsDescending) {
            BanknoteCell cell = cells.get(denomination);
            int count = calculateBanknotesToDispense(cell, denomination, remaining);

            if (count > 0) {
                plan.put(denomination, count);
                remaining -= count * denomination.getValue();
            }

            if (remaining == 0) {
                break;
            }
        }

        validateWithdrawalPossible(remaining);

        return plan;
    }

    private int calculateBanknotesToDispense(BanknoteCell cell, BanknoteDenominationRu denomination, int remaining) {
        int needed = remaining / denomination.getValue();
        int available = cell.getCount();
        return Math.min(needed, available);
    }

    private void validateWithdrawalPossible(int remaining) {
        if (remaining != 0) {
            throw new CannotDispenseExactAmountException("Cannot withdraw exact amount with available banknotes");
        }
    }

    private void validatePositiveCount(int count) {
        if (count <= 0) {
            throw new InvalidAmountException("Count must be positive");
        }
    }

    private void validatePositiveAmount(int amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
    }

    private void validateSufficientBalance(int amount, int balance) {
        if (amount > balance) {
            throw new NotEnoughMoneyException("Not enough money in ATM");
        }
    }

    private BanknoteCell getCellOrThrow(BanknoteDenominationRu denomination) {
        BanknoteCell cell = cells.get(denomination);
        if (cell == null) {
            throw new InvalidAmountException("Unknown denomination");
        }
        return cell;
    }
}

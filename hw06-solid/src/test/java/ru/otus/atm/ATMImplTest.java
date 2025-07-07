package ru.otus.atm;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.enums.BanknoteDenominationRu;
import ru.otus.atm.exeptions.CannotDispenseExactAmountException;
import ru.otus.atm.exeptions.InvalidAmountException;
import ru.otus.atm.interfaces.BanknoteStorage;

@DisplayName("Тесты для ATMImpl с BanknoteStorage")
class ATMImplTest {

    private ATMImpl atm;
    private BanknoteStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryBanknoteStorage();
        atm = new ATMImpl(storage);
    }

    @Test
    @DisplayName("Депозит банкнот и подсчет баланса работают корректно")
    void deposit_and_getBalance_workCorrectly() {
        atm.deposit(BanknoteDenominationRu.RUB_1000, 3);
        atm.deposit(BanknoteDenominationRu.RUB_500, 2);
        assertEquals(4000, atm.getBalance());
    }

    @Test
    @DisplayName("Попытка внести 0 или отрицательное количество банкнот вызывает исключение")
    void deposit_invalidCount_throws() {
        assertThrows(InvalidAmountException.class, () -> atm.deposit(BanknoteDenominationRu.RUB_100, 0));
        assertThrows(InvalidAmountException.class, () -> atm.deposit(BanknoteDenominationRu.RUB_100, -5));
    }

    @Test
    @DisplayName("Успешное снятие точной суммы с разными номиналами")
    void withdraw_exactAmount_success() {
        atm.deposit(BanknoteDenominationRu.RUB_1000, 3);
        atm.deposit(BanknoteDenominationRu.RUB_500, 1);
        atm.withdraw(2500);
        assertEquals(1000, atm.getBalance());
    }

    @Test
    @DisplayName("Попытка снять сумму больше остатка вызывает исключение")
    void withdraw_notEnoughMoney_throws() {
        atm.deposit(BanknoteDenominationRu.RUB_500, 2); // 1000
        assertThrows(CannotDispenseExactAmountException.class, () -> atm.withdraw(1500));
    }

    @Test
    @DisplayName("Если нельзя выдать точную сумму, выбрасывается исключение")
    void withdraw_cannotDispenseExactAmount_throws() {
        atm.deposit(BanknoteDenominationRu.RUB_1000, 1);
        atm.deposit(BanknoteDenominationRu.RUB_500, 1);
        assertThrows(CannotDispenseExactAmountException.class, () -> atm.withdraw(1200));
    }

    @Test
    @DisplayName("Попытка снять 0 или отрицательную сумму вызывает исключение")
    void withdraw_invalidAmount_throws() {
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(0));
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(-100));
    }

    // Простая реализация BanknoteStorage для тестов
    static class InMemoryBanknoteStorage implements BanknoteStorage {

        private final Map<BanknoteDenominationRu, Integer> banknotes = new EnumMap<>(BanknoteDenominationRu.class);

        public InMemoryBanknoteStorage() {
            for (BanknoteDenominationRu denom : BanknoteDenominationRu.values()) {
                banknotes.put(denom, 0);
            }
        }

        @Override
        public void deposit(BanknoteDenominationRu denomination, int count) {
            if (count <= 0) throw new InvalidAmountException("Invalid count: " + count);
            banknotes.put(denomination, banknotes.get(denomination) + count);
        }

        @Override
        public void withdraw(Map<BanknoteDenominationRu, Integer> plan) {
            for (var entry : plan.entrySet()) {
                int available = banknotes.get(entry.getKey());
                if (available < entry.getValue()) {
                    throw new CannotDispenseExactAmountException("Недостаточно банкнот номинала " + entry.getKey());
                }
            }
            for (var entry : plan.entrySet()) {
                banknotes.put(entry.getKey(), banknotes.get(entry.getKey()) - entry.getValue());
            }
        }

        @Override
        public int getBalance() {
            return banknotes.entrySet().stream()
                    .mapToInt(e -> e.getKey().getValue() * e.getValue())
                    .sum();
        }

        @Override
        public List<BanknoteDenominationRu> getAvailableDenominationsDesc() {
            List<BanknoteDenominationRu> list = new ArrayList<>(banknotes.keySet());
            list.sort(Comparator.comparingInt(BanknoteDenominationRu::getValue).reversed());
            return list;
        }

        @Override
        public int getCount(BanknoteDenominationRu denomination) {
            return banknotes.getOrDefault(denomination, 0);
        }

        @Override
        public boolean hasDenomination(BanknoteDenominationRu denomination) {
            return banknotes.getOrDefault(denomination, 0) > 0;
        }
    }
}

package ru.otus.atm;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.enums.BanknoteDenominationRu;
import ru.otus.atm.exeptions.InvalidAmountException;
import ru.otus.atm.exeptions.NotEnoughMoneyException;

@SuppressWarnings("java:S5778")
@DisplayName("Тесты для BanknoteStorageImpl")
class BanknoteStorageImplTest {

    private BanknoteStorageImpl storage;

    @BeforeEach
    void setUp() {
        storage = new BanknoteStorageImpl();
    }

    @Test
    @DisplayName("Начальный баланс равен 0")
    void initial_balance_zero() {
        assertEquals(0, storage.getBalance());
    }

    @Test
    @DisplayName("Депозит увеличивает баланс и количество банкнот")
    void deposit_increases_balance_and_count() {
        storage.deposit(BanknoteDenominationRu.RUB_1000, 5);
        assertEquals(5000, storage.getBalance());
        assertEquals(5, storage.getCount(BanknoteDenominationRu.RUB_1000));
    }

    @Test
    @DisplayName("Депозит с нулем или отрицательным числом бросает исключение")
    void deposit_invalid_count_throws() {
        assertThrows(InvalidAmountException.class, () -> storage.deposit(BanknoteDenominationRu.RUB_500, 0));
        assertThrows(InvalidAmountException.class, () -> storage.deposit(BanknoteDenominationRu.RUB_500, -3));
    }

    @Test
    @DisplayName("Метод hasDenomination корректно проверяет наличие номинала")
    void hasDenomination_returns_true_for_known_and_false_for_unknown() {
        // Номинал должен быть в наборе, даже если количество 0 (изначально)
        assertTrue(storage.hasDenomination(BanknoteDenominationRu.RUB_100));
        // null всегда false
        assertFalse(storage.hasDenomination(null));
        // Номинал, которого нет в enum (например, пользователь может передать), можно проверить с помощью заглушки
        // Но так как enum фиксирован, это можно опустить
    }

    @Test
    @DisplayName("Снятие банкнот уменьшает количество и баланс")
    void withdraw_decreases_balance_and_count() {
        storage.deposit(BanknoteDenominationRu.RUB_500, 4);
        storage.deposit(BanknoteDenominationRu.RUB_100, 10);
        assertEquals(3000, storage.getBalance());

        storage.withdraw(Map.of(
                BanknoteDenominationRu.RUB_500, 2,
                BanknoteDenominationRu.RUB_100, 5));
        assertEquals(1500, storage.getBalance());
        assertEquals(2, storage.getCount(BanknoteDenominationRu.RUB_500));
        assertEquals(5, storage.getCount(BanknoteDenominationRu.RUB_100));
    }

    @Test
    @DisplayName("Снятие с превышением количества банкнот вызывает исключение")
    void withdraw_more_than_available_throws() {
        storage.deposit(BanknoteDenominationRu.RUB_1000, 1);

        // Убираем 2 банкноты, хотя есть только 1
        assertThrows(NotEnoughMoneyException.class, () -> storage.withdraw(Map.of(BanknoteDenominationRu.RUB_1000, 2)));
    }

    @Test
    @DisplayName("Метод getAvailableDenominationsDesc возвращает номиналы по убыванию")
    void getAvailableDenominationsDesc_returns_sorted_list() {
        // Добавим несколько номиналов, чтобы проверить сортировку
        storage.deposit(BanknoteDenominationRu.RUB_1000, 1);
        storage.deposit(BanknoteDenominationRu.RUB_500, 1);
        storage.deposit(BanknoteDenominationRu.RUB_100, 1);

        var denominations = storage.getAvailableDenominationsDesc();

        assertFalse(denominations.isEmpty(), "Список номиналов не должен быть пустым");

        for (int i = 1; i < denominations.size(); i++) {
            assertTrue(
                    denominations.get(i - 1).getValue() >= denominations.get(i).getValue(),
                    "Список номиналов должен быть отсортирован по убыванию");
        }
    }

    @Test
    @DisplayName("Снятие банкнот с точным количеством успешно проходит")
    void withdraw_exact_amount_success() {
        storage.deposit(BanknoteDenominationRu.RUB_2000, 2);
        storage.deposit(BanknoteDenominationRu.RUB_500, 3);

        Map<BanknoteDenominationRu, Integer> withdrawMap = Map.of(
                BanknoteDenominationRu.RUB_2000, 1,
                BanknoteDenominationRu.RUB_500, 2);

        assertDoesNotThrow(() -> storage.withdraw(withdrawMap));

        assertEquals(2000 + 500, storage.getBalance());
        assertEquals(1, storage.getCount(BanknoteDenominationRu.RUB_2000));
        assertEquals(1, storage.getCount(BanknoteDenominationRu.RUB_500));
    }
}

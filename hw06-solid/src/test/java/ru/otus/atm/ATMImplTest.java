package ru.otus.atm;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.enums.BanknoteDenominationRu;
import ru.otus.atm.exeptions.CannotDispenseExactAmountException;
import ru.otus.atm.exeptions.InvalidAmountException;
import ru.otus.atm.exeptions.NotEnoughMoneyException;

/**
 * Unit tests for {@link ATMImpl}.
 */
@DisplayName("Тесты для ATMImpl (банкомат)")
class ATMImplTest {

    private ATMImpl atm;

    @BeforeEach
    void setUp() {
        atm = new ATMImpl();
    }

    @Test
    @DisplayName("Депозит банкнот и подсчет баланса работают корректно")
    void deposit_and_getBalance_workCorrectly() {
        atm.deposit(BanknoteDenominationRu.RUB_1000, 3); // 3000
        atm.deposit(BanknoteDenominationRu.RUB_500, 2); // 1000
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
        assertThrows(NotEnoughMoneyException.class, () -> atm.withdraw(1500));
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
}

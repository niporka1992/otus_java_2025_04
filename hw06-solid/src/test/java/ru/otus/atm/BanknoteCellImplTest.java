package ru.otus.atm;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.enums.BanknoteDenominationRu;
import ru.otus.atm.exeptions.InvalidAmountException;
import ru.otus.atm.exeptions.NotEnoughMoneyException;

/**
 * Unit tests for {@link BanknoteCellImpl}.
 */
@DisplayName("Тесты для BanknoteCellImpl (ячейка с банкнотами)")
class BanknoteCellImplTest {

    @Test
    @DisplayName("Добавление банкнот увеличивает количество")
    void addBanknotes_increasesCount() {
        BanknoteCellImpl cell = new BanknoteCellImpl(BanknoteDenominationRu.RUB_100);
        cell.addBanknotes(5);
        assertEquals(5, cell.getCount());
    }

    @Test
    @DisplayName("Добавление 0 или отрицательного количества банкнот вызывает исключение")
    void addBanknotes_negativeOrZero_throws() {
        BanknoteCellImpl cell = new BanknoteCellImpl(BanknoteDenominationRu.RUB_100);
        assertThrows(InvalidAmountException.class, () -> cell.addBanknotes(0));
        assertThrows(InvalidAmountException.class, () -> cell.addBanknotes(-1));
    }

    @Test
    @DisplayName("Удаление банкнот уменьшает количество")
    void removeBanknotes_decreasesCount() {
        BanknoteCellImpl cell = new BanknoteCellImpl(BanknoteDenominationRu.RUB_100);
        cell.addBanknotes(10);
        cell.removeBanknotes(7);
        assertEquals(3, cell.getCount());
    }

    @Test
    @DisplayName("Удаление больше чем есть в ячейке вызывает исключение")
    void removeBanknotes_moreThanCount_throws() {
        BanknoteCellImpl cell = new BanknoteCellImpl(BanknoteDenominationRu.RUB_100);
        cell.addBanknotes(3);
        assertThrows(NotEnoughMoneyException.class, () -> cell.removeBanknotes(5));
    }

    @Test
    @DisplayName("Удаление 0 или отрицательного количества банкнот вызывает исключение")
    void removeBanknotes_negativeOrZero_throws() {
        BanknoteCellImpl cell = new BanknoteCellImpl(BanknoteDenominationRu.RUB_100);
        cell.addBanknotes(3);
        assertThrows(InvalidAmountException.class, () -> cell.removeBanknotes(0));
        assertThrows(InvalidAmountException.class, () -> cell.removeBanknotes(-1));
    }

    @Test
    @DisplayName("Подсчет суммы в ячейке корректен")
    void getAmount_calculatesCorrectly() {
        BanknoteCellImpl cell = new BanknoteCellImpl(BanknoteDenominationRu.RUB_500);
        cell.addBanknotes(4);
        assertEquals(2000, cell.getAmount());
    }
}

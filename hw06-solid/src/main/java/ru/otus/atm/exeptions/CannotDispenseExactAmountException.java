package ru.otus.atm.exeptions;

public class CannotDispenseExactAmountException extends RuntimeException {
    public CannotDispenseExactAmountException(String message) {
        super(message);
    }
}

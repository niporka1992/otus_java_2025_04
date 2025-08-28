package ru.otus.appcontainer.exeption;

public class ComponentException extends RuntimeException {
    public ComponentException(String message) {
        super(message);
    }

    public ComponentException(String message, Throwable cause) {
        super(message, cause);
    }
}

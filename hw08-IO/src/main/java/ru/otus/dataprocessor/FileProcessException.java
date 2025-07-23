package ru.otus.dataprocessor;

public class FileProcessException extends RuntimeException {

    public FileProcessException(String msg) {
        super(msg);
    }

    public FileProcessException(String message, Exception cause) {
        super(message, cause);
    }
}

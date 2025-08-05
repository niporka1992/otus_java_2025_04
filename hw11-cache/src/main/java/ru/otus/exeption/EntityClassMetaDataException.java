package ru.otus.exeption;

public class EntityClassMetaDataException extends RuntimeException {

    public EntityClassMetaDataException(String message) {
        super(message);
    }

    public EntityClassMetaDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

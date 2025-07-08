package ru.otus.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectForMessage {
    private List<String> data;

    public ObjectForMessage() {
        // пустой конструктор по умолчанию
    }

    // Конструктор копирования
    public ObjectForMessage(ObjectForMessage other) {
        if (other.data != null) {
            this.data = new ArrayList<>(other.data);
        }
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}

package ru.otus.listener.homework;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.otus.listener.Listener;
import ru.otus.model.Message;

public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, Message> history = new HashMap<>();

    @Override
    public void onUpdated(Message msg) {
        Message copy = Message.copyOf(msg);
        history.put(copy.getId(), copy);
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        Message stored = history.get(id);
        if (stored == null) {
            return Optional.empty();
        }
        return Optional.of(Message.copyOf(stored));
    }
}

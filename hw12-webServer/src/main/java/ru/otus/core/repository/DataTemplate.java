package ru.otus.core.repository;

import org.hibernate.Session;

public interface DataTemplate<T> {
    T insert(Session session, T object);

    T update(Session session, T object);
}

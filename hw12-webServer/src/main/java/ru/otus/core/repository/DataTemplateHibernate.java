package ru.otus.core.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;

@RequiredArgsConstructor
public class DataTemplateHibernate<T> implements DataTemplate<T> {

    private final Class<T> clazz;

    @Override
    public T insert(Session session, T object) {
        session.persist(object);
        return object;
    }

    @Override
    public T update(Session session, T object) {
        return session.merge(object);
    }
}

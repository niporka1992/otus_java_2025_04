package ru.otus.dao;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.model.User;

@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final TransactionManagerHibernate transactionManagerHibernate;

    @Override
    public Optional<User> findByLogin(String login) {
        return transactionManagerHibernate.doInTransaction(
                session -> session.createQuery("select u from User u where u.login = :login", User.class)
                        .setParameter("login", login)
                        .uniqueResultOptional());
    }
}

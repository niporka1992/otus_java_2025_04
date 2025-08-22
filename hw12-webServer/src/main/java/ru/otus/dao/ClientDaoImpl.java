package ru.otus.dao;

import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.model.Client;

@RequiredArgsConstructor
public class ClientDaoImpl implements ClientDao {

    private final TransactionManagerHibernate transactionManagerHibernate;
    private final DataTemplateHibernate<Client> dataTemplateHibernate;

    @Override
    public List<Client> findAll() {
        return transactionManagerHibernate.doInTransaction(session -> {
            var graph = session.getEntityGraph("Client.withAll");
            return session.createQuery("select c from Client c", Client.class)
                    .setHint("jakarta.persistence.fetchgraph", graph)
                    .getResultList();
        });
    }

    @Override
    public Client save(Client client) {
        return transactionManagerHibernate.doInTransaction(session -> {
            if (client.getId() == null) {
                dataTemplateHibernate.insert(session, client);
            } else {
                dataTemplateHibernate.update(session, client);
            }
            return client;
        });
    }
}

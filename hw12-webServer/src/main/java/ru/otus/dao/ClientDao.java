package ru.otus.dao;

import java.util.List;
import ru.otus.model.Client;

public interface ClientDao {
    Client save(Client client);

    List<Client> findAll();
}

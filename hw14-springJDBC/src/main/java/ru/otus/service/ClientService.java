package ru.otus.service;

import java.util.List;
import ru.otus.dto.ClientDto;

public interface ClientService {

    List<ClientDto> findAll();

    ClientDto save(ClientDto clientDto);
}

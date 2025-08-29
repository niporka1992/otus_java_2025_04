package ru.otus.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.converter.ClientConverter;
import ru.otus.dto.ClientDto;
import ru.otus.model.Client;
import ru.otus.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientConverter clientConverter;

    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> findAll() {
        return clientRepository.findAll().stream().map(clientConverter::toDto).toList();
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto) {
        Client entity = clientConverter.toEntity(clientDto);
        Client saved = clientRepository.save(entity);
        return clientConverter.toDto(saved);
    }
}

package ru.otus.converter;

import java.util.List;
import org.springframework.stereotype.Component;
import ru.otus.dto.ClientDto;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;

@Component
public class ClientConverter {

    public ClientDto toDto(Client client) {
        String street = client.address() != null ? client.address().street() : null;

        return new ClientDto(
                client.id(),
                client.name(),
                street,
                client.phones() != null
                        ? client.phones().stream().map(Phone::number).toList()
                        : java.util.List.of());
    }

    public Client toEntity(ClientDto dto) {
        Address address = (dto.street() != null && !dto.street().isBlank()) ? new Address(null, dto.street()) : null;

        List<Phone> phones = (dto.phones() != null)
                ? dto.phones().stream().map(number -> new Phone(null, number)).toList()
                : List.of();

        return new Client(dto.id(), dto.name(), address, phones);
    }
}

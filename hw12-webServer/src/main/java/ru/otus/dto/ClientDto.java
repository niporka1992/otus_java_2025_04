package ru.otus.dto;

import java.util.List;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;

public record ClientDto(String name, String street, List<String> phones) {

    public static ClientDto fromEntity(Client client) {
        return new ClientDto(
                client.getName(),
                client.getAddress() != null ? client.getAddress().getStreet() : null,
                client.getPhones().stream().map(Phone::getNumber).toList());
    }

    public Client toEntity() {
        Client client = new Client();
        client.setName(this.name);

        if (this.street != null && !this.street.isBlank()) {
            Address address = new Address();
            address.setStreet(this.street);
            client.setAddress(address);
        }

        if (this.phones != null && !this.phones.isEmpty()) {
            List<Phone> phoneEntities = this.phones.stream()
                    .map(number -> {
                        Phone phone = new Phone();
                        phone.setNumber(number);
                        phone.setClient(client);
                        return phone;
                    })
                    .toList();
            client.setPhones(phoneEntities);
        }

        return client;
    }
}

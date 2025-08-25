package ru.otus.model;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NamedEntityGraph(
        name = "Client.withAll",
        attributeNodes = {@NamedAttributeNode("address"), @NamedAttributeNode("phones")})
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "clients")
public class Client implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Phone> phones = new ArrayList<>();

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        setAddress(address);
        setPhones(phones);
    }

    public void setAddress(Address address) {
        this.address = Optional.ofNullable(address)
                .map(a -> {
                    var clonedAddress = new Address(a.getId(), a.getStreet());
                    clonedAddress.setClient(this);
                    return clonedAddress;
                })
                .orElse(null);
    }

    public void setPhones(List<Phone> phones) {
        this.phones = Optional.ofNullable(phones).orElse(Collections.emptyList()).stream()
                .map(phone -> {
                    var clonedPhone = new Phone(phone.getId(), phone.getNumber());
                    clonedPhone.setClient(this);
                    return clonedPhone;
                })
                .toList();
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        var clonedAddress = Optional.ofNullable(address)
                .map(a -> new Address(a.getId(), a.getStreet()))
                .orElse(null);
        var clonedPhones = phones.stream()
                .map(phone -> new Phone(phone.getId(), phone.getNumber()))
                .toList();
        return new Client(id, name, clonedAddress, clonedPhones);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id)
                && Objects.equals(name, client.name)
                && Objects.equals(address, client.address)
                && Objects.equals(phones, client.phones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, phones);
    }
}

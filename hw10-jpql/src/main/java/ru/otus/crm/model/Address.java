package ru.otus.crm.model;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "client")
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_gen")
    @SequenceGenerator(name = "address_gen", sequenceName = "address_seq", allocationSize = 1)
    private Long id;

    @Column(name = "street")
    private String street;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    public Address(Long id, String street) {
        this.id = id;
        this.street = street;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(id, address.id)
                && Objects.equals(street, address.street)
                && Objects.equals(client, address.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, client);
    }
}

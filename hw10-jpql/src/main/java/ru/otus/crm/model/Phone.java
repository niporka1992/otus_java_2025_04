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
@Table(name = "phone")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_gen")
    @SequenceGenerator(name = "phone_gen", sequenceName = "phone_seq", allocationSize = 1)
    private Long id;

    @Column(name = "number")
    private String number;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Phone(Long id, String number) {
        this.id = id;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return Objects.equals(id, phone.id)
                && Objects.equals(number, phone.number)
                && Objects.equals(client, phone.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, client);
    }
}

package pl.edu.pg.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * Klasa encyjna reprezentująca własności zamówienia w sklepie
 */

@Entity
@Table(name = "orderedparts")
@EqualsAndHashCode(of = "id")
public class OrderedParts {

    @Id
    private UUID id = UUID.randomUUID();

    @Getter
    @Setter
    @ManyToOne
    private Film film;

    @Getter
    @Setter
    private Integer amount;

}

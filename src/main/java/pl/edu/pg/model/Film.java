package pl.edu.pg.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * Klasa encyjna reprezentująca towar w sklepie (film).
 */
@Entity
@Table(name = "films")
@EqualsAndHashCode(of = "id")
@NamedQueries(value = {
        @NamedQuery(name = Film.FIND_ALL, query = "SELECT b FROM Film b")
})
public class Film {
    public static final String FIND_ALL = "Film.FIND_ALL";

    @Getter
    @Id
    UUID id = UUID.randomUUID();

    @Getter
    @Setter
    String title;

    @Getter
    @Setter
    Integer amount;

    @Getter
    @Setter
    String director;

    @Getter
    @Setter
    String productionCountry;

    @Getter
    @Setter
    Integer duration;

    @Getter
    @Setter
    Integer productionYear;

    @Getter
    @Setter
    Integer price;
}

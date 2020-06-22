package pl.edu.pg.services;

import org.springframework.stereotype.Service;
import pl.edu.pg.model.Film;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Komponent (serwis) biznesowy do realizacji operacji na filmach.
 */
@Service
public class FilmsService extends EntityService<Film> {

    //Instancja klasy EntityManger zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public FilmsService(EntityManager em) {

        //Film.class - klasa encyjna, na której będą wykonywane operacje
        //Film::getId - metoda klasy encyjnej do pobierania klucza głównego
        super(em, Film.class, Film::getId);
    }

    /**
     * Pobranie wszystkich filmów z bazy danych.
     *
     * @return lista filmów
     */
    public List<Film> findAll() {
        //pobranie listy wszystkich filmów za pomocą zapytania nazwanego (ang. named query)
        //zapytanie jest zdefiniowane w klasie Film
        return em.createNamedQuery(Film.FIND_ALL, Film.class).getResultList();
    }

}

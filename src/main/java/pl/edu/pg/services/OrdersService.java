package pl.edu.pg.services;


import pl.edu.pg.model.Film;
import pl.edu.pg.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pg.model.OrderedParts;
import pl.edu.pg.services.exceptions.*;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Komponent (serwis) biznesowy do realizacji operacji na zamówieniach.
 */
@Service
public class OrdersService extends EntityService<Order> {

    //Instancja klasy EntityManger zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public OrdersService(EntityManager em) {

        //Order.class - klasa encyjna, na której będą wykonywane operacje
        //Order::getId - metoda klasy encyjnej do pobierania klucza głównego
        super(em, Order.class, Order::getId);
    }

    /**
     * Pobranie wszystkich zamówień z bazy danych.
     *
     * @return lista zamówień
     */
    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    }

    /**
     * Złożenie zamówienia w sklepie.
     * <p>
     * Zamówienie jest akceptowane, jeśli wszystkie objęte nim produkty są dostępne (przynajmniej 1 sztuka),
     * oraz spełnia nastepujący warunki: nie więcej niż x produktów w zamówieniu, koszt zamówienia	nie	mniejsz niż	x
     * W wyniku złożenia zamówienia liczba dostępnych sztuk produktów jest zmniejszana o ilość zamówienego towaru.
     * Metoda działa w sposób transakcyjny - zamówienie jest albo akceptowane w całości albo odrzucane w całości.
     * W razie braku produktu wyrzucany jest wyjątek OutOfStockException.
     * Jeżeli zamówenie nie spełnia kreteriów walidacji wyrzucany jest wyjątek ALotOfProductsException lub LowTotalAmountException
     *
     * @param order zamówienie do przetworzenia
     * @throws ALotOfProductsException więcej niż x produktów w zamówieniu
     * @throws LowTotalAmountException koszt zamówienia	jest mniejszy niż x
     */

    @Transactional
    public void placeOrder(Order order) {

        // jeżeli zamówenie jest puste wyrzucamy wyjątek
        if (order.getFilmsInOrder().size() == 0) {
            throw new EmptyOrderException();
        }

        // jeżeli w zamóweniu ilość filmów jest ujemna - wyrzycamy wyjątek
       for (OrderedParts filmOrder : order.getFilmsInOrder()){
           if (filmOrder.getAmount() < 0)
               throw new NegativeProductsAmountException();
           // jeżeli zamówenie jest puste wyrzucamy wyjątek
           else if (filmOrder.getAmount() == 0)
               throw new EmptyOrderException();
       }

        // walidacja zamóweniea
        // nie więcej niż x produktów w zamówieniu,
        int size = 0, maxSize = 20;
        for (OrderedParts filmOrder : order.getFilmsInOrder()){
            Film film = em.find(Film.class, filmOrder.getFilm().getId());
            size += filmOrder.getAmount();
        }

        //wyjątek z hierarchii RuntineException powoduje wycofanie transakcji (rollback)
        if (size > maxSize)
            throw new ALotOfProductsException();


        // walidacja zamóweniea
        // koszt zamówienia	nie	mniejszy niż x
        int totalPriceAmount = 0, minPriceAmount = 100;
        for (OrderedParts filmOrder : order.getFilmsInOrder()){
            Film film = em.find(Film.class, filmOrder.getFilm().getId());
            totalPriceAmount += film.getPrice() * filmOrder.getAmount(); // changed
        }


        //wyjątek z hierarchii RuntineException powoduje wycofanie transakcji (rollback)
        if (totalPriceAmount < minPriceAmount)
            throw new LowTotalAmountException();


        for (OrderedParts filmOrder : order.getFilmsInOrder()) {
            Film film = em.find(Film.class, filmOrder.getFilm().getId());
            if (film.getAmount() < 1 || film.getAmount() < filmOrder.getAmount()) {
                //wyjątek z hierarchii RuntineException powoduje wycofanie transakcji (rollback)
                throw new OutOfStockException();
            } else {
                int newAmount = film.getAmount() - filmOrder.getAmount();
                film.setAmount(newAmount);
            }

        }


        save(order);

        //jeśli wcześniej nie został wyrzucony jakiś wyjątek (z trzech możliwych),
        // zamówienie jest zapisywane w bazie danych

    }
}

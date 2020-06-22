package pl.edu.pg;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import pl.edu.pg.model.Film;
import pl.edu.pg.model.Order;
import pl.edu.pg.model.OrderedParts;
import pl.edu.pg.services.OrdersService;
import pl.edu.pg.services.exceptions.*;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;


@RunWith(MockitoJUnitRunner.class)
public class OrdersServiceTest {

    @Mock
    EntityManager em;

    @Test(expected = OutOfStockException.class)
    public void whenOrderedFilmNotAvailable_placeOrderThrowsOutOfStockEx() {
        //Arrange
        Order order = new Order();
        OrderedParts orderedParts = new OrderedParts();
        Film film = new Film();
        film.setAmount(0);
        film.setPrice(25);

        orderedParts.setFilm(film);
        orderedParts.setAmount(10); // value*price must be greater than minPriceAmount==100
        // due to order validation
        order.getFilmsInOrder().add(orderedParts);

        Mockito.when(em.find(Film.class, film.getId())).thenReturn(film);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }

    @Test(expected = LowTotalAmountException.class)
    public void whenOrderAmountIsLowerThanOrderValidationCondition_placeOrderThrowsLowTotalAmountEx(){
            // the minimum shoping cart amount is currently set to 100

            //Arrange
            Order order = new Order();
            OrderedParts orderedParts = new OrderedParts();
            Film film = new Film();
            film.setAmount(10);
            film.setPrice(25);

            orderedParts.setFilm(film);
            orderedParts.setAmount(2);
            order.getFilmsInOrder().add(orderedParts);

            Mockito.when(em.find(Film.class, film.getId())).thenReturn(film);

            OrdersService ordersService = new OrdersService(em);

            //Act
            ordersService.placeOrder(order);

            //Assert - exception expected

    }

    @Test(expected = ALotOfProductsException.class)
    public void whenOrderIncludesALotOfProducts_placeOrderThrowsALotOfProductsEx(){
        // the maximum amount of products in order is currently set to 20

        //Arrange
        Order order = new Order();

        OrderedParts orderedParts1 = new OrderedParts();
        Film film1 = new Film();
        film1.setAmount(50);
        film1.setPrice(25);

        OrderedParts orderedParts2 = new OrderedParts();
        Film film2 = new Film();
        film2.setAmount(10);
        film2.setPrice(45);

        orderedParts1.setFilm(film1);
        orderedParts1.setAmount(10); // value*price must be greater than minPriceAmount==100 (in sum)
        // due to order validation

        orderedParts2.setFilm(film2);
        orderedParts2.setAmount(12); // value*price must be greater than minPriceAmount==100 (in sum)
        // due to order validation

        order.getFilmsInOrder().add(orderedParts1);
        order.getFilmsInOrder().add(orderedParts2);

        Mockito.when(em.find(Film.class, film1.getId())).thenReturn(film1);
        Mockito.when(em.find(Film.class, film2.getId())).thenReturn(film2);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected

    }

    @Test(expected = NegativeProductsAmountException.class)
    public void negativeAmountOfProductsAmountInOrder_placeOrderThrowsNegativeProductsAmountEx(){
        Order order = new Order();

        OrderedParts orderedParts = new OrderedParts();

        Film film = new Film();
        film.setAmount(10);
        film.setPrice(25);

        orderedParts.setFilm(film);
        orderedParts.setAmount(-5);

        order.getFilmsInOrder().add(orderedParts);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }

    @Test(expected = EmptyOrderException.class)
    public void whenOrderHasProductsButAmountOfOneOfThemIsSetToZero_placeOrderThrowsEmptyOrderEx(){
        //Arrange
        Order order = new Order();
        OrderedParts orderedParts = new OrderedParts();

        Film film = new Film();
        film.setPrice(15);
        film.setAmount(5);

        orderedParts.setFilm(film);
        orderedParts.setAmount(0);

        order.getFilmsInOrder().add(orderedParts);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }

    @Test(expected = EmptyOrderException.class)
    public void whenOrderIsEmpty_placeOrderThrowsEmptyOrderEx(){
        //Arrange
        Order order = new Order();
        OrderedParts orderedParts = new OrderedParts();

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }
    
    @Test
    public void checkFilmsAmountAfterOrder(){
        //Arrange
        Order order = new Order();

        OrderedParts orderedParts1 = new OrderedParts();
        Film film1 = new Film();
        film1.setAmount(50);
        film1.setPrice(25);

        OrderedParts orderedParts2 = new OrderedParts();
        Film film2 = new Film();
        film2.setAmount(10);
        film2.setPrice(45);

        orderedParts1.setFilm(film1);
        orderedParts1.setAmount(10); // value*price must be greater than minPriceAmount==100 (in sum)
        // due to order validation

        orderedParts2.setFilm(film2);
        orderedParts2.setAmount(7); // value*price must be greater than minPriceAmount==100 (in sum)
        // due to order validation

        order.getFilmsInOrder().add(orderedParts1);
        order.getFilmsInOrder().add(orderedParts2);

        Mockito.when(em.find(Film.class, film1.getId())).thenReturn(film1);
        Mockito.when(em.find(Film.class, film2.getId())).thenReturn(film2);

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert
        assertEquals(film1.getAmount().intValue(), 50-10);
        assertEquals(film2.getAmount().intValue(), 10-7);
        Mockito.verify(em, times(1)).persist(order);

    }

    @Test
    public void checkTotalOrderPriceAmount(){
        //Arrange
        Order order = new Order();

        List<Film> films = new ArrayList<>();
        List<OrderedParts> orderedParts = new ArrayList<>();

        for (int i = 0; i < 3; i++){
            films.add(new Film());
            orderedParts.add(new OrderedParts());
        }

        films.get(0).setAmount(50);
        films.get(0).setPrice(25);

        films.get(1).setAmount(10);
        films.get(1).setPrice(45);

        films.get(2).setAmount(55);
        films.get(2).setPrice(30);

        orderedParts.get(0).setFilm(films.get(0));
        orderedParts.get(0).setAmount(3); // value*price must be greater than minPriceAmount==100 (in sum)
        // due to order validation

        orderedParts.get(1).setFilm(films.get(1));
        orderedParts.get(1).setAmount(7); // value*price must be greater than minPriceAmount==100 (in sum)
        // due to order validation

        orderedParts.get(2).setFilm(films.get(2));
        orderedParts.get(2).setAmount(7); // value*price must be greater than minPriceAmount==100 (in sum)
        // due to order validation

        int prevPrice = 0;
        for (int i = 0; i < 3; i++){
            order.getFilmsInOrder().add(orderedParts.get(i));

            //Mockito.when(em.find(Film.class, films.get(i).getId())).thenReturn(films.get(i));

            prevPrice += orderedParts.get(i).getAmount() * orderedParts.get(i).getFilm().getPrice();
        }


        Mockito.when(em.find(Film.class, films.get(0).getId())).thenReturn(films.get(0));
        Mockito.when(em.find(Film.class, films.get(1).getId())).thenReturn(films.get(1));
        Mockito.when(em.find(Film.class, films.get(2).getId())).thenReturn(films.get(2));

        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        int currentPrice = 0;
        for (OrderedParts orderedFilm : order.getFilmsInOrder()){
            currentPrice += orderedFilm.getFilm().getPrice() * orderedFilm.getAmount();
        }

        //Assert
        assertEquals(currentPrice, prevPrice);
        Mockito.verify(em, times(1)).persist(order);
    }

    @Test
    public void createManyOrdersOfTheSameFilm(){
        // the minimum shoping cart amount is currently set to 100

        //Arrange
        Order order = new Order();

        OrderedParts orderedParts = new OrderedParts();
        Film film = new Film();
        film.setAmount(10);
        film.setPrice(25);

        orderedParts.setFilm(film);
        orderedParts.setAmount(5); // value*price must be greater than minPriceAmount==100
        // due to order validation
        order.getFilmsInOrder().add(orderedParts);

        Mockito.when(em.find(Film.class, film.getId())).thenReturn(film);

        OrdersService ordersService = new OrdersService(em);

        int currentAmount = film.getAmount();

        //Act
        ordersService.placeOrder(order);
        ordersService.placeOrder(order);

        //Assert

        assertEquals(currentAmount, 2*5);
        Mockito.verify(em, times(2)).persist(order);
    }


}

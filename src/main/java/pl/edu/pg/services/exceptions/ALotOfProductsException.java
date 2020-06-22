package pl.edu.pg.services.exceptions;

/**
 * Wyjątek sygnalizujący o tym, że użytkownik chce zamówić za dużo towarów
 *
 * Wystąpienie wyjątku z hierarchii RuntimeException w warstwie biznesowej
 * powoduje wycofanie transakcji (rollback).
 */
public class ALotOfProductsException extends RuntimeException {
}

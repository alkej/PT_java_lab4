package pl.edu.pg.services.exceptions;

/**
 * Wyjątek sygnalizujący o tym, że użytkownik nie ma żadnego productu w zamóweniu
 *
 * Wystąpienie wyjątku z hierarchii RuntimeException w warstwie biznesowej
 * powoduje wycofanie transakcji (rollback).
 */
public class EmptyOrderException extends RuntimeException {
}

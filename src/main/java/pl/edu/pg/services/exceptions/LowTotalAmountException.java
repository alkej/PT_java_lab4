package pl.edu.pg.services.exceptions;

/**
 * Wyjątek sygnalizujący o tym, suma tówarów w zamóweniu jest za mała
 *
 * Wystąpienie wyjątku z hierarchii RuntimeException w warstwie biznesowej
 * powoduje wycofanie transakcji (rollback).
 */
public class LowTotalAmountException extends RuntimeException {
}

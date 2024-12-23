package ru.antoncharov.socks.exception;

public class WrongCSVException extends RuntimeException {
    public WrongCSVException(String message) {
        super(message);
    }
}

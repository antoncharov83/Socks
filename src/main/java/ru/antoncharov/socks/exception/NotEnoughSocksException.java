package ru.antoncharov.socks.exception;

public class NotEnoughSocksException extends RuntimeException{
    public NotEnoughSocksException(String message) {
        super(message);
    }
}

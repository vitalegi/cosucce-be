package it.vitalegi.cosucce.security.exception;

public class MissingCookieException extends RuntimeException {
    public MissingCookieException(String message) {
        super(message);
    }
}

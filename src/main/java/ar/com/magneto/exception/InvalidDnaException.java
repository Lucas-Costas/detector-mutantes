package ar.com.magneto.exception;

public class InvalidDnaException extends RuntimeException {
    public InvalidDnaException(String message) {
        super(message);
    }
}

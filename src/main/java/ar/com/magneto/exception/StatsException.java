package ar.com.magneto.exception;

public class StatsException extends RuntimeException {

    public StatsException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatsException(String message) {
        super(message);
    }

}

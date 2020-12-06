package ar.com.magneto.exception;

public class RedisException extends RuntimeException {

    public RedisException(String message) {
        super(message);
    }

}

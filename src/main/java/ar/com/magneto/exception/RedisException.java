package ar.com.magneto.exception;

public class RedisException extends RuntimeException {

    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, String key, Exception ex) {
        super(message+"'"+key+"'",ex);
    }
}

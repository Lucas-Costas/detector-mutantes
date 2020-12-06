package ar.com.magneto.exception;

public class Neo4jAdapterException extends RuntimeException {

    public Neo4jAdapterException(String message) {
        super(message);
    }

    public Neo4jAdapterException(String message, Throwable ex) {
        super(message,ex);
    }

}

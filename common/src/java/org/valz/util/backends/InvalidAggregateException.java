package org.valz.util.backends;

public class InvalidAggregateException extends Exception {
    public InvalidAggregateException() {
    }

    public InvalidAggregateException(String message) {
        super(message);
    }

    public InvalidAggregateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAggregateException(Throwable cause) {
        super(cause);
    }
}
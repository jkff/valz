package org.valz.util.protocol;

/**
 * Created on: 28.03.2010 10:46:36
 */
public class ConnectionException extends Exception {
    public ConnectionException() {
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }
}
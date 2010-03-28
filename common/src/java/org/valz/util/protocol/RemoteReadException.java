package org.valz.util.protocol;

/**
 * Created on: 28.03.2010 10:46:36
 */
public class RemoteReadException extends Exception {
    public RemoteReadException() {
    }

    public RemoteReadException(String message) {
        super(message);
    }

    public RemoteReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteReadException(Throwable cause) {
        super(cause);
    }
}

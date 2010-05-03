package org.valz.util.backends;

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

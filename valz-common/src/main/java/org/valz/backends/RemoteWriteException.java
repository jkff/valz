package org.valz.backends;

public class RemoteWriteException extends Exception {
    public RemoteWriteException() {
    }

    public RemoteWriteException(String message) {
        super(message);
    }

    public RemoteWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteWriteException(Throwable cause) {
        super(cause);
    }
}
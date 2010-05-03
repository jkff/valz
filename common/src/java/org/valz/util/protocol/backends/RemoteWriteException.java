package org.valz.util.protocol.backends;

/**
 * Created on: 28.03.2010 10:46:36
 */
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
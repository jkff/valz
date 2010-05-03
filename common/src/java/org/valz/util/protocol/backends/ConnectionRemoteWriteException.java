package org.valz.util.protocol.backends;

/**
 * Created on: 28.03.2010 10:46:36
 */
public class ConnectionRemoteWriteException extends RemoteWriteException {
    public ConnectionRemoteWriteException() {
    }

    public ConnectionRemoteWriteException(String message) {
        super(message);
    }

    public ConnectionRemoteWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionRemoteWriteException(Throwable cause) {
        super(cause);
    }
}
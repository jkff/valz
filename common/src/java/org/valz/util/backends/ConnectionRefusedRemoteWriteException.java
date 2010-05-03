package org.valz.util.backends;

public class ConnectionRefusedRemoteWriteException extends RemoteWriteException {
    public ConnectionRefusedRemoteWriteException() {
    }

    public ConnectionRefusedRemoteWriteException(String message) {
        super(message);
    }

    public ConnectionRefusedRemoteWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionRefusedRemoteWriteException(Throwable cause) {
        super(cause);
    }
}
package org.valz.util.backends;

public class ConnectionRefusedRemoteWriteException extends RemoteWriteException {
    private static final long serialVersionUID = 8971593681805788053L;

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
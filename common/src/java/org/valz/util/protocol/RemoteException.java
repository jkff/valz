package org.valz.util.protocol;

/**
 * Created on: 28.03.2010 10:46:36
 */
public class RemoteException extends Exception {
    public RemoteException() {
    }

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }
}

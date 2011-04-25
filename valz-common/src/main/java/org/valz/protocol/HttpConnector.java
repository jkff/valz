package org.valz.protocol;

import org.jetbrains.annotations.NotNull;
import org.valz.util.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpConnector {
    public static String post(@NotNull String serverURL, @NotNull String data) throws
            ConnectionException {
        URL url = null;
        try {
            url = new URL(serverURL);
        } catch (MalformedURLException e) {
            throw new ConnectionException(e);
        }
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection)url.openConnection();

            try {
                connection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new AssertionError(e);
            }
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            IOUtils.writeOutputStream(connection.getOutputStream(), data, "UTF-8");
            return IOUtils.readInputStream(connection.getInputStream(), "UTF-8");
        } catch (IOException e) {
            throw new ConnectionException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpConnector() {
    }
}

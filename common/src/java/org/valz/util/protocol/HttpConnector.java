package org.valz.util.protocol;

import org.jetbrains.annotations.NotNull;
import org.valz.util.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnector {
    public static String post(@NotNull String serverURL, @NotNull String data) throws IOException {
        URL url = new URL(serverURL);
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            IOUtils.writeOutputStream(connection.getOutputStream(), data, "UTF-8");
            return IOUtils.readInputStream(connection.getInputStream(), "UTF-8");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpConnector() {
    }
}

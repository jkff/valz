package org.valz.util.protocol;

import flexjson.JSONSerializer;
import org.jetbrains.annotations.NotNull;
import org.valz.util.io.IOUtils;
import org.valz.util.protocol.messages.RequestMessage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnector {
    public static String post(@NotNull String serverURL, @NotNull String data) throws IOException {
        URL url = new URL(serverURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        Writer w;

        try {
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            w = new OutputStreamWriter(connection.getOutputStream());
            try {
                w.write(data);
            } finally {
                IOUtils.closeSilently(w);
            }

            InputStream is = connection.getInputStream();
            try {
                StringBuilder sb = new StringBuilder();
                String line;
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();

            } finally {
                IOUtils.closeSilently(is);
            }
        } finally {
            connection.disconnect();
        }
    }

    private HttpConnector() {
    }
}

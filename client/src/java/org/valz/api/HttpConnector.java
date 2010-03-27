package org.valz.api;

import org.jetbrains.annotations.NotNull;
import org.valz.util.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpConnector {
    public static String post(@NotNull String serverURL, @NotNull String data) throws IOException {
        URL url = new URL(serverURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            OutputStream os = connection.getOutputStream();
            try {
                Writer writer = new OutputStreamWriter(os);
                writer.write(data);
            } finally {
                IOUtils.closeOutputSilently(os);
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
                IOUtils.closeInputSilently(is);
            }
        } finally {
            connection.disconnect();
        }
    }


    
    private HttpConnector() {
    }
}

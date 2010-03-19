package org.valz.api;

import org.valz.util.aggregates.Aggregate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ValImpl<T> implements Val<T> {
    private String _name;

    ValImpl(String name) {
        _name = name;
    }

    public void submit(T sample) {
        Aggregate<T> aggregate = Valz.getAggregate(_name);

        try {
            
            URL url = new URL("http://localhost:8080");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);


            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            try {
                writer.write(String.format("name=%s", _name));
                writer.write(String.format("&value=%s", sample));
            }
            finally {
                writer.close();
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = rd.readLine())!=null)
            {
                sb.append(line + '\n');
            }

            System.out.println(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}

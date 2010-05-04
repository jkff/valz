package org.valz.util.io;

import org.jetbrains.annotations.NotNull;

import java.io.*;

public class IOUtils {
    public static void closeSilently(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    public static String readInputStream(InputStream stream, String encoding) throws IOException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(stream.available());
            byte[] buf = new byte[65536];
            int n;
            while (-1 != (n = stream.read(buf, 0, buf.length))) {
                bos.write(buf, 0, n);
            }
            return new String(bos.toByteArray(), encoding);
        } finally {
            closeSilently(stream);
        }
    }

    public static void writeOutputStream(OutputStream stream, @NotNull String data,
                                         @NotNull String encoding) throws IOException {
        Writer w = null;
        try {
            w = new OutputStreamWriter(stream, encoding);
            w.write(data);
        } finally {
            closeSilently(w);
        }
    }



    private IOUtils() {
    }
}

package org.valz.util.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class IOUtils {
    public static void closeInputSilently(@Nullable InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    public static void closeOutputSilently(@Nullable OutputStream os) {
        try {
            if (os != null) {
                os.close();
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
            closeInputSilently(stream);
        }
    }

    public static void writeOutputStream(OutputStream stream, @NotNull String data, @NotNull String encoding) throws IOException {
        try {
            Writer out = new OutputStreamWriter(stream, encoding);
            out.write(data);
        } finally {
            closeOutputSilently(stream);
        }
    }



    private IOUtils() {
    }
}

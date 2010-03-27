package org.valz.util.io;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    public static void closeInputSilently(@Nullable InputStream is) {
        try {
            if(is != null)
                is.close();
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
}

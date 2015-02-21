package fr.travauxetservices.tools;

import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;

import java.io.*;

/**
 * Created by Phobos on 26/11/14.
 */
public class IOToolkit {
    public static String getResourceAsText(String s) {
        final StringBuilder result = new StringBuilder();
        try {
            InputStream is = IOToolkit.class.getClassLoader().getResourceAsStream(s);
            final Reader input = new InputStreamReader(is);
            try {
                char[] buffer = new char[8192];
                int read;
                while ((read = input.read(buffer, 0, buffer.length)) > 0) {
                    result.append(buffer, 0, read);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static String getResourceAsText(FileResource f) {
        final StringBuilder result = new StringBuilder();
        try {
            InputStream is = f.getStream().getStream();
            final Reader input = new InputStreamReader(is);
            try {
                char[] buffer = new char[8192];
                int read;
                while ((read = input.read(buffer, 0, buffer.length)) > 0) {
                    result.append(buffer, 0, read);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    static public class ByteArraySource implements StreamResource.StreamSource {
        private byte[] bytes;

        public ByteArraySource(byte[] bytes) {
            this.bytes = bytes;
        }

        public InputStream getStream() {
            return new ByteArrayInputStream(bytes);
        }
    }
}

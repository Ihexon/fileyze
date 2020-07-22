package io.github.ihexon.spi;

import java.io.*;
import java.util.ArrayList;

/**
 * Default implementation of ThrowableRenderer using
 * Throwable.printStackTrace.
 *
 * @since 1.0
 */
public class DefaultThrowableRenderer implements ThrowableRenderer {
    /**
     * Construct new instance.
     */
    public DefaultThrowableRenderer() {
    }

    /**
     * {@inheritDoc}
     */
    public String[] doRender(final Throwable throwable) {
        return render(throwable);
    }


    /**
     * Render throwable using Throwable.printStackTrace.
     * @param throwable throwable, may not be null.
     * @return string representation.
     */
    public static String[] render(final Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
        } catch(RuntimeException ex) {
        }
        pw.flush();
        LineNumberReader reader = new LineNumberReader(
                new StringReader(sw.toString()));
        ArrayList lines = new ArrayList();
        try {
            String line = reader.readLine();
            while(line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch(IOException ex) {
            if (ex instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            lines.add(ex.toString());
        }
        String[] tempRep = new String[lines.size()];
        lines.toArray(tempRep);
        return tempRep;
    }
}

package io.github.ihexon.common;

import io.github.ihexon.spi.LoggingEvent;

import java.io.*;

public class WriterAppender extends AppenderSkeleton {

    protected boolean immediateFlush = true;
    protected String encoding;
    protected QuietWriter qw;

    public final static String LINE_SEP = System.getProperty("line.separator");
    StringBuffer sbuf = new StringBuffer(128);

    public WriterAppender() {
    }

    public WriterAppender(OutputStream os) {
        this(new OutputStreamWriter(os));
    }

    public WriterAppender(Writer writer) {
        this.setWriter(writer);
    }

    public void setImmediateFlush(boolean value) {
        immediateFlush = value;
    }

    public boolean getImmediateFlush() {
        return immediateFlush;
    }

    /**
     * This method is called by the {@link AppenderSkeleton#doAppend}
     * method.
     */
    public void append(LoggingEvent event) {
        if (!checkEntryConditions()) {
            return;
        }
        subAppend(event);
    }


    public String format(LoggingEvent event) {

        sbuf.setLength(0);
//        sbuf.append(event.getLevel().toString());
        sbuf.append("- ");
        sbuf.append(event.getRenderedMessage());
        sbuf.append(LINE_SEP);
        return sbuf.toString();
    }

    protected void subAppend(LoggingEvent event) {
        this.qw.write(this.format(event));
        String[] s = event.getThrowableStrRep();
        if (s != null) {
            int len = s.length;
            for (int i = 0; i < len; i++) {
                this.qw.write(s[i]);
                this.qw.write(LINE_SEP);
            }
        }
        if (shouldFlush(event)) {
            this.qw.flush();
        }
    }

    protected boolean shouldFlush(final LoggingEvent event) {
        return immediateFlush;
    }


    protected boolean checkEntryConditions() {
        if (this.closed) {
            System.err.println("Not allowed to write to a closed appender.");
            return false;
        }

        if (this.qw == null) {
            System.err.println("No output stream or file set for the appender named [" +
                    name + "].");
            return false;
        }
        return true;
    }

    protected void reset() {
        closeWriter();
        this.qw = null;
        //this.tp = null;
    }

    public
    synchronized void close() {
        if (this.closed)
            return;
        this.closed = true;
        writeFooter();
        reset();
    }

    private void writeFooter() {
        String f = "----------- WatchMe End -----------\n";
        if (f != null && this.qw != null) {
            this.qw.write(f);
            this.qw.flush();
        }
    }

    protected void closeWriter() {
        if (qw != null) {
            try {
                qw.close();
            } catch (IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                System.err.println("Could not close " + qw);
                System.err.println(e.getMessage());
            }
        }
    }

    public synchronized void setWriter(Writer writer) {
        reset();
        this.qw = new QuietWriter(writer);
        writeHeader();
    }

    protected void writeHeader() {
        String h = "----------- WatchMe Start -----------\n";
        if (h != null && this.qw != null)
            this.qw.write(h);
    }


    protected OutputStreamWriter createWriter(OutputStream os) {
        OutputStreamWriter retval = null;
        String enc = getEncoding();
        if (enc != null) {
            try {
                retval = new OutputStreamWriter(os, enc);
            } catch (IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                System.err.println("Error initializing output writer.");
                System.err.println("Unsupported encoding?");
            }
        }
        if (retval == null) {
            retval = new OutputStreamWriter(os);
        }
        return retval;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String value) {
        encoding = value;
    }

    @Override
    public void activateOptions() {
    }
}

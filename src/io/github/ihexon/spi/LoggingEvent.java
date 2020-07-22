package io.github.ihexon.spi;

/**
 * The internal representation of logging events of Watcher.
 *
 * <p> Origin code from apache-log4j </p>
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author James P.
 * @author ZZH
 * @since v1.0
 */

public class LoggingEvent {

    private static long startTime = System.currentTimeMillis();

    transient private Object message;
    public final long timeStamp;
    private ThrowableInformation throwableInfo;
    private String threadName;


    /**
     * Returns the time when the application started, in milliseconds
     */
    public static long getStartTime() {
        return startTime;
    }

    public String getThreadName() {
        if (threadName == null)
            threadName = (Thread.currentThread()).getName();
        return threadName;
    }

    public LoggingEvent(Object message, Throwable throwable) {
        this.message = message;
        if (throwable != null) {
            this.throwableInfo = new ThrowableInformation(throwable);
        }
        timeStamp = System.currentTimeMillis();
    }

    private String renderedMessage = null;

    public String getRenderedMessage() {
        if (renderedMessage == null && message != null) {
            if (message instanceof String)
                renderedMessage = (String) message;
        }
        return renderedMessage;
    }

    /**
     * Return this event's throwable's string[] representaion.
     */
    public String[] getThrowableStrRep() {
        if (throwableInfo == null)
            return null;
        else
            return throwableInfo.getThrowableStrRep();
    }
}

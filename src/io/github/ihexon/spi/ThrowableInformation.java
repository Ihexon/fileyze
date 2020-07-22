package io.github.ihexon.spi;

public class ThrowableInformation {

    private Throwable throwable;
    private String[] rep;

    /**
     * Create a new instance.
     *
     * @param throwable throwable, may not be null.
     * @since 1.0
     */
    public ThrowableInformation(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Create new instance.
     *
     * @param r String representation of throwable.
     * @since 1.2.15
     */
    public ThrowableInformation(final String[] r) {
        if (r != null) {
            rep = (String[]) r.clone();
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }


    public synchronized String[] getThrowableStrRep() {
        if(rep == null) {
                rep = DefaultThrowableRenderer.render(throwable);
        }
        return (String[]) rep.clone();
    }
}

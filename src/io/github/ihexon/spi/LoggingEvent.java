package io.github.ihexon.spi;
public class LoggingEvent {

	transient private Object message;
	private transient Throwable throwable;
	public final long timeStamp;

	public LoggingEvent(Object message, Throwable throwable) {
		this.message = message;
		if (throwable != null) {
			this.throwable = throwable;
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

	public String getThrowableStrRep() {
		if(throwable ==  null)
			return null;
		else
			return throwable.getMessage();
	}
}

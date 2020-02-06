package io.github.ihexon.common;

import io.github.ihexon.spi.LoggingEvent;

public abstract class AppenderSkeleton implements Appender {
	protected String name;
	protected boolean closed = false;

	public AppenderSkeleton() {
		super();
	}

	@Override
	public void close() {}

	@Override
	public synchronized void doAppend(LoggingEvent event) {
		if (closed) {
			System.err.println("Attempted to append to closed appender named [" + name + "].");
			return;
		}
		this.append(event);
	}

	@Override
	public String getName() {
		return this.name;
	}

	abstract protected void append(LoggingEvent event);

	public void finalize() {
		// An appender might be closed then garbage collected. There is no
		// point in closing twice.
		if (this.closed)
			return;
		System.out.println("Finalizing appender named [" + name + "].");
		close();
	}
}

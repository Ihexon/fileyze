package io.github.ihexon.common;

import io.github.ihexon.spi.LoggingEvent;

public interface Appender {
	public void close();

	public void doAppend(LoggingEvent event);

	public String getName();

	public void activateOptions();
}

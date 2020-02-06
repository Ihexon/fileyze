package io.github.ihexon.common;

import io.github.ihexon.spi.LoggingEvent;

public class Formater {
	StringBuffer sbuf = new StringBuffer(128);
	public final static String LINE_SEP = System.getProperty("line.separator");

	public
	String format(LoggingEvent event) {
		sbuf.setLength(0);
		sbuf.append(event.getRenderedMessage());
		sbuf.append(LINE_SEP);
		return sbuf.toString();
	}
}

package io.github.ihexon.common;

import io.github.ihexon.ControlOverrides;
import io.github.ihexon.logutils.AppenderAttachableImpl;
import io.github.ihexon.spi.LoggingEvent;
import io.github.ihexon.utils.control.Control;

import java.io.IOException;

public class PrintUtils {

	private static PrintUtils printUtils = null;

	Appender ConsoleAppender;
	Appender FileAppender;
	AppenderAttachableImpl aai;

	public static PrintUtils getSingleton() {
		return printUtils;
	}

	public static void initSingleton(Control control) throws IOException {
		printUtils = new PrintUtils();
		printUtils.init(control);
	}

	public static void stdPrintln(Object x) {
		String s = String.valueOf(x);
		System.out.println(s);
	}

	public static void werrPrintln(Object x) {
		String s = String.valueOf(x);
		System.err.println(s);
	}


	private void init(Control control) throws IOException {
		ConsoleAppender = new ConsoleAppender();
		addAppender(ConsoleAppender);
		if (control.logFile != null){
			FileAppender = new FileAppender(control.logFile.toString());
			addAppender(FileAppender);
		}
	}

	public void info(Object message) {
		Log(message, null);
	}

	// this method not TESTED !!! I DONT KNOW IF IT WORKS RIGHT !!!
	void info(Object message, Throwable t) {
		Log(message, t);
	}

	protected void Log(Object message, Throwable t) {
		callAppenders(new LoggingEvent(message, t));
	}


	synchronized
	public
	void addAppender(Appender newAppender) {
		if(aai == null) {
			aai = new AppenderAttachableImpl();
		}
		aai.addAppender(newAppender);
	}

	private void callAppenders(LoggingEvent event) {
		aai.appendLoopOnAppenders(event);
	}

	public void closeAppenders() {
		if (aai != null){
			aai.closeNestedAppenders();
		}
	}
}

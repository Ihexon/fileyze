package io.github.ihexon.common;

import io.github.ihexon.logutils.AppenderAttachableImpl;
import io.github.ihexon.spi.LoggingEvent;
import io.github.ihexon.utils.control.Control;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class PrintUtils {

	Appender ConsoleAppender;
	Appender FileAppender;
	AppenderAttachableImpl aai;


	public static void setPrintln(String s) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream(s));
			System.setErr(ps);
			System.setOut(ps);
		} catch (FileNotFoundException e) {
			System.err.format("File %s not found", s);
		}
	}

	public static void stdPrintln(Object x) {
		String s = String.valueOf(x);
		System.out.println(s);
	}

	public static void werrPrintln(Object x) {
		String s = String.valueOf(x);
		System.err.println(s);
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
		if (ConsoleAppender == null){
		ConsoleAppender = new ConsoleAppender();
		addAppender(ConsoleAppender);
		}
		if (FileAppender == null){
				FileAppender = new FileAppender();
				((FileAppender) FileAppender).setFile("/tmp/zzh");
				FileAppender.activateOptions();
				addAppender(FileAppender);
		}
		aai.appendLoopOnAppenders(event);
	}

	public void closeAppenders() {
		if (aai != null){
			aai.closeNestedAppenders();
		}
	}

	// Test Code
	public static void main(String[] args) {
		PrintUtils printUtils = new PrintUtils();
		printUtils.info("YYF F**K YOU !");
		printUtils.info("15 RMB !F**K YOU");
		printUtils.closeAppenders();
	}
}

package io.github.ihexon.common;

import io.github.ihexon.spi.LoggingEvent;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;

public class PrintUtils {
	Appender appender;

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

	private void callAppenders(LoggingEvent event) {
		if (appender == null){
		appender = new ConsoleAppender();
		}
		appender.doAppend(event);
	}

	synchronized void closeAppenders() {
		Appender a = (Appender) appender;
		if (a instanceof WriterAppender) {
			a.close();
		}
	}

	// Test Code
	public static void main(String[] args) {
		PrintUtils printUtils = new PrintUtils();
		printUtils.info("YYF F**K YOU !");
		printUtils.info("10 RMB !F**K YOU");
		printUtils.closeAppenders();
	}
}

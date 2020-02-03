package io.github.ihexon.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class PrintUtils {

	public static void setPrintln(String s) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream(s));
			System.setErr(ps);
			System.setOut(ps);
		} catch (FileNotFoundException e){
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

	public static void ErrPrintln(Object x) {
		String s = String.valueOf(x);
		System.err.println(s);
	}
}

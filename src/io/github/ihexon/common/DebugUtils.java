package io.github.ihexon.common;

public class DebugUtils {

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

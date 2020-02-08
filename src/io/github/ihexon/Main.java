package io.github.ihexon;

import io.github.ihexon.common.PrintUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Main {

	
	
	public static void main(String[] args) {
		String s[] = args != null ? Arrays.copyOf(args, args.length) : null;
		CommandLine cmdLine = null;
		try {
			cmdLine = new CommandLine(args != null ? Arrays.copyOf(args, args.length) : null);
		} catch (final Exception e) {
			System.err.println("Failed due to invalid parameters: " + Arrays.toString(args));
			if (e instanceof ArrayIndexOutOfBoundsException){
				int i = (Integer.parseInt(e.getMessage()))-1;
				System.err.println(Objects.requireNonNull(s)[i] + " need one value");
			}
			System.err.println("Use '-h' for more details.");
			System.exit(1);
		}
		Bootstrap demo = new Bootstrap(cmdLine);
		try {
			demo.start();
		} catch (IOException e){
			PrintUtils.werrPrintln(e.getMessage());
		}
	}
}

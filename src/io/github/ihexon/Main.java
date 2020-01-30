package io.github.ihexon;

import io.github.ihexon.common.DebugUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class Main {


	public static void main(String[] args) {
		CommandLine cmdLine = null;
		try {
			cmdLine = new CommandLine(args != null ? Arrays.copyOf(args, args.length) : null);
		} catch (final Exception e) {
			System.out.println("Failed due to invalid parameters: " + Arrays.toString(args));
			e.printStackTrace();
			System.out.println("Use '-h' for more details.");
			System.exit(1);
		}
		Bootstrap demo = new Bootstrap(cmdLine);
		try {
			demo.start();
		} catch (IOException e){
			DebugUtils.werrPrintln(e.getMessage());
		}

	}
}

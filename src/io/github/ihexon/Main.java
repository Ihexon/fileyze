package io.github.ihexon;

import io.github.ihexon.common.PrintUtils;

import java.io.IOException;
import java.util.Arrays;

public class Main {


	public static void main(String[] args) {
		CommandLine cmdLine = null;
		/* My dear zzh, you should know what it does :D Love you mua*/
//		String[] test = {"-d", "/tmp/123", "--log", "--custom-log-file", "/tmp/log.txt"};
		try {
			cmdLine = new CommandLine(args != null ? Arrays.copyOf(args, args.length) : null);
//			cmdLine = new CommandLine(test);
		} catch (final Exception e) {
			System.out.println("Failed due to invalid parameters: " + Arrays.toString(args));
			System.out.println("Use '-h' for more details.");
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

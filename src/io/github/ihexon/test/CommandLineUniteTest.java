package io.github.ihexon.test;

import io.github.ihexon.commandline.CommandLine;

import java.lang.reflect.Array;
import java.util.Arrays;

public class CommandLineUniteTest {


	public static void main(String[] args) {
		CommandLine commandLine = null;
		try{
			commandLine = new CommandLine(args != null ? Arrays.copyOf(args, args.length) : null);
		}catch (final Exception e){
			System.err.println("Failed due to invalid parameters: "+ Arrays.toString(args));
			System.err.println(e.getMessage());
			System.out.println("Use '-h' for more details.");
			System.exit(1);
		}
	}
}

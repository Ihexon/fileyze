package io.github.ihexon;

import io.github.ihexon.commandline.CommandLine;

import java.util.Arrays;

public class watchme {


	public enum ProcessType {
		cmdline,
		daemon, // did not implement
		gui, // did not implement
		web // did not implement
	}

	private static ProcessType processType;


	private static Bootstrap createWatchMeBootstrap(CommandLine cmdLineArgs) {
		Bootstrap bootstrap;
		if (cmdLineArgs.isGUI()) {
			watchme.processType = ProcessType.gui;
			bootstrap = null;
			// we don't have gui yet .
		} else if (cmdLineArgs.isDaemon()) {
			// we don't have daemon yet .
			watchme.processType = ProcessType.daemon;
			bootstrap = null;
		} else if (cmdLineArgs.isWeb){
			// we don't have web client yet .
			watchme.processType = ProcessType.web;
			bootstrap = null;
		}else {
			watchme.processType = ProcessType.cmdline;
			bootstrap = new CommandLineBootstrap(cmdLineArgs);
		}
		return bootstrap;
	}

	public static void main(String[] args) {
		CommandLine cmdLine = null;
		try {
			cmdLine = new CommandLine(args != null ? Arrays.copyOf(args, args.length) : null);
		} catch (final Exception e) {
			System.out.println("Failed due to invalid parameters: " + Arrays.toString(args));
			System.out.println(e.getMessage());
			System.out.println("Use '-h' or '--help' for more details.");
			System.exit(1);
		}
		Bootstrap bootstrap = createWatchMeBootstrap(cmdLine);

	}
}

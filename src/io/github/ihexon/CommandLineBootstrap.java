package io.github.ihexon;

import io.github.ihexon.commandline.CommandLine;

import java.awt.*;


public class CommandLineBootstrap extends Bootstrap {

	/**
	 * The bootstrap process for command line mode.
	 */
	public CommandLineBootstrap(CommandLine cmdLineArgs) {
		super(cmdLineArgs);
	}

	@Override
	public int start() {
		int rc = super.start();
		if (rc != 0) {
			return rc;
		}
		return 0;
	}



}

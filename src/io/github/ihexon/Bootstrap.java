package io.github.ihexon;


import io.github.ihexon.commandline.CommandLine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

abstract class Bootstrap {
	private final CommandLine args;
	public Bootstrap(CommandLine args) {
		this.args = args;
		// Do nothing in Bootstrap construct
	}

	/**
	 * Starts the bootstrap process.
	 */
	public int start() {
		try {
			Constant.getInstance();
		} catch (final Throwable e) {
			System.err.println(e.getMessage());
			return 1;
		}
		return 0;
	}

	protected CommandLine getArgs() {
		return args;
	}

	protected static String getStartingMessage() {
		DateFormat dateFormat =
				SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
		StringBuilder strBuilder = new StringBuilder(200);
		strBuilder.append(Constant.PROGRAM_NAME).append(' ').append(Constant.PROGRAM_VERSION);
		strBuilder.append(" started ");
		strBuilder.append(dateFormat.format(new Date()));
		strBuilder.append(" with configure home ").append(Constant.getConfigHome());
		return strBuilder.toString();
	}
}

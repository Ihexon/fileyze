package io.github.ihexon;

import java.io.IOException;

abstract class AbstractBootstrap {
	private final CommandLine args;
	private final ControlOverrides controlOverrides;

	protected AbstractBootstrap(CommandLine cmdline) {
		this.args = cmdline;
		controlOverrides = new ControlOverrides();
		controlOverrides.setPath(getArgs().getArgument("-d"));
		controlOverrides.setRecurse(getArgs().getArgument("-r"));
		controlOverrides.setExcludeHidden(getArgs().getArgument("--exclude-hidden"));
		controlOverrides.setShowHelp(getArgs().getArgument("--help"));
		controlOverrides.setShowHelp(getArgs().getArgument("-h"));
		controlOverrides.setLogFile(getArgs().getArgument("--log"));
	}

	protected ControlOverrides getControlOverrides() {
		return controlOverrides;
	}

	public int start() throws IOException {
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
}

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
	}

	protected ControlOverrides getControlOverrides() {
		return controlOverrides;
	}

	public void start() throws IOException {
	}

	protected CommandLine getArgs() {
		return args;
	}
}

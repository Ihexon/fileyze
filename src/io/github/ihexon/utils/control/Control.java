package io.github.ihexon.utils.control;

import io.github.ihexon.ControlOverrides;
import io.github.ihexon.common.PrintUtils;

import java.nio.file.Path;

public class Control {
	private static Control control = null;
	public boolean recursive = false;
	public Path dir = null;
	public boolean isDebug = true;
	public boolean excludeHidden = false;
	public boolean showHelp = false;
	public Path logFile = null;

	public static Control getSingleton() {
		return control;
	}

	public static void initSingleton(ControlOverrides overrides) {
		control = new Control();
		control.init(overrides);
	}

	private void init(ControlOverrides overrides) {
		this.dir = overrides.getPath();
		this.recursive = overrides.getRecurse();
		this.excludeHidden = overrides.getExcludeHidden();
		this.showHelp = overrides.getShowHelp();
		this.logFile = overrides.getLogFile();
	}


}

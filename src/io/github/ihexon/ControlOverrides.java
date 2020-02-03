package io.github.ihexon;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ControlOverrides {
	private Path path = null;
	private String customLogFile = null;
	private boolean recurse = false;
	private boolean excludeHidden = false;
	private boolean showHelp = true;
	private boolean showVersion = true;
	private boolean logFile = false;

	public ControlOverrides() {
	}

	public void setPath(String s) {
		if (s != null) {
			this.showHelp = false;
			this.path = Paths.get(s);
		}
	}

	public void setRecurse(String s) {
		if (s != null && s.equalsIgnoreCase("true"))
			this.recurse = true;
	}

	public void setExcludeHidden(String s) {
		if (s != null && s.equalsIgnoreCase("true"))
			this.excludeHidden = true;
	}

	public void setShowHelp(String s) {
		if (s != null && s.equalsIgnoreCase("true"))
			this.showHelp = true;
	}

	public void setLogFile(String s) {
		if (s != null && s.equalsIgnoreCase("true"))
			this.logFile = true;
	}

	public boolean getLogFile() {
		return this.logFile;
	}

	public void setCustomLogFile(String s){
		if (s != null){
			this.showHelp = false;
			this.customLogFile = s;
		}
	}

	public String getCustomLogFile(){
		return this.customLogFile;
	}

	public boolean getShowHelp() {
		return this.showHelp;
	}

	public boolean getRecurse() {
		return this.recurse;
	}

	public Path getPath() {
		return path;
	}

	public boolean getExcludeHidden() {
		return excludeHidden;
	}
}

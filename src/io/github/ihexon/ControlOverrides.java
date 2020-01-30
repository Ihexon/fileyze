package io.github.ihexon;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ControlOverrides {
	private Path path = null;
	private boolean recurse = false;
	private boolean excludeHidden = false;

	public ControlOverrides(){
	}

	public void setPath(String s) {
		if (s != null) this.path = Paths.get(s);
	}

	public void setRecurse(String s) {
		if (s != null&&s.equalsIgnoreCase("true"))
		this.recurse = true;
	}

	public void setExcludeHidden(String s){
		if (s != null&&s.equalsIgnoreCase("true"))
			this.excludeHidden = true;
	}

	public boolean getRecurse(){
		return recurse;
	}

	public Path getPath(){
		return path;
	}

	public boolean getExcludeHidden() {
		return excludeHidden;
	}
}

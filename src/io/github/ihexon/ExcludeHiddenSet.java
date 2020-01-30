package io.github.ihexon;

import io.github.ihexon.common.DebugUtils;
import io.github.ihexon.utils.control.Control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class ExcludeHiddenSet implements Predicate<Path> {

	public boolean isHidden(Path path) {
		try {
			if (!path.startsWith(Control.getSingleton().dir))
				return true;
			for (int i = Control.getSingleton().dir.getNameCount();
			     i < path.getNameCount();
			     i++) {
				if (path.getName(i).toString().startsWith(".")) {
					return true;
				}
			}
			return Files.exists(path) && Files.isHidden(path);
		} catch (IOException e) {
			DebugUtils.werrPrintln(e);
			return false;
		}
	}

	@Override
	public boolean test(Path path) {
		if (Control.getSingleton().excludeHidden && isHidden(path)){
			if (Control.getSingleton().isDebug)
				DebugUtils.stdPrintln("test({"+(path.toString())+"}) -> [Hidden]");
			return true;
		}
		return false;
	}
}

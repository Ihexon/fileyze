package io.github.ihexon.matcher;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashSet;
import java.util.function.Predicate;

public class PathMatcherSet extends HashSet<PathMatcher> implements Predicate<Path> {

	@Override
	public boolean test(Path path) {
		for (PathMatcher pm : this){
			if (pm.matches(path)) return true;
		}
		return false;
	}
}

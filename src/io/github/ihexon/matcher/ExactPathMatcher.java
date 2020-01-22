package io.github.ihexon.matcher;

import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class ExactPathMatcher implements PathMatcher {

	private final Path path;
	public ExactPathMatcher(Path path) {
		this.path = path;
	}
	@Override
	public boolean matches(Path path) {
		return this.path.equals(path);
	}
}

package io.github.ihexon;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Predicate;

import io.github.ihexon.matcher.ExactPathMatcher;
import io.github.ihexon.types.DirAction;
import io.github.ihexon.utils.PathWatcher;

public class Config  implements Predicate<Path> {
	private static final String PATTERN_SEP;
	public static final int UNLIMITED_DEPTH = -9999;
	public final Path path;
	protected int recurseDepth = 0; // 0 means no sub-directories are scanned
	protected boolean excludeHidden = false;


	protected final IncludeExcludeSet<PathMatcher, Path> includeExclude;

	protected final Config parent;

	static
	{
		String sep = File.separator;
		if (File.separatorChar == '\\')
		{
			sep = "\\\\";
		}
		PATTERN_SEP = sep;
	}



	public Config(Path path)
	{
		this(path, null);
	}

	public Config(Path path, Config parent) {
		this.parent = parent;

		this.includeExclude = parent == null ? new IncludeExcludeSet<>(OptionsParam.PathMatcherSet.class) : parent.includeExclude;

		Path dir = path;
		if (! Files.exists(path)) throw new IllegalStateException("Path does not exist: " + path);
		if (!Files.isDirectory(path)) {
			dir = path.getParent();
			includeExclude.include(new ExactPathMatcher(path));
			setRecurseDepth(0);
		}
		this.path = dir;
	}

	public Config getParent()
	{
		return parent;
	}

	public void addExclude(PathMatcher matcher)
	{
		includeExclude.exclude(matcher);
	}

	public void addExclude(final String syntaxAndPattern)
	{
		addExclude(path.getFileSystem().getPathMatcher(syntaxAndPattern));
	}

	public void addExcludeHidden() {
		if (!excludeHidden) {
			excludeHidden = true;
		}
	}

	public void addExcludes(List<String> syntaxAndPatterns)
	{
		for (String syntaxAndPattern : syntaxAndPatterns)
		{
			addExclude(syntaxAndPattern);
		}
	}

	public void addInclude(PathMatcher matcher) {
		includeExclude.include(matcher);
	}

	public void addInclude(String syntaxAndPattern) {
		addInclude(path.getFileSystem().getPathMatcher(syntaxAndPattern));
	}

	public void addIncludeGlobRelative(String pattern) {
		addInclude(toGlobPattern(path, pattern));
	}

	public void addIncludes(List<String> syntaxAndPatterns) {
		for (String syntaxAndPattern : syntaxAndPatterns) {
			addInclude(syntaxAndPattern);
		}
	}

	public Config asSubConfig(Path dir) {
		Config subconfig = new Config(dir, this);
		if (dir == this.path) throw new IllegalStateException("sub " + dir.toString() + " of " + this);
		if (this.recurseDepth == UNLIMITED_DEPTH)
			subconfig.recurseDepth = UNLIMITED_DEPTH;
		else
			subconfig.recurseDepth = this.recurseDepth - (dir.getNameCount() - this.path.getNameCount());
		return subconfig;
	}

	public int getRecurseDepth() {
		return recurseDepth;
	}

	public boolean isRecurseDepthUnlimited() {
		return this.recurseDepth == UNLIMITED_DEPTH;
	}

	public Path getPath() {
		return this.path;
	}

	public Path resolve(Path path) {
		if (Files.isDirectory(this.path))
			return this.path.resolve(path);
		if (Files.exists(this.path))
			return this.path;
		return path;
	}


	/**
	 * test the file is hidden and ignore by set excludeHidden to true ,
	 * and the depth lager the recurseDepth ,
	 * and if include in exclude List.
	 * @param path
	 * @return
	 */
	@Override
	public boolean test(Path path) {

		// check PatchWatcher's configure is ignore hidden file by
		// set excludeHidden to true
		if (excludeHidden && isHidden(path)) {
			return false;
		}
		// check if the file in the monitor dir
		if (!path.startsWith(this.path)) {
			return false;
		}

		// check if the depth > recurseDepth
		if (recurseDepth != UNLIMITED_DEPTH) {
			int depth = path.getNameCount() - this.path.getNameCount() - 1;
			if (depth > recurseDepth) {
				return false;
			}
		}

		boolean matched = includeExclude.test(path);
		return matched;
	}
	public void setRecurseDepth(int depth)
	{
		this.recurseDepth = depth;
	}

	public void setPauseUntil(long time) {
		if (time > pauseUntil)
			pauseUntil = time;
	}

	private String toGlobPattern(Path path, String subPattern){
		StringBuilder s = new StringBuilder();
		s.append("glob:");
		boolean needDelim = false;
		Path root = path.getRoot();
		if (root != null){
			for (char c : root.toString().toCharArray()){
				if (c == '\\'){
					s.append(PATTERN_SEP);
				}else {
					s.append(c);
				}
			}
		}else {
			needDelim = true;
		}


		for (Path segment : path){
			if (needDelim) {
				s.append(PATTERN_SEP);
			}
			s.append(segment);
			needDelim = true;
		}

		if ((subPattern != null) && (subPattern.length() > 0)) {
			if (needDelim) {
				s.append(PATTERN_SEP);
			}
			for (char c : subPattern.toCharArray()) {
				if (c == '/') {
					s.append(PATTERN_SEP);
				} else {
					s.append(c);
				}
			}
		}
		return s.toString();
	}

	public DirAction handleDir(Path path){
		try{
			if (!Files.isDirectory(path)) return DirAction.IGNORE;
			if (excludeHidden && isHidden(path)) return DirAction.IGNORE;
			if (getRecurseDepth() == 0)  return DirAction.WATCH;
			return DirAction.ENTER;
		}catch (Exception e){
			return DirAction.IGNORE;
		}
	}


	public boolean isHidden(Path path){
		try{
			if (!path.startsWith(this.path)) return true;
			for (int i = this.path.getNameCount(); i < path.getNameCount(); i++){
				if (path.getName(i).toString().startsWith(".")){
					return true;
				}
			}
			return Files.exists(path) && Files.isHidden(path);
		}catch (IOException e){
			return false;
		}
	}

	public String toShortPath(Path path) {
		if (!path.startsWith(this.path))
			return path.toString();
		return this.path.relativize(path).toString();
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append(path).append(" [depth=");
		if (recurseDepth == UNLIMITED_DEPTH)
			s.append("UNLIMITED");
		else
			s.append(recurseDepth);
		s.append(']');
		return s.toString();
	}


	private static class ExactPathMatcher implements PathMatcher
	{
		private final Path path;

		ExactPathMatcher(Path path)
		{
			this.path = path;
		}

		@Override
		public boolean matches(Path path)
		{
			return this.path.equals(path);
		}
	}

	protected long pauseUntil = 0;

	public boolean isPaused(long now)
	{
		if (pauseUntil == 0)
			return false;
		if (pauseUntil > now)
		{
			return true;
		}
		pauseUntil = 0;
		return false;
	}





}

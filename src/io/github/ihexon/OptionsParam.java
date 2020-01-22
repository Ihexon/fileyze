package io.github.ihexon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class OptionsParam implements Predicate<Path> {

	public enum DirAction
	{
		IGNORE, WATCH, ENTER
	}

	public static final int UNLIMITED_DEPTH = -999;
	private static final String PATTERN_SEP;

	static
	{
		String sep = File.separator;
		if (File.separatorChar == '\\')
		{
			sep = "\\\\";
		}
		PATTERN_SEP = sep;
	}

	protected final OptionsParam parent;
	protected final Path path;
	protected final IncludeExcludeSet<PathMatcher, Path> includeExclude;
	protected int recurseDepth = 0; // 0 means no sub-directories are scanned
	protected boolean excludeHidden = false;
	protected long pauseUntil;

	public OptionsParam(Path path)
	{
		this(path, null);
	}
	public OptionsParam(Path path, OptionsParam parent)
	{
		this.parent = parent;
		this.includeExclude = parent == null ? new IncludeExcludeSet<>(PathMatcherSet.class) : parent.includeExclude;

		Path dir = path;
		if (! Files.exists(path))
			throw new IllegalStateException("Path does not exist: " + path);

		if (!Files.isDirectory(path))
		{
			dir = path.getParent();
			includeExclude.include(new ExactPathMatcher(path));
			setRecurseDepth(0);
		}

		this.path = dir;
	}
	public OptionsParam getParent()
	{
		return parent;
	}

	public boolean isPaused(long now)
	{
		if (pauseUntil == 0) {
			return false;
		}
		if (pauseUntil > now) {
			return true;
		}
		pauseUntil = 0;
		return false;
	}

	/**
	 * Add an exclude PathMatcher
	 *
	 * @param matcher the path matcher for this exclude
	 */
	public void addExclude(PathMatcher matcher)
	{
		includeExclude.exclude(matcher);
	}

	/**
	 * Add an exclude PathMatcher.
	 * <p>
	 * Note: this pattern is FileSystem specific (so use "/" for Linux and OSX, and "\\" for Windows)
	 *
	 * @param syntaxAndPattern the PathMatcher syntax and pattern to use
	 */
	public void addExclude(final String syntaxAndPattern) {
		addExclude(path.getFileSystem().getPathMatcher(syntaxAndPattern));
	}

	public void setPauseUntil(long time)
	{
		if (time > pauseUntil)
			pauseUntil = time;
	}

	/**
	 * Add a <code>glob:</code> syntax pattern exclude reference in a directory relative, os neutral, pattern.
	 *
	 * <pre>
	 *    On Linux:
	 *    Config config = new Config(Path("/home/user/example"));
	 *    config.addExcludeGlobRelative("*.war") =&gt; "glob:/home/user/example/*.war"
	 *
	 *    On Windows
	 *    Config config = new Config(Path("D:/code/examples"));
	 *    config.addExcludeGlobRelative("*.war") =&gt; "glob:D:\\code\\examples\\*.war"
	 *
	 * </pre>
	 *
	 * @param pattern the pattern, in unixy format, relative to config.dir
	 */
	public void addExcludeGlobRelative(String pattern)
	{
		addExclude(toGlobPattern(path, pattern));
	}

	public void addExcludeHidden()
	{
		if (!excludeHidden)
		{
			excludeHidden = true;
		}
	}

	/**
	 * Add multiple exclude PathMatchers
	 *
	 * @param syntaxAndPatterns the list of PathMatcher syntax and patterns to use
	 */
	public void addExcludes(List<String> syntaxAndPatterns)
	{
		for (String syntaxAndPattern : syntaxAndPatterns)
		{
			addExclude(syntaxAndPattern);
		}
	}

	/**
	 * Add an include PathMatcher
	 *
	 * @param matcher the path matcher for this include
	 */
	public void addInclude(PathMatcher matcher)
	{
		includeExclude.include(matcher);
	}

	/**
	 * Add an include PathMatcher
	 *
	 * @param syntaxAndPattern the PathMatcher syntax and pattern to use
	 */
	public void addInclude(String syntaxAndPattern)
	{
		addInclude(path.getFileSystem().getPathMatcher(syntaxAndPattern));
	}


	public void addIncludeGlobRelative(String pattern)
	{
		addInclude(toGlobPattern(path, pattern));
	}

	/**
	 * Add multiple include PathMatchers
	 *
	 * @param syntaxAndPatterns the list of PathMatcher syntax and patterns to use
	 */
	public void addIncludes(List<String> syntaxAndPatterns)
	{
		for (String syntaxAndPattern : syntaxAndPatterns)
		{
			addInclude(syntaxAndPattern);
		}
	}

	/**
	 * Build a new config from a this configuration.
	 * <p>
	 * Useful for working with sub-directories that also need to be watched.
	 *
	 * @param dir the directory to build new Config from (using this config as source of includes/excludes)
	 * @return the new Config
	 */
	public OptionsParam asSubConfig(Path dir)
	{
		OptionsParam subconfig = new OptionsParam(dir, this);
		if (dir == this.path)
			throw new IllegalStateException("sub " + dir.toString() + " of " + this);

		if (this.recurseDepth == UNLIMITED_DEPTH)
			subconfig.recurseDepth = UNLIMITED_DEPTH;
		else
			subconfig.recurseDepth = this.recurseDepth - (dir.getNameCount() - this.path.getNameCount());

		return subconfig;
	}


	public boolean isRecurseDepthUnlimited()
	{
		return this.recurseDepth == UNLIMITED_DEPTH;
	}

	public Path getPath()
	{
		return this.path;
	}

	public Path resolve(Path path)
	{
		if (Files.isDirectory(this.path))
			return this.path.resolve(path);
		if (Files.exists(this.path))
			return this.path;
		return path;
	}


	public boolean isHidden(Path path)
	{
		try
		{
			if (!path.startsWith(this.path))
				return true;
			for (int i = this.path.getNameCount(); i < path.getNameCount(); i++)
			{
				if (path.getName(i).toString().startsWith("."))
				{
					return true;
				}
			}
			return Files.exists(path) && Files.isHidden(path);
		}
		catch (IOException e)
		{
			return false;
		}
	}

	@Override
	public boolean test(Path path)
	{
		if (excludeHidden && isHidden(path))
		{
			return false;
		}

		if (!path.startsWith(this.path))
		{
			return false;
		}

		if (recurseDepth != UNLIMITED_DEPTH)
		{
			int depth = path.getNameCount() - this.path.getNameCount() - 1;

			if (depth > recurseDepth)
			{
				return false;
			}
		}

		boolean matched = includeExclude.test(path);

		return matched;
	}

	public String toShortPath(Path path)
	{
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



	private String toGlobPattern(Path path, String subPattern)
	{
		StringBuilder s = new StringBuilder();
		s.append("glob:");

		boolean needDelim = false;

		// Add root (aka "C:\" for Windows)
		Path root = path.getRoot();
		if (root != null)
		{
			for (char c : root.toString().toCharArray())
			{
				if (c == '\\')
				{
					s.append(PATTERN_SEP);
				}
				else
				{
					s.append(c);
				}
			}
		}
		else
		{
			needDelim = true;
		}

		// Add the individual path segments
		for (Path segment : path)
		{
			if (needDelim)
			{
				s.append(PATTERN_SEP);
			}
			s.append(segment);
			needDelim = true;
		}

		// Add the sub pattern (if specified)
		if ((subPattern != null) && (subPattern.length() > 0))
		{
			if (needDelim)
			{
				s.append(PATTERN_SEP);
			}
			for (char c : subPattern.toCharArray())
			{
				if (c == '/')
				{
					s.append(PATTERN_SEP);
				}
				else
				{
					s.append(c);
				}
			}
		}

		return s.toString();
	}


	/**
	 * Set the recurse depth for the directory scanning.
	 * <p>
	 * -999 indicates arbitrarily deep recursion, 0 indicates no recursion, 1 is only one directory deep, and so on.
	 *
	 * @param depth the number of directories deep to recurse
	 */
	public void setRecurseDepth(int depth)
	{
		this.recurseDepth = depth;
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

	public static class PathMatcherSet extends HashSet<PathMatcher> implements Predicate<Path>
	{
		@Override
		public boolean test(Path path)
		{
			for (PathMatcher pm : this)
			{
				if (pm.matches(path))
					return true;
			}
			return false;
		}
	}

}

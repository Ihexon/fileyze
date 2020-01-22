package io.github.ihexon.utils;

import io.github.ihexon.Config;
import io.github.ihexon.Loder.AbstractLifeCycle;
import io.github.ihexon.common.DebugUtils;
import io.github.ihexon.event.PathWatchEvent;
import io.github.ihexon.other.MultiException;
import io.github.ihexon.types.PathWatchEventType;
import io.github.ihexon.utils.control.Control;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.*;

public class PathWatcher extends AbstractLifeCycle implements Runnable {
	private WatchService watchService;


	private final Map<WatchKey, Config> keys = new ConcurrentHashMap<>();

	public boolean isDebugEnabled = true;
	private boolean nativeWatchService = true;
	private final Map<Path, PathWatchEvent> pending = new LinkedHashMap<>(32, (float)0.75, false);

	private final List<PathWatchEvent> events = new ArrayList<>();
	private WatchEvent.Modifier[] watchModifiers;
	private boolean _notifyExistingOnStart = true;
	public PathWatcher pathWatcher=null;
	private Thread thread;
	private TimeUnit updateQuietTimeUnit;
	private long updateQuietTimeDuration;
	private final List<Config> configs = new ArrayList<>();
	private static final WatchEvent.Kind<?>[] WATCH_EVENT_KINDS = {ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY};
	private static final WatchEvent.Kind<?>[] WATCH_DIR_KINDS = {ENTRY_CREATE, ENTRY_DELETE};


	public PathWatcher() {
	}






	@Override
	public void run() {
		long waitTime = Control.getSingleton().getUpdateQuietTimeMillis();
		WatchService watch = watchService;
		while (isRunning() && thread == Thread.currentThread()) {
			WatchKey key;
			try {
				// Reset all keys before watching
				long now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

				for (Map.Entry<WatchKey, Config> e : keys.entrySet()) {
					WatchKey k = e.getKey();
					Config c = e.getValue();

					if (! k.reset()) {
						keys.remove(k);
						if (keys.isEmpty()) return;
					}
				}

				key = waitTime < 0 ? watch.take() : waitTime > 0 ? watch.poll(waitTime, updateQuietTimeUnit) : watch.poll();

				// handle all active keys
				while (key != null) {
					handleKey(key);
					key = watch.poll();
				}

				waitTime = processPending();

				notifyEvents();

			} catch (ClosedWatchServiceException e) {
				return;
			} catch (InterruptedException e) {
				if (isRunning()) System.err.println(e.getMessage());
			}
		}
	}

	private long processPending(){
		if (isDebugEnabled)
			DebugUtils.werrPrintln("processPending> {"+pending.values()+"}");
		long now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
		long wait = Long.MAX_VALUE;
		for (PathWatchEvent event : new ArrayList<>(pending.values())){
			Path path = event.getPath();
			if (pending.containsKey(path.getParent()))
				continue;

			if (event.isQuiet(now, Control.getSingleton().getUpdateQuietTimeMillis())) {
				pending.remove(path);
				events.add(event);
			}else {
				long msToCheck = event.toQuietCheck(now, Control.getSingleton().getUpdateQuietTimeMillis());
				if (msToCheck < wait)
					wait = msToCheck;
			}
		}
		return wait == Long.MAX_VALUE ? -1 : wait;
	}

	@SuppressWarnings("unchecked")
	protected static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	private void registerDir(Path path, Config config) throws IOException
	{

		DebugUtils.werrPrintln("registerDir {"+path+"} {"+config+"}");

		if (!Files.isDirectory(path))
			throw new IllegalArgumentException(path.toString());

		register(path, config.asSubConfig(path), WATCH_DIR_KINDS);
	}

	private void register(Path path, Config config, WatchEvent.Kind<?>[] kinds) throws IOException
	{
		if (watchModifiers != null) {
			// Java Watcher
			WatchKey key = path.register(watchService, kinds, watchModifiers);
			keys.put(key, config);
		}
		else {
			// Native Watcher
			WatchKey key = path.register(watchService, kinds);
			keys.put(key, config);
		}
	}
	protected void register(Path path, Config config) throws IOException
	{

		DebugUtils.werrPrintln("Registering watch on {"+path+"} {"+watchModifiers == null ? null : Arrays.asList(watchModifiers)+"}");

		register(path, config, WATCH_EVENT_KINDS);
	}

	private void registerTree(Path dir, Config config, boolean notify) throws IOException
	{
		DebugUtils.werrPrintln("registerTree {"+dir+"}{"+config+"}{"+notify+"}");

		if (!Files.isDirectory(dir))
			throw new IllegalArgumentException(dir.toString());

		register(dir, config);

		final MultiException me = new MultiException();
		try (Stream<Path> stream = Files.list(dir))
		{
			stream.forEach(p ->
			{
				DebugUtils.werrPrintln("registerTree? {"+p+"}");
				try
				{
					if (notify && config.test(p))
						pending.put(p, new PathWatchEvent(p, PathWatchEventType.ADDED, config));

					switch (config.handleDir(p))
					{
						case ENTER:
							registerTree(p, config.asSubConfig(p), notify);
							break;
						case WATCH:
							registerDir(p, config);
							break;
						case IGNORE:
						default:
							break;
					}
				}
				catch (IOException e)
				{
					me.add(e);
				}
			});
		}
		try
		{
			me.ifExceptionThrow();
		}
		catch (IOException e)
		{
			throw e;
		}
		catch (Throwable ex)
		{
			throw new IOException(ex);
		}
	}

	private void handleKey(WatchKey key) {
		Config config = keys.get(key);

		if (config == null) return;

		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent<Path> ev = cast(event);
			Path name = ev.context(); // file name which get events
			Path path = config.resolve(name); // the full path of the file

			if (isDebugEnabled) {
				System.out.println("handleKey" + " " + ev.kind() + " " + config.toShortPath(path) + " " + config);
			}
			// !!! Ignore modified events on directories.  These are handled as create/delete events of their contents
			if (ev.kind() == ENTRY_MODIFY && Files.exists(path) && Files.isDirectory(path)) continue;
			if (config.test(path)) {
				handleWatchEvent(path, new PathWatchEvent(path, ev, config));
			} else if (config.getRecurseDepth() == - 1) {
				System.err.println("Get recurse depth fail ! " +
						"mybe you point a file not a dir ! " +
						"or make sure the dir has premission to open");
			}
			if (ev.kind() == ENTRY_CREATE) {
				try {
					switch (config.handleDir(path)) {
						case ENTER:
							registerTree(path, config.asSubConfig(path), true);
							break;
						case WATCH:
							registerDir(path, config);
							break;
						case IGNORE:
						default:
							break;
					}
				} catch (IOException e) {
					DebugUtils.werrPrintln(e.getMessage());
				}
			}
		}
	}

	/**
	 * Add an event reported by the WatchService to list of pending events
	 * that will be sent after their quiet time has expired.
	 *
	 * @param path  the path to add to the pending list
	 * @param event the pending event
	 */
	private void handleWatchEvent(Path path, PathWatchEvent event) {
		PathWatchEvent existing = pending.get(path);

		if (isDebugEnabled) {
			System.out.println("handleWatchEvent {" + path + "} {" + event + "} <= {" + existing + "}");
		}

		switch (event.getType()) {
			case ADDED:
				if (existing != null && existing.getType() == PathWatchEventType.MODIFIED)
					events.add(new PathWatchEvent(path, PathWatchEventType.DELETED, existing.getConfig()));
				pending.put(path, event);
				break;
			case MODIFIED:
				pending.put(path, event);
				break;
			case DELETED:
				if (existing != null)
					pending.remove(path);
				events.add(event);
				break;
			case UNKNOWN:
				DebugUtils.werrPrintln(path.toAbsolutePath() + " " + event.getType());
				break;
		}
	}


	private void notifyEvents() {
		if (isDebugEnabled)
			DebugUtils.werrPrintln("notifyEvents " + events.size());
	}

	public void setNotifyExistingOnStart(boolean notify) {
		_notifyExistingOnStart = notify;
	}




	public void setUpdateQuietTime(long duration, TimeUnit unit) {
		long desiredMillis = unit.toMillis(duration);

		if (watchService != null && ! this.nativeWatchService && (desiredMillis < 5000)) {
			DebugUtils.werrPrintln("Quiet Time is too low for non-native WatchService [" + watchService.getClass().getName() + "]:" + desiredMillis + " < 5000 ms (defaulting to 5000 ms)");
			this.updateQuietTimeDuration = 5000;
			this.updateQuietTimeUnit = TimeUnit.MILLISECONDS;
			return;
		}

		boolean IS_WINDOWS = Control.getSingleton().getIS_WINDOWS();
		if ( IS_WINDOWS && (desiredMillis < 1000)) {
			DebugUtils.werrPrintln("Quiet Time is too low for Microsoft Windows: {"+desiredMillis+"} < 1000 ms (defaulting to 1000 ms)");
			this.updateQuietTimeDuration = 1000;
			this.updateQuietTimeUnit = TimeUnit.MILLISECONDS;
			return;
		}

		this.updateQuietTimeDuration = duration;
		this.updateQuietTimeUnit = unit;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(this.getClass().getName());
		appendConfigId(s);
		return s.toString();
	}

	private void appendConfigId(StringBuilder s) {
		List<Path> dirs = new ArrayList<>();
		for (Config config : keys.values()) {
			dirs.add(config.path);
		}
		Collections.sort(dirs);
		s.append("[");
		if (dirs.size() > 0) {
			s.append(dirs.get(0));
			if (dirs.size() > 1) {
				s.append(" (+").append(dirs.size() - 1).append(")");
			}
		} else {
			s.append("<null>");
		}
		s.append("]");
	}


	public boolean isNotifyExistingOnStart()
	{
		return _notifyExistingOnStart;
	}


	@Override
	protected void doStart() throws Exception {
		super.doStart();
		createWatchService();
		setUpdateQuietTime(Control.getSingleton().getUpdateQuietTimeMillis(), TimeUnit.MILLISECONDS);
		for (Config c : configs)
		{
			registerTree(c.getPath(), c, isNotifyExistingOnStart());
		}

		StringBuilder threadId = new StringBuilder();
		threadId.append("PathWatcher@");
		threadId.append(Integer.toHexString(hashCode()));
		if (isDebugEnabled) DebugUtils.werrPrintln("{"+this+"}"+"->"+"{"+threadId+"}");
		thread = new Thread(this, threadId.toString());
		thread.setDaemon(true);
		thread.start();

	}

	private void createWatchService() throws IOException
	{
		//create a watch service
		this.watchService = FileSystems.getDefault().newWatchService();

		WatchEvent.Modifier[] modifiers = null;
		boolean nativeService = true;
		// Try to determine native behavior
		// See http://stackoverflow.com/questions/9588737/is-java-7-watchservice-slow-for-anyone-else
		try
		{
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Class<?> pollingWatchServiceClass = Class.forName("sun.nio.fs.PollingWatchService", false, cl);
			if (pollingWatchServiceClass.isAssignableFrom(this.watchService.getClass()))
			{
				nativeService = false;
				DebugUtils.werrPrintln("Using Non-Native Java {"+pollingWatchServiceClass.getName()+"}");
				Class<?> c = Class.forName("com.sun.nio.file.SensitivityWatchEventModifier");
				Field f = c.getField("HIGH");
				modifiers = new WatchEvent.Modifier[]
						{
								(WatchEvent.Modifier)f.get(c)
						};
			}
		}
		catch (Throwable t)
		{
			// Unknown JVM environment, assuming native.
			DebugUtils.werrPrintln(t);
		}

		this.watchModifiers = modifiers;
		this.nativeWatchService = nativeService;
	}

	@Override
	protected void doStop() throws Exception
	{
		if (watchService != null)
			watchService.close(); //will invalidate registered watch keys, interrupt thread in take or poll
		watchService = null;
		thread = null;
		keys.clear();
		pending.clear();
		events.clear();
		super.doStop();
	}
}

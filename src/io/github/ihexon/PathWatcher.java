package io.github.ihexon;

import io.github.ihexon.common.PrintUtils;
import io.github.ihexon.listener.EventListListener;
import io.github.ihexon.listener.EventListenerImpl;
import io.github.ihexon.utils.control.Control;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;


public class PathWatcher {
	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final boolean recursive;
	private boolean trace = false;
	String LOG_FILE = null;
	private static final WatchEvent.Kind<?>[] WATCH_EVENT_KINDS = {ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY};


	public PathWatcher() throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<>();
		this.recursive = Control.getSingleton().recursive;
		Path path = Control.getSingleton().dir;
		if (recursive) {
			System.out.format("Scanning %s ...\n", path);
			registerAll(path);
		} else {
			System.out.format("Add monitoring dir %s ...\n", path);
			register(path);
		}
		System.out.println("Done.");
		this.trace = true;
	}

	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (Control.getSingleton().excludeHidden) {
					if (! new ExcludeHiddenSet().test(dir)) {
						register(dir);
					}
				} else {
					register(dir);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private void register(Path dir) throws IOException {
		if (dir == null){
			PrintUtils.werrPrintln("Please point a directory to be monitor, eg -d /home/test/die1");
			System.exit(1);
		}
		WatchKey key = dir.register(watcher, WATCH_EVENT_KINDS);
		if (trace) {
			Path prev = keys.get(key);
			if (prev == null) {
				System.out.format("register: %s\n", dir);
			} else {
				if (! dir.equals(prev)) {
					System.out.format("update: %s -> %s\n", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	private final List<PathWatchEvent> events = new ArrayList<>();

	private void handleKey(WatchKey key) {
		EventListListener listener = new EventListenerImpl();

		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent.Kind kind = event.kind();
			if (kind == OVERFLOW) {
				continue;
			}

			Path child = dir.resolve((Path)(cast(event)).context());
			handleWatchEvent(
					child,
					new PathWatchEvent(dir.resolve((Path)(cast(event)).context()),
					(cast(event)))
			);
			for (PathWatchEvent pathWatchEvent : new ArrayList<>(pending.values())) {
				pending.remove(pathWatchEvent.getPath());
				events.add(pathWatchEvent);
			}
			for (PathWatchEvent e : events) {
				listener.onPathWatchEvents(e);
			}
			events.clear();
			if (recursive && (kind == ENTRY_CREATE)) {
				try {
					if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
						registerAll(child);
					}
				} catch (IOException x) {
					// ignore to keep sample readbale
				}
			}
		}
	}

	private final Map<Path, PathWatchEvent> pending = new LinkedHashMap<>(32, (float) 0.75, false);

	public void handleWatchEvent(Path path, PathWatchEvent event) {
		switch (event.getType()) {
			case ADDED:
			case MODIFIED:
			case DELETED:
				pending.put(path, event);
				break;
			case UNKNOWN:
				PrintUtils.werrPrintln("Un-know event");
				break;
		}
	}

	private Path dir;

	void processEvents() {
		for (; ; ) {
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}
			dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}
			if (key != null) {
				handleKey(key);
			} else {
				PrintUtils.werrPrintln("Watchkey is null");
			}
			boolean valid = key.reset();
			if (! valid) {
				keys.remove(key);
				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}
}

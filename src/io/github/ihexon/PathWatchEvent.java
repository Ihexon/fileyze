package io.github.ihexon;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
public class PathWatchEvent {
	private final Path path;
	private final PathWatchEventType type;
	public enum PathWatchEventType {
		ADDED, DELETED, MODIFIED, UNKNOWN
	}
	public PathWatchEvent(Path path, WatchEvent<Path> event) {
		this.path = path;
		if (event.kind() == ENTRY_CREATE) {
			this.type = PathWatchEventType.ADDED;
		} else if (event.kind() == ENTRY_DELETE) {
			this.type = PathWatchEventType.DELETED;
		} else if (event.kind() == ENTRY_MODIFY) {
			this.type = PathWatchEventType.MODIFIED;
		} else {
			this.type = PathWatchEventType.UNKNOWN;
		}

		if (! type.equals(PathWatchEventType.DELETED))
			check();
	}

	public PathWatchEventType getType()
	{
		return type;
	}

	public Path getPath()
	{
		return path;
	}


	long modified;
	long length;
	private void check() {
		if (Files.exists(path)) {
			try {
				modified = Files.getLastModifiedTime(path).toMillis();
				length = Files.size(path);
			} catch (IOException e) {
				modified = - 1;
				length = - 1;
			}
		} else {
			modified = - 1;
			length = - 1;
		}
	}
}

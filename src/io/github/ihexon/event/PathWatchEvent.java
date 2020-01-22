package io.github.ihexon.event;

import io.github.ihexon.Config;
import io.github.ihexon.types.PathWatchEventType;
import io.github.ihexon.utils.PathWatcher;
import io.github.ihexon.utils.control.Control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.concurrent.TimeUnit;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class PathWatchEvent {
	private final Path path;
	private final PathWatchEventType type;
	private final Config config;
	long checked;
	long modified;
	long length;

	private PathWatcher pathWatcher = null;

	public PathWatchEvent(Path path, PathWatchEventType type, Config config) {
		this.path = path;
		this.type = type;
		this.config = config;
		checked = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
		check();
	}

	public PathWatchEvent(Path path, WatchEvent<Path> event, Config config) {
		this.path = path;
		if (event.kind() == ENTRY_CREATE){
			this.type = PathWatchEventType.ADDED;
		}else if (event.kind() == ENTRY_DELETE){
			this.type = PathWatchEventType.DELETED;
		}else if (event.kind() == ENTRY_MODIFY){
			this.type = PathWatchEventType.MODIFIED;
		} else{
			this.type = PathWatchEventType.UNKNOWN;
		}
		this.config = config;
		checked = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
		check();
	}

	private void check(){
		if (Files.exists(path)){
			try {
				modified = Files.getLastModifiedTime(path).toMillis();
				length = Files.size(path);
			} catch (IOException e) { modified = -1; length = -1; }
		}else{ modified = -1;length = -1; }
	}

	public void modified(){
		long now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
		checked = now;
		check();
		config.setPauseUntil(now + Control.getSingleton().getUpdateQuietTimeMillis());
	}

	public boolean isQuiet(long now, long quietTime){
		long lastModified = modified;
		long lastLength = length;
		check();
		if (lastModified == modified && lastLength == length) return (now - checked) >= quietTime;
		checked = now;
		return false;
	}



	public long toQuietCheck(long now, long quietTime){
		long check = quietTime - (now - checked);
		if (check <= 0) return quietTime;
		return check;
	}


	@Override
	public boolean equals(Object obj){
		if (this == obj)  return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PathWatchEvent other = (PathWatchEvent)obj;
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)){
			return false;
		}
		return type == other.type;
	}

	@Override
	public  int hashCode(){
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((path == null) ? 0 : path.hashCode());
		result = (prime * result) + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return String.format("PathWatchEvent[%8s|%s]", type, path);
	}

	public Path getPath() {
		return path;
	}
	public PathWatchEventType getType() {
		return type;
	}

	public Config getConfig() {
		return config;
	}
}

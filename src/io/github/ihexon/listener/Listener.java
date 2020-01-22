package io.github.ihexon.listener;

import io.github.ihexon.event.PathWatchEvent;

public interface Listener {
	void onPathWatchEvent(PathWatchEvent event);
}

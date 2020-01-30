package io.github.ihexon.listener;

import io.github.ihexon.PathWatchEvent;

import java.util.EventListener;

public interface EventListListener extends EventListener
{
	void onPathWatchEvents(PathWatchEvent events);
}

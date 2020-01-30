package io.github.ihexon.listener;

import io.github.ihexon.PathWatchEvent;
import io.github.ihexon.common.DebugUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.WatchEvent;
import java.util.List;

public class EventListenerImpl implements EventListListener {
	@Override
	public void onPathWatchEvents(PathWatchEvent event) {
		StringBuilder msg = new StringBuilder();
		msg.append("Event: [");
		msg.append(event.getType());
		msg.append("] ");
		msg.append(event.getPath());
		if (Files.isRegularFile(event.getPath())) {
			try {
				String fileSize = String.format(" (File size=%,d)", Files.size(event.getPath()));
				msg.append(fileSize);
			} catch (IOException e) {
				DebugUtils.werrPrintln("Unable to get File size"+e);
			}
		}
		DebugUtils.stdPrintln("{"+msg.toString()+"}");

	}
}

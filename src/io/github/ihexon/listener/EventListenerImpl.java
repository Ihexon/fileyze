package io.github.ihexon.listener;

import io.github.ihexon.PathWatchEvent;
import io.github.ihexon.common.PrintUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventListenerImpl implements EventListListener {
	@Override
	public void onPathWatchEvents(PathWatchEvent event) {
		StringBuilder msg = new StringBuilder();
		LocalDateTime rightNow = LocalDateTime.now();
		msg.append(rightNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS ")));
		msg.append("Event: [");
		msg.append(event.getType());
		msg.append("] ");
		msg.append(event.getPath());
		if (Files.isRegularFile(event.getPath())) {
			try {
				String fileSize = String.format(" (File size=%,d)", Files.size(event.getPath()));
				msg.append(fileSize);
			} catch (IOException e) {
				PrintUtils.werrPrintln("Unable to get File size"+e);
			}
		}
		PrintUtils.stdPrintln(msg.toString());
	}
}

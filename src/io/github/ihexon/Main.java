package io.github.ihexon;

import io.github.ihexon.common.DebugUtils;
import io.github.ihexon.event.PathWatchEvent;
import io.github.ihexon.listener.Listener;
import io.github.ihexon.utils.PathWatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main implements Listener {

	public static void main(String[] args) {
		List<Path> paths = new ArrayList<>();
		for (String arg : args) {
			paths.add(new File(arg).toPath());
		}

		if (paths.isEmpty()) {
			DebugUtils.ErrPrintln("No paths specified on command line");
			System.exit(- 1);
		}

		Main demo = new Main();
		try {
			demo.run(paths);
		} catch (Throwable t) {
			DebugUtils.werrPrintln(t);
		}
	}

	public void run(List<Path> paths) throws Exception {
		PathWatcher watcher = new PathWatcher();
		watcher.setUpdateQuietTime(1, TimeUnit.SECONDS);
		watcher.addListener((PathWatcher.EventListListener) events -> {
			if (events == null) {
				DebugUtils.werrPrintln("Null events received");
			} else if (events.isEmpty()) {
				DebugUtils.werrPrintln("Empty events received");
			} else {
				DebugUtils.werrPrintln("Bulk notification received");
				for (PathWatchEvent e : events) {
					onPathWatchEvent(e);
				}
			}
		});
		watcher.setNotifyExistingOnStart(true);
		List<String> excludes = new ArrayList<>();
		excludes.add("glob:*.bak"); // ignore backup files
		excludes.add("regex:^.*/\\~[^/]*$"); // ignore scratch files

		for (Path path : paths) {
			if (Files.isDirectory(path)) {
				Config config = new Config(path);
				config.addExcludeHidden();
				config.addExcludes(excludes);
				config.setRecurseDepth(4);
				watcher.watch(config);
			} else {
				DebugUtils.ErrPrintln("Please point a dir not a file or other MatherF**k");
			}
		}

		watcher.start();
		Thread.currentThread().join();
	}


	@Override
	public void onPathWatchEvent(PathWatchEvent event) {

		StringBuilder msg = new StringBuilder();
		msg.append("onPathWatchEvent: [");
		msg.append(event.getType());
		msg.append("] ");
		msg.append(event.getPath());
		msg.append(" (count=").append(event.getCount()).append(")");
		if (Files.isRegularFile(event.getPath()))
		{
			try
			{
				String fsize = String.format(" (filesize=%,d)", Files.size(event.getPath()));
				msg.append(fsize);
			}
			catch (IOException e)
			{
				DebugUtils.werrPrintln("Unable to get filesize "+e.getMessage());
			}
		}
		DebugUtils.werrPrintln("{"+msg.toString()+"}");

	}
}

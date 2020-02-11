package io.github.ihexon.other;

import io.github.ihexon.common.PrintUtils;
import io.github.ihexon.services.logsystem.Log;

public class HelpClass {

	public HelpClass(){
		StringBuilder msg = new StringBuilder();
		msg.append("WatchMe - A util use to monitor the changes of directory\n");
		msg.append("usage:\n");
		msg.append("watchme [OPTION] [DIRECTOR]\n");
		msg.append("-r                  : recurse monitor, means the all directory and file will be monitor\n" +
					"-d [dir]                : the directory to monitor\n" +
					"--exclude-hidden   : exclude the hidden directory\n" +
					"--log	[dir]			: output events to log file\n" +
					"--help & -h        : show help\n");
		Log.getInstance().info(msg);
	}

}

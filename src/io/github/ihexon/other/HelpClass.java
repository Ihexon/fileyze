/***************************************************************************************************
 * Copyright (C) 2019 - 2020, IHEXON
 * This file is part of the WatchMe.
 *
 * WatchMe is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * WatchMe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WatchMe; see the file COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * This Copyright copy from shadowsocks-libev. with little modified
 **************************************************************************************************/

package io.github.ihexon.other;
import io.github.ihexon.services.logsystem.Log;

public class HelpClass {

	public HelpClass(){
		StringBuilder msg = new StringBuilder();
		msg.append("WatchMe - A util use to monitor the changes of directory\n");
		msg.append("usage:\n");
		msg.append("watchme [OPTION] [DIRECTOR]\n");
		msg.append("-r                  	: recurse monitor, means the all directory and file will be monitor\n" +
					"-d [dir]              	: the directory to monitor\n" +
					"--exchid  			   	: exclude the hidden directory\n" +
					"--log	[dir]			: output events to log file\n" +
					"--help & -h        	: show help\n"+
					"-ex [syntax:pattern]   : exclude path or file with pattern [syntax:pattern]\n"
		);
		Log.getInstance().info(msg.toString());
		Log.getInstance().closeAppenders();
	}
}

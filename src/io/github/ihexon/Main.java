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

package io.github.ihexon;

import io.github.ihexon.common.PrintUtils;
import io.github.ihexon.services.logsystem.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Main {
	// initialize PrintUtils FIRST !
	private final static Log logger = Log.getInstance();

	public static void main(String[] args) {
		String s[] = args != null ? Arrays.copyOf(args, args.length) : null;
		CommandLine cmdLine = null;
		try {
			cmdLine = new CommandLine(args != null ? Arrays.copyOf(args, args.length) : null);
		} catch (final Exception e) {
			logger.info("Failed due to invalid parameters: " + Arrays.toString(args));
			if (e instanceof ArrayIndexOutOfBoundsException){
				int i = (Integer.parseInt(e.getMessage()))-1;
				logger.info(Objects.requireNonNull(s)[i] + " need one value");
			}
			logger.info("Use '-h' for more details.");
			System.exit(1);
		}
		Bootstrap demo = new Bootstrap(cmdLine);
		try {
			demo.start();
		} catch (IOException e){
			PrintUtils.werrPrintln(e.getMessage());

		}
	}
}

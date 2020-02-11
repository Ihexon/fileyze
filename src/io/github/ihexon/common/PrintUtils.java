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

package io.github.ihexon.common;

import io.github.ihexon.logutils.AppenderAttachableImpl;
import io.github.ihexon.spi.LoggingEvent;
import io.github.ihexon.utils.control.Control;

import java.io.IOException;

/**
 * <font color="#AA2222"><b>This class has been deprecated and will remove in next release</em></b></font>
 * Please use {@link io.github.ihexon.services.logsystem.Log} to log the message .
 * @see io.github.ihexon.services.logsystem.Log
 */
public class PrintUtils {

	private static PrintUtils printUtils = null;

	Appender ConsoleAppender;
	Appender FileAppender;
	AppenderAttachableImpl aai;

	public static PrintUtils getSingleton() {
		return printUtils;
	}

	public static void initSingleton(Control control) throws IOException {
		printUtils = new PrintUtils();
		printUtils.init(control);
	}

	public static void stdPrintln(Object x) {
		String s = String.valueOf(x);
		System.out.println(s);
	}

	public static void werrPrintln(Object x) {
		String s = String.valueOf(x);
		System.err.println(s);
	}


	private void init(Control control) throws IOException {
		ConsoleAppender = new ConsoleAppender();
		addAppender(ConsoleAppender);
		if (control.logFile != null){
			FileAppender = new FileAppender(control.logFile.toString());
			addAppender(FileAppender);
		}
	}

	public void info(Object message) {
		Log(message, null);
	}

	// this method not TESTED !!! I DONT KNOW IF IT WORKS RIGHT !!!
	void info(Object message, Throwable t) {
		Log(message, t);
	}

	protected void Log(Object message, Throwable t) {
		callAppenders(new LoggingEvent(message, t));
	}


	synchronized
	public
	void addAppender(Appender newAppender) {
		if(aai == null) {
			aai = new AppenderAttachableImpl();
		}
		aai.addAppender(newAppender);
	}

	private void callAppenders(LoggingEvent event) {
		aai.appendLoopOnAppenders(event);
	}

	public void closeAppenders() {
		if (aai != null){
			aai.closeNestedAppenders();
		}
	}
}

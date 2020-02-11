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

package io.github.ihexon.services.logsystem;

import io.github.ihexon.common.Appender;
import io.github.ihexon.common.ConsoleAppender;
import io.github.ihexon.common.FileAppender;
import io.github.ihexon.logutils.AppenderAttachableImpl;
import io.github.ihexon.spi.LoggingEvent;

public class Log {

    private static Log instance = null;
    private ConsoleAppender ca;
    private FileAppender fa;
    private AppenderAttachableImpl aai;

    public static Log getInstance() {
        if (instance == null) createInstance();
        return instance;
    }

    synchronized private static void createInstance() {
        if (instance == null) {
            instance = new Log();
            instance.init();
        }
    }

    /**
     * You should use {@link #addAppender(Appender)} to add new {@link Appender}
     * @param newAppender the new {@link Appender} to be add.
     * @see AppenderAttachableImpl
     */
    synchronized public void addAppender(Appender newAppender) {
        if (aai == null) {
            aai = new AppenderAttachableImpl();
        }
        aai.addAppender(newAppender);
    }

    private void init() {
        // Initialize a ConsoleAppender first
        ca = new ConsoleAppender();
        addAppender(ca);
    }

    public void info(Object message) {
        Logger(message, null);
    }

    public void closeAppenders() {
        if (aai != null) {
            aai.closeNestedAppenders();
        }
    }

    private void Logger(Object message, Throwable throwable) {
        callAppenders(new LoggingEvent(message, throwable));
    }

    private void callAppenders(LoggingEvent e) {
        aai.appendLoopOnAppenders(e);
    }

}

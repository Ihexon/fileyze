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

import io.github.ihexon.services.logsystem.Log;

import java.io.IOException;

abstract class AbstractBootstrap {
    private final CommandLine commandLine;
    private final ControlOverrides controlOverrides;

    protected AbstractBootstrap(CommandLine cmdline) {
        this.commandLine = cmdline;
        controlOverrides = new ControlOverrides();
    }

    protected ControlOverrides getControlOverrides() {
        return controlOverrides;
    }

    public int start() throws IOException {
        try {
            Constant instant = Constant.getInstance();
            instant.initializeFilesAndDirectories();
        } catch (final Throwable e) {
            Log.getInstance().info(e.getMessage());
            return 1;
        }
        return 0;
    }

    protected CommandLine getCommandLine() {
        return commandLine;
    }
}

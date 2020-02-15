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

package io.github.ihexon.utils;

import io.github.ihexon.common.PrintUtils;
import io.github.ihexon.services.logsystem.Log;
import io.github.ihexon.utils.control.Control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class NewDirFilter implements Predicate<Path> {

    public boolean isHidden(Path path) {
        try {
            if (!path.startsWith(Control.getSingleton().dir))
                return true;
            for (int i = Control.getSingleton().dir.getNameCount();
                 i < path.getNameCount();
                 i++) {
                if (path.getName(i).toString().startsWith(".")) {
                    return true;
                }
            }
            return Files.exists(path) && Files.isHidden(path);
        } catch (IOException e) {
            PrintUtils.werrPrintln(e);
            return false;
        }
    }

    @Override
    public boolean test(Path path) {
        if (Control.getSingleton().excludeHidden && isHidden(path)) {
            if (Control.getSingleton().isDebug)
                Log.getInstance().info("test({" + (path.toString()) + "}) -> [Hidden]");
            return true;
        }

        if (!path.startsWith(Control.getSingleton().dir)) {
            if (Control.getSingleton().isDebug)
                Log.getInstance().info("test({" + path.toString() + "}) -> [!child {" + Control.getSingleton().dir.toString() + "}]");
            return false;
        }

        // HAVE BUG !!!
//        if (recurseDepth != UNLIMITED_DEPTH) {
//            int depth = path.getNameCount() - this.path.getNameCount() - 1;
//
//            if (depth > recurseDepth) {
//                if (LOG.isDebugEnabled())
//                    LOG.debug("test({}) -> [depth {}>{}]", toShortPath(path), depth, recurseDepth);
//                return false;
//            }
//        }

		boolean matched = Control.getSingleton().includeExclude.test(path);
        return matched;
    }
}

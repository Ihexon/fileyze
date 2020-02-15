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

import io.github.ihexon.services.logsystem.Log;
import io.github.ihexon.utils.control.Control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class BigFilter implements Predicate<Path> {

    private static BigFilter instance = null;

    private boolean isDebug = Control.getSingleton().isDebug;
    private boolean excludeHidden = Control.getSingleton().excludeHidden;


    public static BigFilter getInstance(){
        if (instance == null){
            createInstance();
        }
        return instance;
    }

    private static void createInstance(){
        if (instance == null){
            instance = new BigFilter();
        }
    }

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
            Log.getInstance().info(e);
            return false;
        }
    }




    /**
     * Return means that the dir is not exclude ,not hidden,
     * if the dir is subdir , make sure it not link to its
     * parent dir.
     */
    @Override
    public boolean test(Path path) {

        //  make sure it not link to its parent dir.
        if (!path.startsWith(Control.getSingleton().dir)) {
            if (isDebug)
                Log.getInstance().info("test({" + path.toString() + "}) -> [!child {" + Control.getSingleton().dir.toString() + "}]");
            return false;
        }

        // make sure it not hidden dir
        if (excludeHidden && isHidden(path)) {
            if (isDebug)
                Log.getInstance().info("test({" + (path.toString()) + "}) -> [Hidden]");
            return true;
        }

        // make soure it not in exclude list
        if (Control.getSingleton().exclude != null) {
            boolean matched = Control.getSingleton().exclude.test(path);
            return matched;
        }
        return true;
    }
}

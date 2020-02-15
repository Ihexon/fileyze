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

import java.io.File;
import java.util.Hashtable;

public class CommandLine {

    public final String DIR = "-d";
    public final String RECURSE = "-r";
    public final String EXCLUDES_HID = "-exchid";
    public final String HELP0x00 = "-h";
    public final String HELP0x01 = "--help";
    public final String VERSION0x00 = "--version";
    public final String VERSION0x01 = "-v";
    public final String LOGFILE = "--log";
    public final String EXCLUDEREGX = "-ex";

    private final Hashtable<String, String> keywords = new Hashtable<>();

    public CommandLine(String[] args) throws Exception {
        args = args == null ? new String[0] : args;
        parseArgs(args);
    }

    /**
     * Start parsing two type of parameters.
     * <p><strong>parse KeysPair : </strong></p> eg: -d [dir], --log [file] <br />
     * the parameter which comes in pairs,
     * <p><strong>parse Switch : </strong></p> eg : -h, --help, -v, --version<br />
     * the parameter whch comes to be single.
     *
     * @param args which the String[] contains the parameters
     * @throws Exception
     */
    private void parseArgs(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            if (parseSwitchs(args, i)) continue;
            if (parseKeywords(args, i)) continue;
        }
    }

    /**
     * Selects the valid parameter in args, use {@link #checkPair(String[], String, int)}
     * to parse the KeyPairs and store into a HashTable which named {@link #keywords}.
     *
     * @param args args which the String[] contains the parameters
     * @param i    the index of the parameter which to be checked
     * @return boolean value, if the parameter in args to be checked is valid.
     * than return true,otherwise return false
     * @throws Exception throw {@link Exception} when the parameter can not be parse.
     */
    private boolean parseKeywords(String[] args, int i) throws Exception {
        boolean result = false;
        if (checkPair(args, DIR, i)) {
            result = true;
        } else if (checkPair(args, LOGFILE, i)) {
            result = true;
        } else if (checkPair(args, EXCLUDEREGX, i)) {
            result = true;
        }
        return result;
    }

    /**
     * The {@link #parseSwitchs(String[], int)} use {@link #checkSwitch(String[], String, int)} to check the valid parameter.
     * If the parameter is valid, than it will be store in a HashTable named keywords
     *
     * @param args args which the String[] contains the parameters
     * @param i    he index of the parameter which to be checked
     * @return boolean value, if the parameter in args to be checked is valid.
     * than return true,otherwise return false
     * @throws Exception throw {@link Exception} when the parameter can not be parse.
     */
    private boolean parseSwitchs(String[] args, int i) throws Exception {
        boolean result = false;
        if (checkSwitch(args, RECURSE, i)) {
            result = true;
        } else if (checkSwitch(args, HELP0x00, i)) {
            result = true;
        } else if (checkSwitch(args, HELP0x01, i)) {
            result = true;
        } else if (checkSwitch(args, EXCLUDES_HID, i)) {
            result = true;
        }
        return result;
    }

    public boolean isValidExistingDirectory(String path) {
        if (path == null || path.trim().isEmpty()) return false;
        File file = new File(path);
        return file.isDirectory();
    }

    synchronized private boolean checkPair(String[] args, String paramName, int i) throws Exception {
        String key = args[i];
        String value = null;
        if (key == null) {
            return false;
        }

        // check the linux path name.
        if (key.equalsIgnoreCase(DIR)) {
            value = args[i + 1];
            // USE REGEX TO TEST PATH, BUT IT BUGGLY
//            IncludeExclude<String> ie = new IncludeExclude<>(RegexSet.class);
//            ie.include("^/|(/[a-zA-Z0-9_-]+)+$");
            if (!isValidExistingDirectory(value.trim())) {
                Log.getInstance().info("Error parameter  '" +key.trim()+ "'");
                Log.getInstance().info("The dir is not exist or wrong path name");
                Log.getInstance().closeAppenders();
                System.exit(-1);
            }
//            ie.clear();
        }

        if (key.equalsIgnoreCase(paramName)) {
            value = args[i + 1];
            if (value == null) {
                throw new Exception("Missing parameter for keyword '" + paramName + "'.");
            }
            keywords.put(paramName, value);
            args[i] = null;
            args[i + 1] = null;
            return true;
        }
        return false;
    }

    synchronized private boolean checkSwitch(String[] args, String paramName, int i) throws Exception {
        String key = args[i];
        if (key == null) {
            return false;
        }
        if (key.equalsIgnoreCase(paramName)) {
            keywords.put(paramName, "true");
            args[i] = null;
            return true;
        }
        return false;
    }


    public String getKeyPair(String key) {
        if (keywords.get(key) != null && keywords.get(key).length() != 0)
            return keywords.get(key);
        return null;
    }

    public boolean getSwitchs(String key) {
        if (key != null && key.length() != 0) {
            String result = getKeyPair(key);
            if (result != null && result.equalsIgnoreCase("true"))
                return true;
        }
        return false;
    }
}

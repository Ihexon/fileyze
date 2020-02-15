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

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A Set of Regular expressions strings.
 * <p>
 * Provides the efficient {@link #matches(String)} method to check for a match against all the combined Regex's
 */
public class RegexSet extends AbstractSet<String> implements Predicate<String> {

    private  Set<String> _patternString = new HashSet<String>();
    private Pattern _pattern;

    @Override
    public boolean add(String pattern)
    {
        boolean added = _patternString.add(pattern);
        if (added)
            updatePattern();
        return added;
    }

    private void updatePattern(){
        StringBuilder builder = new StringBuilder();
        builder.append("^(");
        for (String patterStringN : _patternString){
            if (builder.length() > 2)
                builder.append('|');
            builder.append('(');
            builder.append(patterStringN);
            builder.append(')');
        }
        builder.append(")$");
        _pattern = Pattern.compile(builder.toString());
    }

    @Override
    public boolean test(String s) {
        return _pattern != null&&_pattern.matcher(s).matches();
    }

    // same as test
    public boolean matches(String s)
    {
        return test(s);
    }


    @Override
    public Iterator<String> iterator() {
        return _patternString.iterator();
    }

    @Override
    public int size() {
        return _patternString.size();
    }
}

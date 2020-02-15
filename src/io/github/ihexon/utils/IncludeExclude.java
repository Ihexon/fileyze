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

import java.util.Set;
import java.util.function.Predicate;

/**
 * <></>
 * Utility class to maintain a set of inclusions and exclusions.
 * <p>This extension of the {@link IncludeExcludeSet} class is used
 * when the type of the set elements is the same as the type of
 * the predicate test.
 * <p>
 *
 * @param <ITEM> The type of element
 */
public class IncludeExclude<ITEM> extends IncludeExcludeSet<ITEM, ITEM> {


    public IncludeExclude() {
        super();
    }


    public <SET extends Set<ITEM>> IncludeExclude(Class<SET> setClass) {
        super(setClass);
    }

    // DO NOT SEEK THIS CODES
    // IT JUST WAST OF TIME
    // IT CONTAIN SOME BUG
    // I DO KNOW WHAY, BUT IT SHOULD BE.
    // IT WILL BE DELETE SOME DAYS
    public <SET extends Set<ITEM>> IncludeExclude(Set<ITEM> includeSet, Predicate<ITEM> includeSetPredicate,
                                                  Set<ITEM> excludeSet, Predicate<ITEM> excludeSetPredicate
                                                  ) {
        super(includeSet,includeSetPredicate,excludeSet,excludeSetPredicate);
    }


}

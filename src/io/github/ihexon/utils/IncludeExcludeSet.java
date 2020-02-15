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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * <font color="#AA2222"><p>NEW! Since Release 2.0, we will use Include/Exclude class to do the
 * right work, but not now, because it not tested, and may contain some bug now</p></font>
 * Utility class to maintain a set of inclusions and exclusions.
 * <p>Maintains a set of included and excluded elements.  The method {@link #test(Object)}
 * will return true IFF the passed object is not in the excluded set AND ( either the
 * included set is empty OR the object is in the included set)
 * <p>The type of the underlying {@link Set} used may be passed into the
 * constructor, so special sets like Servlet PathMap may be used.
 * <p>
 *
 * @param <T> The type of element of the set (often a pattern)
 * @param <P> The type of the instance passed to the predicate
 */
public class IncludeExcludeSet<T, P> implements Predicate<P> {

    private  Set<T> _includes;
    private  Set<T> _excludes;
    private  Predicate<P> _includePredicate;
    private  Predicate<P> _excludePredicate;


    public IncludeExcludeSet() {
        this(HashSet.class);
    }


    /**
     * Construct an IncludeExclude.
     *
     * @param setClass The type of {@link Set} to using internally to hold patterns. Two instances will be created.
     *                 one for include patterns and one for exclude patters.  If the class is also a {@link Predicate},
     *                 then it is also used as the item test for the set, otherwise a {@link SetContainsPredicate} instance
     *                 is created.
     * @param <SET>    The type of a set to use as the backing store
     */
    public <SET extends Set<T>> IncludeExcludeSet(Class<SET> setClass) {

        try {
            _includes = setClass.getDeclaredConstructor().newInstance();
            _excludes = setClass.getDeclaredConstructor().newInstance();

            if (_includes instanceof Predicate && _excludes instanceof Predicate) {
                _includePredicate = (Predicate<P>) _includes;
                _excludePredicate = (Predicate<P>) _excludes;
            } else {
                _includePredicate = new SetContainsPredicate(_includes);
                _excludePredicate = new SetContainsPredicate(_excludes);
            }


        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    private static class SetContainsPredicate<T> implements Predicate<T> {

        private final Set<T> set;

        public SetContainsPredicate(Set<T> set) {
            this.set = set;
        }


        @Override
        public String toString() {
            return "CONTAINS";
        }

        @Override
        public boolean test(T item) {
            return set.contains(item);
        }
    }


    // DO SEEK THIS CODE, CONTAIN SOME F**K BUG.
    // IT JUST WAST OF TIME.
    // NOT TEST
    // NOT SURE IT WORK OR NOT
    // DO NOT USE PLEASE
    public <SET extends Set<T>> IncludeExcludeSet(Set<T> includeSet, Predicate<P> includePredicate,
                                                  Set<T> excludeSet, Predicate<P> excludePredicate) {
        Objects.requireNonNull(includeSet, "IncludeSet Null");
        Objects.requireNonNull(excludeSet, "ExcludeSet Null");
        Objects.requireNonNull(includePredicate, "IncludePredicate Null");
        Objects.requireNonNull(excludePredicate, "ExcludePredicate Null");

        this._includes = includeSet;
        this._excludes = excludeSet;
        this._includePredicate = includePredicate;
        this._excludePredicate = excludePredicate;

    }

    public void include(T element) {
        _includes.add(element);
    }

    public void include(T... element) {
        for (T e : element) {
            _includes.add(e);
        }
    }

    public void exclude(T element) {
        _excludes.add(element);
    }

    public void exclude(T... element) {
        for (T e : element) {
            _excludes.add(e);
        }
    }

    /**
     * When return.
     * it means the that the element to be include.
     * it also means that the element not in exclude list
     * @param t the path to be test
     * @return boolean
     */
    @Override
    public boolean test(P t)
    {
        if (!_includes.isEmpty() && !_includePredicate.test(t))
            return false;
        return !_excludePredicate.test(t);
    }

    public int size() {
        return _includes.size() + _excludes.size();
    }

    public Set<T> getIncluded() {
        return _includes;
    }

    public Set<T> getExcluded() {
        return _excludes;
    }

    public void clear() {
        if (_includes != null)
        _includes.clear();
        if (_excludes != null)
        _excludes.clear();
    }

    public boolean isEmpty() {
        return _includes.isEmpty() && _excludes.isEmpty();
    }


    // NOT TEST, NOT SURE IT WORK OR NOT, DO NOT USE PLEASE
    public Boolean isIncludedAndNotExcluded(P item) {
        if (_excludePredicate.test(item))
            return Boolean.FALSE;
        if (_includePredicate.test(item))
            return Boolean.TRUE;

        return null;
    }

    // NOT TEST, NOT SURE IT WORK OR NOT, DO NOT USE PLEASE
    public boolean hasIncludes() {
        return !_includes.isEmpty();
    }

    // DO SEEK THIS CODE, CONTAIN SOME BUG.
    // IT JUST WAST OF TIME.
    // NOT TEST.
    // NOT SURE IT WORK OR NOT.
    // DO NOT USE PLEASE.
    public static <T1, T2> boolean matchCombined(T1 item1, IncludeExcludeSet<?, T1> set1, T2 item2, IncludeExcludeSet<?, T2> set2) {
        Boolean match1 = set1.isIncludedAndNotExcluded(item1);
        Boolean match2 = set2.isIncludedAndNotExcluded(item2);

        if (match1 == Boolean.FALSE || match2 == Boolean.FALSE)
            return false;

        if (set1.hasIncludes() || set2.hasIncludes())
            return match1 == Boolean.TRUE || match2 == Boolean.TRUE;

        return true;
    }


}

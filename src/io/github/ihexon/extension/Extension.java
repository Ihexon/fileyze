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

package io.github.ihexon.extension;

public interface Extension {

    /**
     * Returns the name of the extension, for configurations and access from other components (e.g.
     * extensions).
     *
     * @return the name of the extension, never {@code null}
     */
    String getName();

    /**
     * Returns the description of the extension, to be shown in UI components. The description must
     * be internationalised.
     *
     * @return the description of the extension, never {@code null}
     */
    String getDescription();

    /** Initialize plugin during startup. This phase is carried out before all others. */
    void init();



}

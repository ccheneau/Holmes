/*
 * Copyright (C) 2012-2013  Cedric Cheneau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sun.syndication.feed.module.mediarss.io;

import org.jdom.Namespace;

/**
 * @author cooper
 */
public class AlternateMediaModuleParser extends MediaModuleParser {

    private static final Namespace NS = Namespace.getNamespace("http://search.yahoo.com/mrss");

    /**
     * Creates a new instance of AlternateMediaModuleParser.
     */
    public AlternateMediaModuleParser() {
        super();
    }

    @Override
    public String getNamespaceUri() {
        return "http://search.yahoo.com/mrss";
    }

    /**
     * Gets the name space.
     *
     * @return the name space
     */
    public Namespace getNS() {
        return AlternateMediaModuleParser.NS;
    }
}

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

package com.sun.syndication.feed.module.itunes.io;

import org.jdom.Namespace;

/**
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class ITunesParserOldNamespace extends ITunesParser {
    private static final String URI = "http://www.itunes.com/DTDs/Podcast-1.0.dtd";

    /**
     * Creates a new instance of ITunesParserOldNamespace
     */
    public ITunesParserOldNamespace() {
        super(Namespace.getNamespace(URI));
    }

    @Override
    public String getNamespaceUri() {
        return URI;
    }
}

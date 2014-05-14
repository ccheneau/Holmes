/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.itunes.AbstractITunesObject;
import com.sun.syndication.feed.module.itunes.EntryInformationImpl;
import com.sun.syndication.feed.module.itunes.types.Duration;
import com.sun.syndication.io.ModuleParser;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.10 $
 */
public class ITunesParser implements ModuleParser {
    private final Namespace ns;

    /**
     * Creates a new instance of ITunesParser
     */
    public ITunesParser() {
        ns = Namespace.getNamespace(AbstractITunesObject.URI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNamespaceUri() {
        return AbstractITunesObject.URI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Module parse(final Element element) {
        AbstractITunesObject module = null;

        if ("item".equals(element.getName())) {
            EntryInformationImpl entryInfo = new EntryInformationImpl();
            module = entryInfo;

            Element duration = element.getChild("duration", ns);
            if (duration != null)
                entryInfo.setDuration(new Duration(duration.getValue().trim()));
        }
        return module;
    }
}

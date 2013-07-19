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

package net.holmes.core.inject.provider;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import net.holmes.core.common.configuration.ConfigurationNode;
import net.holmes.core.common.configuration.XmlRootNode;

import javax.inject.Provider;

/**
 * XStream provider.
 */
public class XStreamProvider implements Provider<XStream> {

    @Override
    public XStream get() {
        XStream xs = new XStream(new DomDriver("UTF-8"));
        xs.alias("config", XmlRootNode.class);
        xs.alias("node", ConfigurationNode.class);
        return xs;
    }
}

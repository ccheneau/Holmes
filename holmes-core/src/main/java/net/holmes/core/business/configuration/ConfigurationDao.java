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

package net.holmes.core.business.configuration;

import net.holmes.core.business.media.model.RootNode;
import net.holmes.core.common.ConfigurationParameter;

import java.io.IOException;
import java.util.List;

/**
 * Holmes configuration dao.
 */
public interface ConfigurationDao {

    /**
     * Save configuration.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    void saveConfig() throws IOException;

    /**
     * Gets the configuration nodes.
     *
     * @param rootNode root node
     * @return configuration nodes
     */
    List<ConfigurationNode> getNodes(RootNode rootNode);

    /**
     * Get parameter value.
     *
     * @param parameter parameter
     * @return parameter value
     */
    <T> T getParameter(final ConfigurationParameter<T> parameter);

    /**
     * Sets parameter value.
     *
     * @param parameter parameter
     * @param value     parameter value
     */
    <T> void setParameter(final ConfigurationParameter<T> parameter, T value);
}

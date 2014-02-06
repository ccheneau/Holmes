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
     * Gets the folders.
     *
     * @param folderRootNode folder root node
     * @return folders
     */
    List<ConfigurationNode> getFolders(RootNode folderRootNode);

    /**
     * Gets the boolean parameter.
     *
     * @param parameter parameter
     * @return boolean parameter
     */
    Boolean getBooleanParameter(Parameter parameter);

    /**
     * Gets the int parameter.
     *
     * @param parameter parameter
     * @return int parameter
     */
    Integer getIntParameter(Parameter parameter);

    /**
     * Gets the parameter.
     *
     * @param parameter parameter
     * @return parameter
     */
    String getParameter(Parameter parameter);

    /**
     * Sets parameter value.
     *
     * @param parameter parameter
     * @param value     parameter value
     */
    void setParameter(Parameter parameter, String value);

    /**
     * Sets boolean parameter value.
     *
     * @param parameter parameter
     * @param value     parameter value
     */
    void setBooleanParameter(Parameter parameter, Boolean value);
}

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

package net.holmes.core.backend.handler;

import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.response.ConfigurationFolder;
import net.holmes.core.business.media.model.RootNode;

import java.util.Collection;

/**
 * Abstract handler for configuration folders REST requests.
 */
public abstract class AbstractFolderHandler {

    private final BackendManager backendManager;
    private final RootNode rootNode;

    /**
     * Instantiates a new abstract folder handler.
     *
     * @param backendManager backend manager
     * @param rootNode       root node
     */
    protected AbstractFolderHandler(BackendManager backendManager, RootNode rootNode) {
        this.backendManager = backendManager;
        this.rootNode = rootNode;
    }

    /**
     * Get configuration folders.
     *
     * @return configuration folders
     */
    protected Collection<ConfigurationFolder> getFolders() {
        return backendManager.getFolders(rootNode);
    }

    /**
     * Get configuration folder.
     *
     * @param id folder id
     * @return configuration folder
     */
    protected ConfigurationFolder getFolder(final String id) {
        return backendManager.getFolder(id, rootNode);
    }

    /**
     * Add configuration folder.
     *
     * @param folder folder to add
     * @return added folder
     */
    protected ConfigurationFolder addFolder(final ConfigurationFolder folder) {
        backendManager.addFolder(folder, rootNode);
        return folder;
    }

    /**
     * Edit configuration folder.
     *
     * @param id     folder id
     * @param folder new folder value
     * @return edited folder
     */
    protected ConfigurationFolder editFolder(final String id, final ConfigurationFolder folder) {
        backendManager.editFolder(id, folder, rootNode);
        return folder;
    }

    /**
     * Remove configuration folder.
     *
     * @param id folder id to remove
     * @return removed folder
     */
    protected ConfigurationFolder removeFolder(final String id) {
        backendManager.removeFolder(id, rootNode);
        return new ConfigurationFolder(id, null, null);
    }
}

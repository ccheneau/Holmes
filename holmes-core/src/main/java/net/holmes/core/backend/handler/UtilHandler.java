/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

import net.holmes.core.business.version.ReleaseInfo;
import net.holmes.core.business.version.VersionManager;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.*;
import static net.holmes.core.common.FileUtils.listChildren;
import static net.holmes.core.common.SystemProperty.USER_HOME;

/**
 * Handler for util REST requests.
 */
@Path("/backend/util")
public final class UtilHandler {

    private final VersionManager versionManager;

    /**
     * Instantiates a new util handler.
     *
     * @param versionManager holmes version manager
     */
    @Inject
    public UtilHandler(final VersionManager versionManager) {
        this.versionManager = versionManager;
    }

    /**
     * Get current Holmes version.
     *
     * @return version
     */
    @GET
    @Path("/getVersion")
    @Produces(TEXT_PLAIN)
    public String getVersion() {
        String currentVersion = versionManager.getCurrentVersion();
        return currentVersion != null ? currentVersion : "alpha";
    }

    /**
     * Get Holmes release info.
     *
     * @return release info
     */
    @GET
    @Path("/getReleaseInfo")
    @Produces(APPLICATION_JSON)
    public ReleaseInfo getReleaseInfo() {
        return versionManager.getRemoteReleaseInfo();
    }

    /**
     * Get child folders.
     *
     * @param parentPath parent path
     * @return child folders
     */
    @POST
    @Path("/getChildFolders")
    @Produces(APPLICATION_JSON)
    public Collection<Folder> getChildFolders(@FormParam("path") final String parentPath) {
        Collection<Folder> folders = new ArrayList<>();
        if (parentPath == null || "none".equals(parentPath)) {
            // No parent path specified
            // Add user home folder to response
            File userHomeDir = new File(USER_HOME.getValue());
            folders.add(new Folder(userHomeDir.getName(), userHomeDir.getAbsolutePath()));

            // Add server root folders to response
            FileSystems.getDefault().getRootDirectories()
                    .forEach(root -> folders.add(new Folder(root.toString(), root.toString())));
        } else {
            // Get child folders
            folders.addAll(listChildren(parentPath, false).stream()
                    .map(child -> new Folder(child.getName(), child.getAbsolutePath()))
                    .collect(toList()));
        }
        return folders;
    }

    /**
     * Folder.
     */
    public static class Folder {
        private final String data;
        private final String state;
        private final Map<String, String> metadata;

        /**
         * Instantiates a new folder.
         *
         * @param data folder data
         * @param path folder path
         */
        public Folder(final String data, final String path) {
            this.data = data;
            this.metadata = new HashMap<>();
            this.metadata.put("path", path);
            this.state = "closed";
        }

        /**
         * Get folder state.
         *
         * @return folder state
         */
        public String getState() {
            return state;
        }

        /**
         * Get folder data.
         *
         * @return folder data
         */
        public String getData() {
            return data;
        }

        /**
         * Get folder meta data.
         *
         * @return folder meta data
         */
        public Map<String, String> getMetadata() {
            return metadata;
        }
    }
}

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

package net.holmes.core.backend.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Map;

import static net.holmes.common.SystemProperty.USER_HOME;

/**
 * Handler for util REST requests.
 */
@Path("/backend/util")
public final class UtilHandler {

    /**
     * Get Holmes version.
     *
     * @return version
     */
    @GET
    @Path("/getVersion")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVersion() {
        String version = this.getClass().getPackage().getImplementationVersion();
        return version == null ? "alpha" : version;
    }

    /**
     * Get child folders.
     *
     * @param parentPath parent path
     * @return child folders
     */
    @POST
    @Path("/getChildFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Folder> getChildFolders(@FormParam("path") final String parentPath) {
        Collection<Folder> folders = Lists.newArrayList();

        if (parentPath == null || parentPath.equals("none")) {
            // Add user home folder
            File userHomeDir = new File(USER_HOME.getValue());
            folders.add(new Folder(userHomeDir.getName(), userHomeDir.getAbsolutePath()));

            // Add root folders
            File[] roots = File.listRoots();
            if (roots != null) {
                for (File root : roots) {
                    folders.add(new Folder(root.getAbsolutePath(), root.getAbsolutePath()));
                }
            }
        } else {
            // Get child folders
            File fPath = new File(parentPath);
            if (fPath.exists() && fPath.isDirectory() && fPath.canRead()) {
                File[] childDirs = fPath.listFiles(new FolderFileFilter());
                if (childDirs != null) {
                    for (File childDir : childDirs) {
                        folders.add(new Folder(childDir.getName(), childDir.getAbsolutePath()));
                    }
                }
            }
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
            this.metadata = Maps.newHashMap();
            this.metadata.put("path", path);
            this.state = "closed";
        }

        public String getState() {
            return state;
        }

        public String getData() {
            return data;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }
    }

    /**
     * Folder file filter.
     */
    private static class FolderFileFilter implements FileFilter {

        @Override
        public boolean accept(final File file) {
            return file.exists() && file.isDirectory() && file.canRead() && !file.isHidden() && !file.getName().startsWith(".") && file.listFiles() != null;
        }

    }
}

/**
* Copyright (C) 2012  Cedric Cheneau
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
package net.holmes.core.backend;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.holmes.core.backend.response.Folder;
import net.holmes.core.util.HolmesProperty;

@Path("/backend/util")
public class UtilHandler {

    /**
     * Get holmes version
     */
    @GET
    @Path("/getVersion")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVersion() {
        String version = this.getClass().getPackage().getImplementationVersion();
        return version == null ? "alpha" : version;
    }

    /**
     * Get child folders
     */
    @POST
    @Path("/getChildFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Folder> getChildFolders(@FormParam("path") String parentPath) {
        Collection<Folder> folders = new ArrayList<Folder>();

        if (parentPath == null || parentPath.equals("none")) {
            // Add user home folder
            File userHomeDir = new File(System.getProperty(HolmesProperty.SYS_VAR_USER_HOME.getValue()));
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
                File[] childDirs = fPath.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.exists() && file.isDirectory() && file.canRead() && !file.isHidden() && !file.getName().startsWith(".")
                                && file.listFiles() != null;
                    }
                });
                if (childDirs != null) {
                    for (File childDir : childDirs) {
                        folders.add(new Folder(childDir.getName(), childDir.getAbsolutePath()));
                    }
                }
            }
        }
        return folders;
    }
}

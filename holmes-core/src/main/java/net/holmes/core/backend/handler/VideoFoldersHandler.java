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

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static net.holmes.core.media.model.RootNode.VIDEO;

/**
 * Handler for video folders REST requests.
 */
@Path("/backend/videoFolders")
public final class VideoFoldersHandler {

    private final BackendManager backendManager;

    /**
     * Instantiates a new video folders handler.
     *
     * @param backendManager backend manager
     */
    @Inject
    public VideoFoldersHandler(final BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    /**
     * Get video folders.
     *
     * @return video folders
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getVideoFolders() {
        return backendManager.getFolders(VIDEO);
    }

    /**
     * Get video folder.
     *
     * @param id video folder id
     * @return video folder
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder getVideoFolder(@PathParam("id") final String id) {
        return backendManager.getFolder(id, VIDEO);
    }

    /**
     * Add video folder.
     *
     * @param folder video folder to add
     * @return added video folder
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder addVideoFolder(final ConfigurationFolder folder) {
        backendManager.addFolder(folder, VIDEO);
        return folder;
    }

    /**
     * Edit video folder.
     *
     * @param id     video folder id
     * @param folder video value
     * @return edited video folder
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder editVideoFolder(@PathParam("id") final String id, final ConfigurationFolder folder) {
        backendManager.editFolder(id, folder, VIDEO);
        return folder;
    }

    /**
     * Remove video folder.
     *
     * @param id video folder id to remove
     * @return removed video folder
     */
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder removeVideoFolder(@PathParam("id") final String id) {
        backendManager.removeFolder(id, VIDEO);
        return new ConfigurationFolder(id, null, null);
    }
}

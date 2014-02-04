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
import java.util.Collection;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.holmes.core.manager.media.model.RootNode.PICTURE;

/**
 * Handler for picture folders REST requests.
 */
@Path("/backend/pictureFolders")
public final class PictureFoldersHandler {

    private final BackendManager backendManager;

    /**
     * Instantiates a new picture folders handler.
     *
     * @param backendManager backend manager
     */
    @Inject
    public PictureFoldersHandler(final BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    /**
     * Get picture folders.
     *
     * @return picture folders
     */
    @GET
    @Produces(APPLICATION_JSON)
    public Collection<ConfigurationFolder> getPictureFolders() {
        return backendManager.getFolders(PICTURE);
    }

    /**
     * Get picture folder.
     *
     * @param id picture folder id
     * @return picture folder
     */
    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    public ConfigurationFolder getPictureFolder(@PathParam("id") final String id) {
        return backendManager.getFolder(id, PICTURE);
    }

    /**
     * Add picture folder.
     *
     * @param folder picture folder to add
     * @return added picture folder
     */
    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public ConfigurationFolder addPictureFolder(final ConfigurationFolder folder) {
        backendManager.addFolder(folder, PICTURE);
        return folder;
    }

    /**
     * Edit picture folder.
     *
     * @param id     picture folder id
     * @param folder picture value
     * @return edited picture folder
     */
    @PUT
    @Path("/{id}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public ConfigurationFolder editPictureFolder(@PathParam("id") final String id, final ConfigurationFolder folder) {
        backendManager.editFolder(id, folder, PICTURE);
        return folder;
    }

    /**
     * Remove picture folder.
     *
     * @param id picture folder id to remove
     * @return removed picture folder
     */
    @DELETE
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    public ConfigurationFolder removePictureFolder(@PathParam("id") final String id) {
        backendManager.removeFolder(id, PICTURE);
        return new ConfigurationFolder(id, null, null);
    }
}

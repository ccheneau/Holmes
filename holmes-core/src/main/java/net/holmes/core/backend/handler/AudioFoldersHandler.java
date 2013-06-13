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

import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.response.ConfigurationFolder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static net.holmes.core.media.model.RootNode.AUDIO;

/**
 * Handler for audio folders REST requests.
 */
@Path("/backend/audioFolders")
public final class AudioFoldersHandler {

    private final BackendManager backendManager;

    /**
     * Instantiates a new audio folders handler.
     *
     * @param backendManager backend manager
     */
    @Inject
    public AudioFoldersHandler(final BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    /**
     * Get audio folders.
     *
     * @return audio folders
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getAudioFolders() {
        return backendManager.getFolders(AUDIO);
    }

    /**
     * Get audio folder.
     *
     * @param id audio folder id
     * @return audio folder
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder getAudioFolder(@PathParam("id") final String id) {
        return backendManager.getFolder(id, AUDIO);
    }

    /**
     * Add audio folder.
     *
     * @param folder audio folder to add
     * @return added audio folder
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder addAudioFolder(final ConfigurationFolder folder) {
        backendManager.addFolder(folder, AUDIO);
        return folder;
    }

    /**
     * Edit audio folder.
     *
     * @param id     audio folder id
     * @param folder folder value
     * @return edited audio folder
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder editAudioFolder(@PathParam("id") final String id, final ConfigurationFolder folder) {
        backendManager.editFolder(id, folder, AUDIO);
        return folder;
    }

    /**
     * Remove audio folder.
     *
     * @param id audio folder id to remove
     * @return removed audio folder
     */
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder removeAudioFolder(@PathParam("id") final String id) {
        backendManager.removeFolder(id, AUDIO);
        return new ConfigurationFolder(id, null, null);
    }
}

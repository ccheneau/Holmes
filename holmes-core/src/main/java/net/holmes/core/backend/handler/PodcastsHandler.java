/**
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

import static net.holmes.common.media.RootNode.PODCAST;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.response.ConfigurationFolder;

/**
 * Handler for podcast REST requests.
 */
@Path("/backend/podcasts")
public final class PodcastsHandler {

    private final BackendManager backendManager;

    /**
     * Constructor.
     * @param backendManager
     *      backend manager
     */
    @Inject
    public PodcastsHandler(final BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    /**
     * Get podcasts.
     *
     * @return  podcasts
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getPodcasts() {
        return backendManager.getFolders(PODCAST);
    }

    /**
     * Get podcasts.
     * 
     * @param id
     *      podcasts id
     * @return podcasts
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder getPodcast(@PathParam("id") final String id) {
        return backendManager.getFolder(id, PODCAST);
    }

    /**
     * Add podcast.
     * 
     * @param folder
     *      podcast to add
     * @return added podcast
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder addPodcast(final ConfigurationFolder folder) {
        backendManager.addFolder(folder, PODCAST);
        return folder;
    }

    /**
     * Edit podcast.
     * 
     * @param id
     *      podcast id
     * @param folder
     *      podcast value
     * @return edited podcast
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder editPodcast(@PathParam("id") final String id, final ConfigurationFolder folder) {
        backendManager.editFolder(id, folder, PODCAST);
        return folder;
    }

    /**
     * Remove podcast.
     * 
     * @param id
     *      podcast id to remove
     * @return removed podcast
     */
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder removePodcast(@PathParam("id") final String id) {
        backendManager.removeFolder(id, PODCAST);
        return new ConfigurationFolder(id, null, null);
    }
}

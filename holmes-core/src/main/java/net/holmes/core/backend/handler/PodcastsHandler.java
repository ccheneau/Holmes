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
import static net.holmes.core.manager.media.model.RootNode.PODCAST;

/**
 * Handler for podcast REST requests.
 */
@Path("/backend/podcasts")
public final class PodcastsHandler {

    private final BackendManager backendManager;

    /**
     * Instantiates a new podcasts handler.
     *
     * @param backendManager backend manager
     */
    @Inject
    public PodcastsHandler(final BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    /**
     * Get podcasts.
     *
     * @return podcasts
     */
    @GET
    @Produces(APPLICATION_JSON)
    public Collection<ConfigurationFolder> getPodcasts() {
        return backendManager.getFolders(PODCAST);
    }

    /**
     * Get podcasts.
     *
     * @param id podcasts id
     * @return podcasts
     */
    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    public ConfigurationFolder getPodcast(@PathParam("id") final String id) {
        return backendManager.getFolder(id, PODCAST);
    }

    /**
     * Add podcast.
     *
     * @param folder podcast to add
     * @return added podcast
     */
    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public ConfigurationFolder addPodcast(final ConfigurationFolder folder) {
        backendManager.addFolder(folder, PODCAST);
        return folder;
    }

    /**
     * Edit podcast.
     *
     * @param id     podcast id
     * @param folder podcast value
     * @return edited podcast
     */
    @PUT
    @Path("/{id}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public ConfigurationFolder editPodcast(@PathParam("id") final String id, final ConfigurationFolder folder) {
        backendManager.editFolder(id, folder, PODCAST);
        return folder;
    }

    /**
     * Remove podcast.
     *
     * @param id podcast id to remove
     * @return removed podcast
     */
    @DELETE
    @Path("/{id}")
    @Produces(APPLICATION_JSON)
    public ConfigurationFolder removePodcast(@PathParam("id") final String id) {
        backendManager.removeFolder(id, PODCAST);
        return new ConfigurationFolder(id, null, null);
    }
}

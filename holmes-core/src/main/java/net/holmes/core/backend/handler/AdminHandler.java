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

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.response.IndexElement;

/**
 * Administration handler for REST requests.
 */
@Path("/backend/admin")
public final class AdminHandler {

    private final BackendManager backendManager;

    /**
     * Constructor.
     *
     * @param backendManager 
     */
    @Inject
    public AdminHandler(final BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    /**
     * Get media index elements.
     *
     * @return media index elements
     */
    @GET
    @Path("/indexElements")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<IndexElement> getMediaIndexElements() {
        return backendManager.getMediaIndexElements();
    }

    /**
     * Scan all medias.
     */
    @POST
    @Path("/scanAll")
    public void scanAllMedias() {
        backendManager.scanAllMedias();
    }
}

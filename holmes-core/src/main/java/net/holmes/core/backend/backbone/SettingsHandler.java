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

package net.holmes.core.backend.backbone;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.holmes.core.backend.backbone.response.Settings;

@Path("/backend/backbone/settings")
public class SettingsHandler {

    private final BackboneManager backboneManager;

    @Inject
    public SettingsHandler(BackboneManager backboneManager) {
        this.backboneManager = backboneManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Settings getSettings() {
        return backboneManager.getSettings();
    }

    @POST
    public void updateSettings(@FormParam("serverName") String serverName, @FormParam("httpServerPort") Integer httpServerPort,
            @FormParam("prependPodcastItem") Boolean prependPodcastItem) {
        backboneManager.updateSettings(new Settings(serverName, httpServerPort, prependPodcastItem));
    }
}

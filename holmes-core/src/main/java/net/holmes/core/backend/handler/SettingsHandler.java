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
import net.holmes.core.backend.response.Settings;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Handler for settings REST requests.
 */
@Path("/backend/settings")
public final class SettingsHandler {

    private final BackendManager backendManager;

    /**
     * Instantiates a new settings handler.
     *
     * @param backendManager backend manager
     */
    @Inject
    public SettingsHandler(final BackendManager backendManager) {
        this.backendManager = backendManager;
    }

    /**
     * Get settings.
     *
     * @return settings
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Settings getSettings() {
        return backendManager.getSettings();
    }

    /**
     * Save settings.
     *
     * @param settings settings to save
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Settings saveSettings(final Settings settings) {
        backendManager.saveSettings(settings);
        return settings;
    }
}

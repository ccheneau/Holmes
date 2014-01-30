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

package net.holmes.core.backend.exception;

import com.google.common.annotations.VisibleForTesting;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * Map backend exceptions to Http response.
 */
@Provider
public class BackendExceptionMapper implements ExceptionMapper<BackendException> {

    @Inject
    private ResourceBundle resourceBundle;

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(final BackendException e) {
        String entityMessage;
        try {
            entityMessage = resourceBundle.getString(e.getMessage());
        } catch (MissingResourceException ex) {
            entityMessage = e.getMessage();
        }

        return Response.status(BAD_REQUEST)//
                .type(TEXT_PLAIN) //
                .entity(entityMessage).build();
    }

    @VisibleForTesting
    void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
}

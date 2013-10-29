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

package net.holmes.core.backend.exception;

import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BackendExceptionMapperTest {

    @Test
    public void testBackendExceptionMapper() {
        BackendExceptionMapper backendExceptionMapper = new BackendExceptionMapper();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messageTest");
        backendExceptionMapper.setResourceBundle(resourceBundle);

        Response response = backendExceptionMapper.toResponse(new BackendException("messageKey"));
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getMediaType(), MediaType.TEXT_PLAIN_TYPE);
        assertEquals(response.getEntity().toString(), "messageValue");
    }

    @Test
    public void testBackendExceptionMapperBadMessageKey() {
        BackendExceptionMapper backendExceptionMapper = new BackendExceptionMapper();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messageTest");
        backendExceptionMapper.setResourceBundle(resourceBundle);

        Response response = backendExceptionMapper.toResponse(new BackendException("badMessageKey"));
        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getMediaType(), MediaType.TEXT_PLAIN_TYPE);
        assertEquals(response.getEntity().toString(), "badMessageKey");
    }
}

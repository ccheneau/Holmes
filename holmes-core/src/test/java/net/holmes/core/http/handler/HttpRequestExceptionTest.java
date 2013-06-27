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

package net.holmes.core.http.handler;


import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpRequestExceptionTest {

    @Test
    public void testHttpRequestException1() {
        HttpRequestException httpRequestException = new HttpRequestException("message", HttpResponseStatus.NOT_FOUND);
        assertNotNull(httpRequestException);
        assertEquals(HttpResponseStatus.NOT_FOUND, httpRequestException.getStatus());
        assertEquals("message", httpRequestException.getMessage());
    }

    @Test
    public void testHttpRequestException2() {
        Exception exception = new Exception("message");
        HttpRequestException httpRequestException = new HttpRequestException(exception, HttpResponseStatus.NOT_FOUND);
        assertNotNull(httpRequestException);
        assertEquals(HttpResponseStatus.NOT_FOUND, httpRequestException.getStatus());
        assertEquals(exception, httpRequestException.getCause());
    }
}

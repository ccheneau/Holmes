/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

package net.holmes.core.business.streaming.airplay.controlpoint;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;

public class AirplayCommandResponseTest {

    @Test
    public void testDecodeHttpResponse() {
        CommandResponse response = new CommandResponse();
        List<String> responseLines = newArrayList("HTTP/1.1 200 OK TEST", "Content-Type: text/parameters", "Content-Length : 10");
        response.decodeHttpResponse(responseLines);
        assertEquals(200, response.getCode());
        assertEquals("OK TEST", response.getMessage());
        assertEquals(10, response.getContentLength());
        assertEquals("text/parameters", response.getContentType());
        assertNotNull(response.toString());
    }

    @Test
    public void testDecodeResponseNoHeaders() {
        CommandResponse response = new CommandResponse();
        List<String> responseLines = newArrayList("HTTP/1.1 200 OK");
        response.decodeHttpResponse(responseLines);
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        assertEquals(0, response.getContentLength());
        assertNull(response.getContentType());
        assertNotNull(response.toString());
    }

    @Test
    public void testDecodeBadResponse() {
        CommandResponse response = new CommandResponse();
        List<String> responseLines = newArrayList("");
        response.decodeHttpResponse(responseLines);
        assertEquals(0, response.getCode());
        assertNull(response.getMessage());
        assertEquals(0, response.getContentLength());
        assertNull(response.getContentType());
        assertNotNull(response.toString());
    }

    @Test
    public void testDecodeContentParameters() {
        CommandResponse response = new CommandResponse();
        String content = "duration: 83.124794\nposition: 14.467000";
        response.decodeContentParameters(content);
        Map<String, String> parameters = response.getContentParameters();
        assertEquals("83.124794", parameters.get("duration"));
        assertEquals("14.467000", parameters.get("position"));
    }
}

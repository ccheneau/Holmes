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

package net.holmes.core.transport.airplay.controlpoint;

import com.google.common.collect.Lists;
import net.holmes.core.transport.airplay.command.Command;
import net.holmes.core.transport.airplay.device.AirplayDevice;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ControlPointTest {

    @Test
    public void testDecodeHttpResponse() {
        ControlPointTester controlPointTester = new ControlPointTester();
        List<String> responseLines = Lists.newArrayList("HTTP/1.1 200 OK", "Content-Type: text/parameters", "Content-Length: 10");
        ControlPoint.CommandHttpResponse httpResponse = controlPointTester.testHttpDecodeResponse(responseLines);
        assertEquals(200, httpResponse.getCode());
        assertEquals("OK", httpResponse.getMessage());
        assertEquals(10, httpResponse.getContentLength());
        assertEquals("text/parameters", httpResponse.getContentType());
        assertNotNull(httpResponse.toString());
    }

    @Test
    public void testDecodeResponseNoHeaders() {
        ControlPointTester controlPointTester = new ControlPointTester();
        List<String> responseLines = Lists.newArrayList("HTTP/1.1 200 OK");
        ControlPoint.CommandHttpResponse httpResponse = controlPointTester.testHttpDecodeResponse(responseLines);
        assertEquals(200, httpResponse.getCode());
        assertEquals("OK", httpResponse.getMessage());
        assertEquals(0, httpResponse.getContentLength());
        assertNull(httpResponse.getContentType());
        assertNotNull(httpResponse.toString());
    }

    @Test
    public void testDecodeContentParameters() {
        ControlPointTester controlPointTester = new ControlPointTester();
        String content = "duration: 83.124794\nposition: 14.467000";
        Map<String, String> parameters = controlPointTester.testDecodeContentParameters(content);
        assertEquals("83.124794", parameters.get("duration"));
        assertEquals("14.467000", parameters.get("position"));
    }

    @Test
    public void testCommandResponse() {
        ControlPointTester controlPointTester = new ControlPointTester();
        List<String> responseLines = Lists.newArrayList("HTTP/1.1 200 OK", "Content-Type: text/parameters", "Content-Length: 10");
        String content = "duration: 83.124794\nposition: 14.467000";
        ControlPoint.CommandHttpResponse httpResponse = controlPointTester.testHttpDecodeResponse(responseLines);
        Map<String, String> parameters = controlPointTester.testDecodeContentParameters(content);
        ControlPoint.CommandResponse response = controlPointTester.testCommandResponse(httpResponse, parameters);
        assertNotNull(response);
        assertNotNull(response.toString());
        assertEquals(2, response.getContentParameters().size());
        assertEquals(200, response.getHttpResponse().getCode());
    }

    private class ControlPointTester extends ControlPoint {

        @Override
        public void execute(AirplayDevice device, Command command) {

        }

        public CommandHttpResponse testHttpDecodeResponse(List<String> responseLines) {
            return decodeHttpResponse(responseLines);
        }

        public Map<String, String> testDecodeContentParameters(String content) {
            return decodeContentParameters(content);
        }

        public CommandResponse testCommandResponse(CommandHttpResponse httpResponse, Map<String, String> contentParameters) {
            return new CommandResponse(httpResponse, contentParameters);
        }
    }
}

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
    public void testDecodeResponse() {
        ControlPointTester controlPointTester = new ControlPointTester();
        List<String> responseLines = Lists.newArrayList("HTTP/1.1 200 OK", "Content-Type: text/parameters", "Content-Length: 10");
        ControlPoint.DeviceResponse deviceResponse = controlPointTester.testDecodeResponse(responseLines);
        assertEquals(200, deviceResponse.getCode());
        assertEquals("OK", deviceResponse.getMessage());
        assertEquals(10, deviceResponse.getContentLength());
        assertEquals("text/parameters", deviceResponse.getContentType());
        assertNotNull(deviceResponse.toString());
    }

    @Test
    public void testDecodeResponseNoHeaders() {
        ControlPointTester controlPointTester = new ControlPointTester();
        List<String> responseLines = Lists.newArrayList("HTTP/1.1 200 OK");
        ControlPoint.DeviceResponse deviceResponse = controlPointTester.testDecodeResponse(responseLines);
        assertEquals(200, deviceResponse.getCode());
        assertEquals("OK", deviceResponse.getMessage());
        assertEquals(0, deviceResponse.getContentLength());
        assertNull(deviceResponse.getContentType());
        assertNotNull(deviceResponse.toString());
    }

    @Test
    public void testDecodeContentParameters() {
        ControlPointTester controlPointTester = new ControlPointTester();
        String content = "duration: 83.124794\nposition: 14.467000";
        Map<String, String> parameters = controlPointTester.testDecodeContentParameters(content);
        assertEquals("83.124794", parameters.get("duration"));
        assertEquals("14.467000", parameters.get("position"));
    }

    private class ControlPointTester extends ControlPoint {

        @Override
        public void execute(AirplayDevice device, Command command) {

        }

        public DeviceResponse testDecodeResponse(List<String> responseLines) {
            return decodeResponse(responseLines);
        }

        public Map<String, String> testDecodeContentParameters(String content) {
            return decodeContentParameters(content);
        }
    }
}

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

package net.holmes.core.common;

import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertNotNull;

public class UpnpUtilsTest {

    @Test
    public void testTestPrivateConstructor() throws Exception {
        Constructor<UpnpUtils> cnt = UpnpUtils.class.getDeclaredConstructor();
        cnt.setAccessible(true);
        cnt.newInstance();
    }

    @Test
    public void testGetUpnpMimeType() {
        org.seamless.util.MimeType mimeType = UpnpUtils.getUpnpMimeType(MimeType.valueOf("image/png"));
        assertNotNull(mimeType);
    }

    @Test
    public void testGetConnectionManagerService() {
        LocalService<ConnectionManagerService> connectionManagerService = UpnpUtils.getConnectionManagerService();
        assertNotNull(connectionManagerService);
    }

    @Test
    public void testGetDeviceDetails() {
        DeviceDetails deviceDetails = UpnpUtils.getDeviceDetails("serverName", "1.0");
        assertNotNull(deviceDetails);
    }

    @Test
    public void testGetIcons() {
        Icon[] icons = UpnpUtils.getIcons();
        assertNotNull(icons);
    }
}

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

package net.holmes.core.backend.handler;

import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.response.Settings;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;

public class SettingsHandlerTest {

    @Test
    public void testGetSettings() {
        BackendManager backendManager = createMock(BackendManager.class);

        expect(backendManager.getSettings()).andReturn(new Settings("serverName", true)).atLeastOnce();

        replay(backendManager);
        SettingsHandler settingsHandler = new SettingsHandler(backendManager);
        assertNotNull(settingsHandler.getSettings());
        verify(backendManager);
    }

    @Test
    public void testSaveSettings() {
        BackendManager backendManager = createMock(BackendManager.class);
        Settings settings = new Settings("serverName", true);

        backendManager.saveSettings(settings);
        expectLastCall().atLeastOnce();

        replay(backendManager);
        SettingsHandler settingsHandler = new SettingsHandler(backendManager);
        settingsHandler.saveSettings(settings);
        verify(backendManager);
    }
}

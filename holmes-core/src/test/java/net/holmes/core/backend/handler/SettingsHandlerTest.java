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

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.holmes.core.backend.BackendManager;
import net.holmes.core.backend.response.Settings;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

public class SettingsHandlerTest {
    @Inject
    private BackendManager backendManager;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testGetSettings() {
        SettingsHandler settingsHandler = new SettingsHandler(backendManager);
        assertNotNull(settingsHandler.getSettings());
    }

    @Test
    public void testSaveSettings() {
        SettingsHandler settingsHandler = new SettingsHandler(backendManager);
        settingsHandler.saveSettings(new Settings("serverName", 2000, true, true));
    }
}

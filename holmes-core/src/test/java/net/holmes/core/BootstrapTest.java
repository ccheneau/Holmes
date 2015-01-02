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

package net.holmes.core;

import com.google.inject.Injector;
import net.holmes.core.backend.inject.BackendInjector;
import net.holmes.core.business.inject.BusinessInjector;
import net.holmes.core.common.inject.CommonInjector;
import net.holmes.core.service.inject.ServiceInjector;
import org.junit.Test;

import java.io.File;

import static com.google.inject.Guice.createInjector;
import static net.holmes.core.Bootstrap.loadLogging;
import static net.holmes.core.common.Constants.HOLMES_HOME_UI_DIRECTORY;
import static net.holmes.core.common.SystemProperty.HOLMES_HOME;
import static org.junit.Assert.assertNotNull;

public class BootstrapTest {

    @Test
    public void testCreateInjectors() {
        File uiPath = new File(HOLMES_HOME.getValue(), HOLMES_HOME_UI_DIRECTORY.toString());
        if (!uiPath.exists() && uiPath.mkdirs()) uiPath.deleteOnExit();

        Injector injector = createInjector(new CommonInjector(), new BusinessInjector(),
                new ServiceInjector(), new BackendInjector());

        assertNotNull(injector);
    }

    @Test
    public void testLoadLogging() {
        loadLogging(false);
    }

    @Test
    public void testLoadDebugLogging() {
        loadLogging(true);
    }
}

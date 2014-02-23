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

package net.holmes.core;

import com.google.inject.Guice;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;

import static net.holmes.core.common.SystemProperty.HOLMES_HOME;
import static org.junit.Assert.assertNotNull;

public class TestHolmesServerModule {

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testGetLocalIPV4() {
        assertNotNull(HolmesServerModule.getLocalAddress());
    }

    @Test
    public void testHolmesServerModule() {
        File uiPath = new File(HOLMES_HOME.getValue(), "ui");
        if (!uiPath.exists() && uiPath.mkdirs()) uiPath.deleteOnExit();

        HolmesServerModule module = new HolmesServerModule();
        assertNotNull(module);
        assertNotNull(Guice.createInjector(module));
    }
}

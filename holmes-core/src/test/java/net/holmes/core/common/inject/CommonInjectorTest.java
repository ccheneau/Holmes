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

package net.holmes.core.common.inject;

import net.holmes.core.common.exception.HolmesRuntimeException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.holmes.core.common.SystemProperty.HOLMES_HOME;
import static org.junit.Assert.assertNotNull;

public class CommonInjectorTest {
    @Test
    public void testGetLocalIPV4() throws IOException {
        assertNotNull(CommonInjector.getLocalAddress());
    }

    @Test
    public void testGetHolmesHomeSubDir() {
        File uiPath = new File(HOLMES_HOME.getValue(), "ui");
        if (!uiPath.exists() && uiPath.mkdirs()) uiPath.deleteOnExit();

        assertNotNull(CommonInjector.getHolmesHomeSubDirectory("ui"));
    }

    @Test(expected = HolmesRuntimeException.class)
    public void testGetBadHolmesHomeSubDir() {
        assertNotNull(CommonInjector.getHolmesHomeSubDirectory("bad_subDir"));
    }
}

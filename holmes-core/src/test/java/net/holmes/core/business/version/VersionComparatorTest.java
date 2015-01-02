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

package net.holmes.core.business.version;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VersionComparatorTest {

    @Test
    public void testVersionComparator() {
        VersionComparator vc = new VersionComparator();
        assertEquals(0, vc.compare(null, "V 1.2.3"));
        assertEquals(0, vc.compare("V 1.2.3", "alpha"));
        assertEquals(0, vc.compare("V 1.2.3", "1.2.3"));

        assertEquals(1, vc.compare("1", "0.99"));
        assertEquals(-1, vc.compare("V 0.6.3", "0.6.4"));
    }
}

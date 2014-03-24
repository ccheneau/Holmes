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

package net.holmes.core.business.version.release;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReleaseDaoImplTest {

    @Test
    public void testUpdateRelease() {
        String releaseApiUrl = this.getClass().getResource("/githubReleaseApi.json").toString();
        ReleaseDaoImpl releaseDao = new ReleaseDaoImpl();
        releaseDao.updateRelease(releaseApiUrl);

        Release latestRelease = releaseDao.getLatestRelease();
        assertNotNull(latestRelease);
        assertNotNull(latestRelease.toString());
        assertFalse(latestRelease.isDraft());
        assertNotNull(latestRelease.getName());
        assertNotNull(latestRelease.getUrl());
    }

    @Test
    public void testUpdateReleaseBadUrl() {
        String releaseApiUrl = "badUrl";
        ReleaseDaoImpl releaseDao = new ReleaseDaoImpl();
        releaseDao.updateRelease(releaseApiUrl);

        Release latestRelease = releaseDao.getLatestRelease();
        assertNotNull(latestRelease);
    }
}

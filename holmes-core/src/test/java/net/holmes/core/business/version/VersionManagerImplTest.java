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

package net.holmes.core.business.version;

import net.holmes.core.business.version.release.Release;
import net.holmes.core.business.version.release.ReleaseDao;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class VersionManagerImplTest {

    @Test
    public void testGetCurrentVersion() {
        ReleaseDao releaseDao = createMock(ReleaseDao.class);
        replay(releaseDao);

        VersionManagerImpl versionManager = new VersionManagerImpl(releaseDao);
        assertNull(versionManager.getCurrentVersion());

        verify(releaseDao);
    }

    @Test
    public void testGetRemoteReleaseInfo() {
        ReleaseDao releaseDao = createMock(ReleaseDao.class);

        Release release = new Release();
        release.setName("V 0.6.3");
        release.setUrl("url");
        release.setDraft(false);

        expect(releaseDao.getLatestRelease()).andReturn(release);

        replay(releaseDao);

        VersionManagerImpl versionManager = new VersionManagerImpl(releaseDao);
        ReleaseInfo releaseInfo = versionManager.getRemoteReleaseInfo();
        assertNotNull(releaseInfo);
        assertNotNull(releaseInfo.getName());
        assertNotNull(releaseInfo.getUrl());
        assertFalse(releaseInfo.isNeedsUpdate());

        verify(releaseDao);
    }

    @Test
    public void testUpdateRemoteReleaseInfo() {
        ReleaseDao releaseDao = createMock(ReleaseDao.class);

        releaseDao.updateRelease(isA(String.class));
        expectLastCall();

        replay(releaseDao);

        VersionManagerImpl versionManager = new VersionManagerImpl(releaseDao);
        versionManager.updateRemoteReleaseInfo();

        verify(releaseDao);
    }
}

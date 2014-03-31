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

import javax.inject.Inject;

import static net.holmes.core.common.Constants.HOLMES_GITHUB_RELEASE_API_URL;

/**
 * Holmes version manager implementation
 */
public class VersionManagerImpl implements VersionManager {
    private final ReleaseDao releaseDao;
    private final String currentVersion;

    /**
     * Instantiates a new version manager implementation.
     *
     * @param releaseDao release DAO
     */
    @Inject
    public VersionManagerImpl(final ReleaseDao releaseDao) {
        this.releaseDao = releaseDao;
        this.currentVersion = this.getClass().getPackage().getImplementationVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReleaseInfo getReleaseInfo() {
        Release latestRelease = releaseDao.getLatestRelease();
        boolean needsUpdate = new VersionComparator().compare(currentVersion, latestRelease.getName()) == -1;
        return new ReleaseInfo(latestRelease.getName(), needsUpdate, latestRelease.getUrl());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateReleaseInfo() {
        releaseDao.updateRelease(HOLMES_GITHUB_RELEASE_API_URL.toString());
    }
}

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

import com.google.inject.name.Named;
import net.holmes.core.business.version.release.Release;
import net.holmes.core.business.version.release.ReleaseDao;

import javax.inject.Inject;

import static net.holmes.core.common.Constants.HOLMES_GITHUB_RELEASE_API_URL;

/**
 * Holmes version manager implementation
 */
public final class VersionManagerImpl implements VersionManager {
    private final ReleaseDao releaseDao;
    private final String currentVersion;

    /**
     * Instantiates a new version manager implementation.
     *
     * @param releaseDao     release DAO
     * @param currentVersion current Holmes version
     */
    @Inject
    public VersionManagerImpl(final ReleaseDao releaseDao, @Named("currentVersion") final String currentVersion) {
        this.releaseDao = releaseDao;
        this.currentVersion = currentVersion;
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
    public ReleaseInfo getRemoteReleaseInfo() {
        Release latestRelease = releaseDao.getLatestRelease();
        boolean needsUpdate = new VersionComparator().compare(currentVersion, latestRelease.getName()) == -1;
        return new ReleaseInfo(latestRelease.getName(), needsUpdate, latestRelease.getUrl());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRemoteReleaseInfo() {
        releaseDao.updateRelease(HOLMES_GITHUB_RELEASE_API_URL.toString());
    }
}

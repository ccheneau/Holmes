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

package net.holmes.core.service.upnp.directory;

import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.SortCriterion;
import org.junit.Test;

import java.util.Arrays;

public class AbstractContentDirectoryServiceTest {

    @Test
    public void testBrowse() throws ContentDirectoryException {
        new AbstractContentDirectoryServiceTester().browse("0", "BrowseMetadata", "", new UnsignedIntegerFourBytes(0), new UnsignedIntegerFourBytes(1), "+name", null);
    }

    @Test(expected = ContentDirectoryException.class)
    public void testBrowseBadSortCriteria() throws ContentDirectoryException {
        new AbstractContentDirectoryServiceTester().browse("0", "BrowseMetadata", "", new UnsignedIntegerFourBytes(0), new UnsignedIntegerFourBytes(1), "bad_criteria", null);
    }

    @Test
    public void testSearch() throws ContentDirectoryException {
        new AbstractContentDirectoryServiceTester().search("0", "BrowseMetadata", "", new UnsignedIntegerFourBytes(0), new UnsignedIntegerFourBytes(1), "+name", null);
    }

    @Test(expected = ContentDirectoryException.class)
    public void testSearchBadSortCriteria() throws ContentDirectoryException {
        new AbstractContentDirectoryServiceTester().search("0", "BrowseMetadata", "", new UnsignedIntegerFourBytes(0), new UnsignedIntegerFourBytes(1), "bad_criteria", null);
    }

    private class AbstractContentDirectoryServiceTester extends AbstractContentDirectoryService {

        AbstractContentDirectoryServiceTester() {
            super(Arrays.asList("dc:title"), // search caps
                    Arrays.asList("dc:title", "dc:date")); // sort caps
        }

        @Override
        public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter, long firstResult, long maxResults, SortCriterion[] orderBy, RemoteClientInfo remoteClientInfo) throws ContentDirectoryException {
            return null;
        }

        @Override
        public BrowseResult search(String containerId, String searchCriteria, String filter, long firstResult, long maxResults, SortCriterion[] orderBy, RemoteClientInfo remoteClientInfo) throws ContentDirectoryException {
            return null;
        }
    }
}

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

package com.sun.syndication.feed;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class RomeTest {

    @Before
    public void setUp() {
    }

    /**
     * Test rome with cast coders rss.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testRomeCastCoders() {
        XmlReader reader = null;
        try {
            SyndFeedInput input = new SyndFeedInput();
            InputStream in = this.getClass().getResourceAsStream("/castCodersRss.xml");
            reader = new XmlReader(in);
            SyndFeed feed = input.build(reader);
            List<SyndEntry> entries = feed.getEntries();
            if (entries != null && !entries.isEmpty()) {
                for (SyndEntry entry : entries) {
                    if (entry.getEnclosures() != null && !entry.getEnclosures().isEmpty()) {
                        for (SyndEnclosure enclosure : (List<SyndEnclosure>) entry.getEnclosures()) {
                            assertNotNull(enclosure.getType());
                            assertNotNull(enclosure.getUrl());
                        }
                    }
                }
            }
        } catch (IOException | FeedException | IllegalArgumentException e) {
            fail(e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * Test rome with allocine faux raccords rss.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testRomeAllocineFauxRaccord() {
        XmlReader reader = null;
        try {
            SyndFeedInput input = new SyndFeedInput();
            InputStream in = this.getClass().getResourceAsStream("/allocineFauxRaccordRss.xml");
            reader = new XmlReader(in);
            SyndFeed feed = input.build(reader);
            List<SyndEntry> entries = feed.getEntries();
            if (entries != null && !entries.isEmpty()) {
                for (SyndEntry entry : entries) {
                    assertNotNull(entry.getTitle());
                    if (entry.getEnclosures() != null && !entry.getEnclosures().isEmpty()) {
                        for (SyndEnclosure enclosure : (List<SyndEnclosure>) entry.getEnclosures()) {
                            assertNotNull(enclosure.getType());
                            assertNotNull(enclosure.getUrl());
                        }
                    }
                }
            }
        } catch (IOException | IllegalArgumentException | FeedException e) {
            fail(e.getMessage());
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
    }
}

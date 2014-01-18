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

package com.sun.syndication.feed;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class RomeTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RomeTest.class);

    /**
     * Test rome with cast coders rss.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testRomeCastCoders() {
        try (XmlReader reader = new XmlReader(this.getClass().getResourceAsStream("/castCodersRss.xml"))) {
            SyndFeedInput input = new SyndFeedInput();
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
        }
    }

    /**
     * Test rome with allocine faux raccords rss.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testRomeAllocineFauxRaccord() {
        try (XmlReader reader = new XmlReader(this.getClass().getResourceAsStream("/allocineFauxRaccordRss.xml"))) {
            SyndFeedInput input = new SyndFeedInput();
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
        }
    }

    /**
     * Test rome with allocine faux raccords rss.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testMediaRss() {
        try (XmlReader reader = new XmlReader(this.getClass().getResourceAsStream("/mediaRss.xml"))) {
            SyndFeedInput input = new SyndFeedInput();
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
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test rome with allocine faux raccords rss.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testBadMediaRss() {
        try (XmlReader reader = new XmlReader(this.getClass().getResourceAsStream("/badMediaRss.xml"))) {
            SyndFeedInput input = new SyndFeedInput();
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
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}

/**
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
package net.holmes.core.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import junit.framework.TestCase;
import net.holmes.core.TestModule;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RomeTest extends TestCase {

    @Override
    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    /**
     * Test rome with cast coders rss.
     */
    @SuppressWarnings("unchecked")
    @Test
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
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        } catch (FeedException e) {
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
     * Test rome with allocine faux raccord rss.
     */
    @SuppressWarnings("unchecked")
    @Test
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
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        } catch (FeedException e) {
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

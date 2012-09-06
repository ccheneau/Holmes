/**
* Copyright (C) 2012  Cedric Cheneau
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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * The Class RomeTest.
 */
public class RomeTest extends TestCase {
    private static Logger logger = LoggerFactory.getLogger(RomeTest.class);

    /**
     * Test rome.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRome() {
        XmlReader reader = null;
        try {
            SyndFeedInput input = new SyndFeedInput();
            InputStream in = this.getClass().getResourceAsStream("/castCodersRss.xml");
            reader = new XmlReader(in);
            SyndFeed feed = input.build(reader);
            List<SyndEntry> entries = feed.getEntries();
            if (entries != null && !entries.isEmpty()) {
                for (SyndEntry entry : entries) {
                    logger.debug(entry.getTitle());
                    if (entry.getEnclosures() != null && !entry.getEnclosures().isEmpty()) {
                        for (SyndEnclosure enclosure : (List<SyndEnclosure>) entry.getEnclosures()) {
                            logger.debug(enclosure.getType());
                            logger.debug(enclosure.getUrl());
                        }
                    }
                    //                    EntryInformation itunesInfo = (EntryInformation) (entry.getModule(ITunes.URI));
                    //                    if (itunesInfo != null && itunesInfo.getDuration() != null) {
                    //                        logger.debug("duration: " + itunesInfo.getDuration().toString());
                    //                    }
                    //                    MediaEntryModule mediaInfo = (MediaEntryModule) (entry.getModule(MediaModule.URI));
                    //                    if (mediaInfo != null && mediaInfo.getMetadata() != null && mediaInfo.getMetadata().getThumbnail() != null) {
                    //                        logger.debug("iconUrl: " + mediaInfo.getMetadata().getThumbnail()[0].getUrl().toString());
                    //                    }
                }
            }
        }
        catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        catch (FeedException e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        finally {
            try {
                if (reader != null) reader.close();
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}

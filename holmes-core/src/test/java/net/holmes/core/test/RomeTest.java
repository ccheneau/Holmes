/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
public class RomeTest extends TestCase
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(RomeTest.class);

    /**
     * Test rome.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRome()
    {
        XmlReader reader = null;
        try
        {
            URL feedSource = new URL("http://lescastcodeurs.libsyn.com/rss");
            SyndFeedInput input = new SyndFeedInput();
            reader = new XmlReader(feedSource);
            SyndFeed feed = input.build(reader);
            List<SyndEntry> entries = feed.getEntries();
            if (entries != null && !entries.isEmpty())
            {
                for (SyndEntry entry : entries)
                {
                    logger.debug(entry.getTitle());
                    if (entry.getEnclosures() != null && !entry.getEnclosures().isEmpty())
                    {
                        for (SyndEnclosure enclosure : (List<SyndEnclosure>) entry.getEnclosures())
                        {
                            logger.debug(enclosure.getType());
                            logger.debug(enclosure.getUrl());
                        }
                    }
                }
            }
        }
        catch (MalformedURLException e)
        {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        catch (FeedException e)
        {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
        finally
        {
            try
            {
                if (reader != null) reader.close();
            }
            catch (IOException e)
            {
                logger.error(e.getMessage(), e);
            }
        }
    }
}

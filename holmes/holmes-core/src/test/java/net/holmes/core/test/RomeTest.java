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

public class RomeTest extends TestCase
{
    private static Logger logger = LoggerFactory.getLogger(RomeTest.class);

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

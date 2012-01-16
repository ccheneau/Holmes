package net.holmes.core.test;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import net.holmes.core.TestModule;
import net.holmes.core.service.IMediaService;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class MediaServiceTest extends TestCase
{
    private static Logger logger = LoggerFactory.getLogger(MediaServiceTest.class);

    @Inject
    private IMediaService mediaService;

    @Override
    @Before
    public void setUp()
    {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testScanAll()
    {
        try
        {
            mediaService.scanAll(true);

            assertNotNull(mediaService.getNodes());
            logger.debug(mediaService.getNodes().toString());
            assertFalse(mediaService.getNodes().isEmpty());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testScanAllTwice()
    {
        try
        {
            mediaService.scanAll(false);

            assertNotNull(mediaService.getNodes());
            Collection<String> nodeIds1 = new ArrayList<String>();
            assertFalse(mediaService.getNodes().isEmpty());
            for (String id : mediaService.getNodes().keySet())
            {
                nodeIds1.add(id);
            }

            mediaService.scanAll(false);

            assertNotNull(mediaService.getNodes());
            Collection<String> nodeIds2 = new ArrayList<String>();
            assertFalse(mediaService.getNodes().isEmpty());
            for (String id : mediaService.getNodes().keySet())
            {
                nodeIds2.add(id);
            }

            assertTrue(nodeIds1.containsAll(nodeIds2));
            assertTrue(nodeIds2.containsAll(nodeIds1));
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
}

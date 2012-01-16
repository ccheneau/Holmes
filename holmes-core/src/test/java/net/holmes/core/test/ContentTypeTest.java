package net.holmes.core.test;

import junit.framework.TestCase;
import net.holmes.core.TestModule;
import net.holmes.core.model.ContentType;
import net.holmes.core.model.IContentTypeFactory;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class ContentTypeTest extends TestCase
{
    private static Logger logger = LoggerFactory.getLogger(ContentTypeTest.class);

    @Inject
    private IContentTypeFactory contentTypeFactory;

    @Override
    @Before
    public void setUp()
    {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testContentType()
    {
        try
        {
            String fileName = "movie.avi";

            ContentType contentType = contentTypeFactory.getContentType(fileName);

            assertNotNull(contentType);
            logger.debug(contentType.toString());
            assertEquals("video", contentType.getType());
            assertEquals("video/x-msvideo", contentType.getContentType());
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void testWrongContentType()
    {
        try
        {
            String fileName = "movie.blabla";

            ContentType contentType = contentTypeFactory.getContentType(fileName);

            assertNull(contentType);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

}

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

/**
 * The Class ContentTypeTest.
 */
public class ContentTypeTest extends TestCase
{
    private static Logger logger = LoggerFactory.getLogger(ContentTypeTest.class);

    /** The content type factory. */
    @Inject
    private IContentTypeFactory contentTypeFactory;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    @Before
    public void setUp()
    {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    /**
     * Test content type.
     */
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

    /**
     * Test wrong content type.
     */
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

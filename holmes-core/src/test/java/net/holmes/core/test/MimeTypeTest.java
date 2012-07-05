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
import net.holmes.core.util.IMimeTypeFactory;
import net.holmes.core.util.MimeType;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class MimeTypeTest extends TestCase {
    private static Logger logger = LoggerFactory.getLogger(MimeTypeTest.class);

    @Inject
    private IMimeTypeFactory mimeTypeFactory;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    /**
     * Test mime type.
     */
    @Test
    public void testMimeType() {
        try {
            String fileName = "movie.avi";

            MimeType mimeType = mimeTypeFactory.getMimeType(fileName);

            assertNotNull(mimeType);
            logger.debug(mimeType.toString());
            assertEquals("video", mimeType.getType());
            assertEquals("video/x-msvideo", mimeType.getMimeType());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test wrong mime type.
     */
    @Test
    public void testWrongMimeType() {
        try {
            String fileName = "movie.blabla";

            MimeType mimeType = mimeTypeFactory.getMimeType(fileName);

            assertNull(mimeType);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

}

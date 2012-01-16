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
package net.holmes.core.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for creating ContentType objects.
 */
public final class ContentTypeFactoryImpl implements IContentTypeFactory
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(ContentTypeFactoryImpl.class);

    /** The props. */
    private Properties props = null;

    /**
     * Instantiates a new content type factory.
     */
    public ContentTypeFactoryImpl()
    {

        // Load mime types property file
        props = new Properties();
        InputStream in = null;
        try
        {
            in = this.getClass().getResourceAsStream("/mimetypes.properties");
            props.load(in);
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            try
            {
                if (in != null) in.close();
            }
            catch (IOException e)
            {
                // Nothing
            }
        }
    }

    /* (non-Javadoc)
     * @see net.holmes.core.model.IContentTypeFactory#getContentType(java.lang.String)
     */
    @Override
    public ContentType getContentType(String fileName)
    {

        // Get file extension
        String ext = "";
        int mid = fileName.lastIndexOf(".");
        ext = fileName.substring(mid + 1, fileName.length());

        // Get content type
        String mimeType = props.getProperty(ext);
        ContentType contentType = null;
        if (mimeType != null) contentType = new ContentType(mimeType);

        return contentType;
    }
}

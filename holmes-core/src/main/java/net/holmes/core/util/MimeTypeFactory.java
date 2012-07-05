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
package net.holmes.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MimeTypeFactory implements IMimeTypeFactory {
    private static Logger logger = LoggerFactory.getLogger(MimeTypeFactory.class);

    private Properties properties = null;

    public MimeTypeFactory() {
        // Load mime types from property file
        properties = new Properties();
        InputStream in = null;
        try {
            in = this.getClass().getResourceAsStream("/mimetypes.properties");
            properties.load(in);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            try {
                if (in != null) in.close();
            }
            catch (IOException e) {
                // Nothing
            }
        }
    }

    /* (non-Javadoc)
     * @see net.holmes.core.util.IMimeTypeFactory#getMimeType(java.lang.String)
     */
    @Override
    public MimeType getMimeType(String fileName) {
        // Get file extension
        String ext = "";
        int mid = fileName.lastIndexOf(".");
        ext = fileName.substring(mid + 1, fileName.length());

        // Get mime type
        MimeType mimeType = null;
        if (properties.getProperty(ext) != null) mimeType = new MimeType(properties.getProperty(ext));

        return mimeType;
    }
}

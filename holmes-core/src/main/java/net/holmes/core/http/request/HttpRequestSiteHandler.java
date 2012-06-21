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
package net.holmes.core.http.request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map.Entry;

import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.http.HttpServer;
import net.holmes.core.model.IContentTypeFactory;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * The Class HttpRequestSiteHandler.
 */
public final class HttpRequestSiteHandler implements IHttpRequestHandler
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(HttpRequestSiteHandler.class);

    /** The configuration. */
    @Inject
    private IConfiguration configuration;

    /** The content type factory. */
    @Inject
    private IContentTypeFactory contentTypeFactory;

    /** The home site directory. */
    private String homeSiteDirectory;

    /**
     * Instantiates a new http site handler.
     */
    public HttpRequestSiteHandler()
    {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#initHandler()
     */
    @Override
    @Inject
    public void initHandler()
    {
        homeSiteDirectory = configuration.getHomeSiteDirectory();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#processRequest(org.jboss.netty.handler.codec.http.HttpRequest, org.jboss.netty.channel.Channel)
     */
    @Override
    public void processRequest(HttpRequest request, Channel channel) throws HttpRequestException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("[START] processRequest");
            logger.debug("Request uri: " + request.getUri());
            for (Entry<String, String> entry : request.getHeaders())
            {
                logger.debug("Request header: " + entry.getKey() + " ==> " + entry.getValue());
            }
        }

        // Get file name
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        String fileName = decoder.getPath();
        if (fileName.equals("/"))
        {
            fileName = "/index.html";
        }

        if (fileName == null || fileName.trim().isEmpty())
        {
            throw new HttpRequestException("", HttpResponseStatus.NOT_FOUND);
        }

        String filePath = homeSiteDirectory + fileName;

        if (logger.isDebugEnabled()) logger.debug("file path:" + filePath);

        try
        {
            // Get file
            File file = new File(filePath);
            if (file == null || !file.exists() || !file.canRead() || file.isHidden())
            {
                logger.warn("resource not found:" + fileName);
                throw new HttpRequestException("", HttpResponseStatus.NOT_FOUND);
            }

            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long fileLength = raf.length();

            // Compute HttpHeader
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpHeaders.setContentLength(response, fileLength);
            response.setHeader(HttpHeaders.Names.SERVER, HttpServer.HTTP_SERVER_NAME);
            String contentType = contentTypeFactory.getContentType(fileName).getContentType();
            if (contentType != null)
            {
                response.setHeader(HttpHeaders.Names.CONTENT_TYPE, contentTypeFactory.getContentType(fileName).getContentType());
            }

            // Write the header.
            channel.write(response);

            // Write the content.
            ChannelFuture writeFuture = channel.write(new ChunkedFile(raf, 0, fileLength, 8192));

            // Decide whether to close the connection or not.
            if (!HttpHeaders.isKeepAlive(request))
            {
                // Close the connection when the whole content is written out.
                writeFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
        catch (FileNotFoundException fnfe)
        {
            logger.warn("resource not found:" + fileName);
            throw new HttpRequestException("", HttpResponseStatus.NOT_FOUND);
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
            throw new HttpRequestException("", HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        finally
        {
            if (logger.isDebugEnabled()) logger.debug("[END] processRequest");
        }
    }
}

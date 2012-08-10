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
package net.holmes.core.http.request;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.inject.Inject;

import net.holmes.core.http.HttpServer;
import net.holmes.core.util.HolmesHomeDirectory;
import net.holmes.core.util.mimetype.IMimeTypeFactory;

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

/**
 * Handler to serve pages of Holmes administration site
 */
public final class HttpRequestSiteHandler implements IHttpRequestHandler {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestSiteHandler.class);

    @Inject
    private IMimeTypeFactory mimeTypeFactory;

    public HttpRequestSiteHandler() {
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#canProcess(java.lang.String)
     */
    @Override
    public boolean canProcess(String requestPath) {
        return true;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.http.request.IHttpRequestHandler#processRequest(org.jboss.netty.handler.codec.http.HttpRequest, org.jboss.netty.channel.Channel)
     */
    @Override
    public void processRequest(HttpRequest request, Channel channel) throws HttpRequestException {
        if (logger.isDebugEnabled()) logger.debug("[START] processRequest");

        // Get file name
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        String fileName = decoder.getPath();
        if ("/".equals(fileName)) {
            fileName = "/index.html";
        }

        if (fileName == null || fileName.trim().isEmpty()) {
            throw new HttpRequestException("", HttpResponseStatus.NOT_FOUND);
        }

        String filePath = HolmesHomeDirectory.getSiteDirectory() + fileName;

        if (logger.isDebugEnabled()) logger.debug("file path:" + filePath);

        try {
            // Get file
            File file = new File(filePath);
            if (file == null || !file.exists() || !file.canRead() || file.isHidden()) {
                if (logger.isWarnEnabled()) logger.warn("resource not found:" + fileName);
                throw new HttpRequestException("", HttpResponseStatus.NOT_FOUND);
            }

            // Read the file
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            // Define response header
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpHeaders.setContentLength(response, raf.length());
            response.setHeader(HttpHeaders.Names.SERVER, HttpServer.HTTP_SERVER_NAME);
            String mimeType = mimeTypeFactory.getMimeType(fileName).getMimeType();
            if (mimeType != null) {
                response.setHeader(HttpHeaders.Names.CONTENT_TYPE, mimeType);
            }

            // Write the response.
            channel.write(response);

            // Write the file.
            ChannelFuture writeFuture = channel.write(new ChunkedFile(raf, 0, raf.length(), 8192));

            // Decide whether to close the connection or not.
            if (!HttpHeaders.isKeepAlive(request)) {
                // Close the connection when the whole content is written out.
                writeFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
        catch (FileNotFoundException e) {
            if (logger.isWarnEnabled()) logger.warn("resource not found:" + fileName);
            throw new HttpRequestException("", HttpResponseStatus.NOT_FOUND);
        }
        catch (IOException e) {
            if (logger.isErrorEnabled()) logger.error(e.getMessage(), e);
            throw new HttpRequestException("", HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        finally {
            if (logger.isDebugEnabled()) logger.debug("[END] processRequest");
        }
    }
}

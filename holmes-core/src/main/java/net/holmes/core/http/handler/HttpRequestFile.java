package net.holmes.core.http.handler;

import net.holmes.core.common.NodeFile;
import net.holmes.core.common.mimetype.MimeType;

/**
 * Http Requested file.
 */
public final class HttpRequestFile {
    private final NodeFile nodeFile;
    private final MimeType mimeType;

    /**
     * Instantiates a new RequestFile.
     *
     * @param nodeFile file
     * @param mimeType mime type
     */
    public HttpRequestFile(NodeFile nodeFile, MimeType mimeType) {
        this.nodeFile = nodeFile;
        this.mimeType = mimeType;
    }

    public NodeFile getNodeFile() {
        return nodeFile;
    }

    public MimeType getMimeType() {
        return mimeType;
    }
}
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
package net.holmes.core.media.index;

import java.util.UUID;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class MediaIndex implements IMediaIndex {

    private BiMap<String, String> nodeUUID;

    public MediaIndex() {
        nodeUUID = HashBiMap.create();
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.index.IMediaIndex#getValue(java.lang.String)
     */
    @Override
    public NodeValue getValue(String uuid) {
        NodeValue nodeValue = null;
        String value = nodeUUID.get(uuid);
        if (value != null) {
            String[] nodeParams = value.split("\\|");
            if (nodeParams != null && nodeParams.length == 3) {
                nodeValue = new NodeValue(nodeParams[0], nodeParams[1], nodeParams[2]);
            }
        }
        return nodeValue;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.index.IMediaIndex#getUUID(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getUUID(String parentId, String mediaType, String path) {
        String nodeValue = parentId + "|" + mediaType + "|" + path;
        String uuid = nodeUUID.inverse().get(nodeValue);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            nodeUUID.put(uuid, nodeValue);
        }
        return uuid;
    }
}

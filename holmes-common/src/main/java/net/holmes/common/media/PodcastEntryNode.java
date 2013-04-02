/**
* Copyright (C) 2012-2013  Cedric Cheneau
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
package net.holmes.common.media;

import net.holmes.common.mimetype.MimeType;

public final class PodcastEntryNode extends AbstractNode {
    private final MimeType mimeType;
    private final Long size;
    private final String url;
    private final String duration;

    public PodcastEntryNode(String id, String parentId, String name, MimeType mimeType, Long size, String url, String duration) {
        super(NodeType.TYPE_PODCAST_ENTRY, id, parentId, name);
        this.size = size;
        this.url = url;
        this.duration = duration;
        this.mimeType = mimeType;
    }

    public MimeType getMimeType() {
        return this.mimeType;
    }

    public Long getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public String getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PodcastEntryNode [mimeType=");
        builder.append(mimeType);
        builder.append(", size=");
        builder.append(size);
        builder.append(", url=");
        builder.append(url);
        builder.append(", duration=");
        builder.append(duration);
        builder.append(", id=");
        builder.append(id);
        builder.append(", parentId=");
        builder.append(parentId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", modifedDate=");
        builder.append(modifedDate);
        builder.append(", type=");
        builder.append(type);
        builder.append(", iconUrl=");
        builder.append(iconUrl);
        builder.append("]");
        return builder.toString();
    }
}

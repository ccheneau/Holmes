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
package net.holmes.core.media.node;

import java.io.File;
import java.util.Date;

import net.holmes.core.util.mimetype.MimeType;

public final class ContentNode extends AbstractNode {
    private final MimeType mimeType;
    private final Long size;
    private final String path;

    public ContentNode(String id, String parentId, String name, File file, MimeType mimeType) {
        super(NodeType.TYPE_CONTENT, id, parentId, name);
        this.path = file.getAbsolutePath();
        this.mimeType = mimeType;
        this.size = file.length();
        this.modifedDate = new Date(file.lastModified());
    }

    public MimeType getMimeType() {
        return this.mimeType;
    }

    public Long getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ContentNode [mimeType=");
        builder.append(mimeType);
        builder.append(", size=");
        builder.append(size);
        builder.append(", path=");
        builder.append(path);
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

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


public enum RootNode {
    ROOT("0", "-1", null), //
    VIDEO("1_VIDEOS", "0", MediaType.TYPE_VIDEO), //
    PICTURE("2_PICTURES", "0", MediaType.TYPE_IMAGE), //
    AUDIO("3_AUDIOS", "0", MediaType.TYPE_AUDIO), //
    PODCAST("4_PODCASTS", "0", MediaType.TYPE_PODCAST);

    private String id;
    private String parentId;
    private MediaType mediaType;

    private RootNode(String id, String parentId, MediaType mediaType) {
        this.id = id;
        this.parentId = parentId;
        this.mediaType = mediaType;
    }

    public String getId() {
        return this.id;
    }

    public String getParentId() {
        return this.parentId;
    }

    public MediaType getMediaType() {
        return this.mediaType;
    }

    public static RootNode getById(String id) {
        for (RootNode rootNode : RootNode.values()) {
            if (rootNode.id.equals(id)) return rootNode;
        }
        return null;
    }
}

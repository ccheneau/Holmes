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
package net.holmes.core.media;

import java.util.List;

import net.holmes.core.media.node.AbstractNode;

public interface IMediaService {
    public static final String ROOT_NODE_ID = "0";
    public static final String ROOT_VIDEO_NODE_ID = "1_VIDEOS";
    public static final String ROOT_PICTURE_NODE_ID = "2_PICTURES";
    public static final String ROOT_AUDIO_NODE_ID = "3_AUDIOS";
    public static final String ROOT_PODCAST_NODE_ID = "4_PODCASTS";

    public AbstractNode getNode(String nodeId);

    public List<AbstractNode> getChildNodes(AbstractNode parentNode);
}
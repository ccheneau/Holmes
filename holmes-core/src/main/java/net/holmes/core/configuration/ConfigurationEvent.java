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

package net.holmes.core.configuration;

import net.holmes.core.media.node.RootNode;

public class ConfigurationEvent {
    private final EventType type;
    private final ConfigurationNode node;
    private final RootNode rootNode;

    public ConfigurationEvent(EventType type, ConfigurationNode node, RootNode rootNode) {
        this.type = type;
        this.node = node;
        this.rootNode = rootNode;
    }

    public EventType getType() {
        return type;
    }

    public ConfigurationNode getNode() {
        return node;
    }

    public RootNode getRootNode() {
        return rootNode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConfigurationEvent [type=");
        builder.append(type);
        builder.append(", node=");
        builder.append(node);
        builder.append(", rootNode=");
        builder.append(rootNode);
        builder.append("]");
        return builder.toString();
    }

    public enum EventType {
        ADD, UPDATE, DELETE
    }
}

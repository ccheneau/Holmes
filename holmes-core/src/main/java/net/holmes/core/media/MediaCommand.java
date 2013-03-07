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

package net.holmes.core.media;

public class MediaCommand {
    private final CommandType type;
    private final String parameter;

    public MediaCommand(CommandType type, String parameter) {
        this.type = type;
        this.parameter = parameter;
    }

    public CommandType getType() {
        return type;
    }

    public String getParameter() {
        return parameter;
    }

    public enum CommandType {
        SCAN_ALL, SCAN_NODE;
    }

}

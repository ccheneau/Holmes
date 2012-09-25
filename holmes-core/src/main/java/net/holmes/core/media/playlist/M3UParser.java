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
package net.holmes.core.media.playlist;

import java.io.File;
import java.util.List;

public class M3UParser implements IPlaylistParser {

    private String playlist;

    public M3UParser(String playlist) {
        this.playlist = playlist;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.playlist.IPlaylistParser#parse()
     */
    @Override
    public List<PlaylistItem> parse() {
        File pl = new File(playlist);
        if (pl.exists()) return null;
        else return null;
    }
}

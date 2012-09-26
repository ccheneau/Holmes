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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class M3UParser implements IPlaylistParser {
    private static Logger logger = LoggerFactory.getLogger(M3UParser.class);

    private String playlist;

    public M3UParser(String playlist) {
        this.playlist = playlist;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.media.playlist.IPlaylistParser#parse()
     */
    @Override
    public List<PlaylistItem> parse() {
        List<PlaylistItem> items = Lists.newArrayList();
        File fPlaylist = new File(playlist);
        Charset charset = Files.getFileExtension(playlist).toLowerCase().equals("m3u") ? Charset.defaultCharset() : Charset.forName("UTF-8");
        try {
            List<String> lines = Files.readLines(fPlaylist, charset);
            for (String line : lines) {
                if (line.startsWith("#")) {

                } else if (line.trim().length() > 0) {

                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return items;
    }
}

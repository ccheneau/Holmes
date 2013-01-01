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
package net.holmes.core.media.playlist;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import net.holmes.core.util.inject.Loggable;

import org.slf4j.Logger;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

@Loggable
public class M3uParser implements PlaylistParser {
    private Logger logger;

    private final File playlist;

    public M3uParser(File playlist) {
        this.playlist = playlist;
    }

    @Override
    public List<PlaylistItem> parse() {
        List<PlaylistItem> items = null;
        Charset charset = Files.getFileExtension(playlist.getName()).toLowerCase().equals("m3u") ? Charset.defaultCharset() : Charset.forName("UTF-8");
        try {
            List<String> lines = Files.readLines(playlist, charset);
            if (lines != null && !lines.isEmpty()) {
                if (lines.get(0).trim().equals("#EXTM3U")) items = parseM3uExt(lines);
                else items = parseM3u(lines);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return items;
    }

    /**
     * Parse extended m3u
     */
    private List<PlaylistItem> parseM3uExt(List<String> lines) {
        List<PlaylistItem> items = Lists.newArrayList();
        File playlistDir = playlist.getParentFile();
        String currentLabel = null;
        for (String line : lines) {
            if (line.startsWith("#")) {
                if (line.startsWith("#EXTINF")) {
                    currentLabel = Iterables.getLast(Splitter.on(',').trimResults().omitEmptyStrings().split(line));
                }
            } else if (currentLabel != null && line.trim().length() > 0) {
                File item = new File(line.trim());
                boolean validFile = item.exists() && item.isFile();
                if (!validFile) {
                    item = new File(playlistDir, line.trim());
                    validFile = item.exists() && item.isFile();
                }
                if (validFile) items.add(new PlaylistItem(currentLabel, item.getAbsolutePath()));
                currentLabel = null;
            }
        }
        return items;
    }

    /**
     * Parse simple m3u playlist
     */
    private List<PlaylistItem> parseM3u(List<String> lines) {
        List<PlaylistItem> items = Lists.newArrayList();
        File playlistDir = playlist.getParentFile();
        for (String line : lines) {
            if (line.trim().length() > 0) {
                File item = new File(line.trim());
                boolean validFile = item.exists() && item.isFile();
                if (!validFile) {
                    item = new File(playlistDir, line.trim());
                    validFile = item.exists() && item.isFile();
                }
                if (validFile) items.add(new PlaylistItem(item.getName(), item.getAbsolutePath()));
            }
        }
        return items;
    }
}

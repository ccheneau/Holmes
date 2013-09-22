/*
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

package net.holmes.core.common;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

/**
 * Node file.
 */
public class NodeFile extends File {

    /**
     * Instantiates a new node file.
     *
     * @param path file path
     */
    public NodeFile(final String path) {
        super(path);
    }

    /**
     * Instantiates a new node file.
     *
     * @param parent The parent path name string
     * @param child  The child path name string
     */
    public NodeFile(final String parent, final String child) {
        super(parent, child);
    }

    /**
     * Check this is a readable file.
     *
     * @return true if this is a valid file
     */
    public boolean isValidFile() {
        return isFile() && canRead() && !isHidden();
    }

    /**
     * Check this is a readable directory.
     *
     * @return true if this is a valid directory
     */
    public boolean isValidDirectory() {
        return isDirectory() && canRead() && !isHidden();
    }

    /**
     * List readable child folders and files.
     *
     * @param includeFiles include file list in result
     * @return child files
     */
    public List<File> listChildFiles(boolean includeFiles) {
        List<File> fileList = Lists.newArrayList();
        File[] files = listFiles();
        if (files != null)
            for (File file : files)
                if (file.canRead() && !file.isHidden())
                    if (includeFiles && file.isFile())
                        fileList.add(file);
                    else if (file.isDirectory() && file.listFiles() != null)
                        fileList.add(file);

        return fileList;
    }
}

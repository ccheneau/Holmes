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

package net.holmes.core.common.mimetype;

import com.google.common.io.Files;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Mime type manager implementation.
 */
public final class MimeTypeManagerImpl implements MimeTypeManager {
    private final Properties properties;

    /**
     * Instantiates a new mime type manager implementation.
     */
    public MimeTypeManagerImpl() {
        // Load mime types from property file
        properties = new Properties();
        InputStream in = null;
        try {
            in = this.getClass().getResourceAsStream("/mimetypes.properties");
            properties.load(in);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public MimeType getMimeType(final String fileName) {
        // Get file extension
        String ext = Files.getFileExtension(fileName).toLowerCase();

        // Get mime type
        return properties.getProperty(ext) == null ? null : new MimeType(properties.getProperty(ext));
    }
}
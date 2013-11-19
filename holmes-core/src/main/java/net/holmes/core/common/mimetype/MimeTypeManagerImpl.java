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

import com.google.common.base.Strings;
import com.google.common.io.Files;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Mime type manager implementation.
 */
public final class MimeTypeManagerImpl implements MimeTypeManager {
    private final Properties properties;

    /**
     * Instantiates a new mime type manager implementation.
     */
    @Inject
    public MimeTypeManagerImpl(@Named("mimeTypePath") final String mimeTypePath) {
        // Load mime types from property file
        properties = new Properties();
        try (InputStream in = this.getClass().getResourceAsStream(mimeTypePath)) {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MimeType getMimeType(final String fileName) {
        // Get file extension
        String ext = Files.getFileExtension(fileName).toLowerCase();

        // Get mime type
        return properties.getProperty(ext) == null ? null : new MimeType(properties.getProperty(ext));
    }

    @Override
    public boolean isMimeTypeCompliant(MimeType mimeType, List<String> availableMimeTypes) {
        String aliasMimeType = (mimeType != null ? (String) properties.get(mimeType.getMimeType()) : null);
        return mimeType == null || Strings.isNullOrEmpty(mimeType.getMimeType())
                || availableMimeTypes == null || availableMimeTypes.isEmpty()
                || availableMimeTypes.contains(mimeType.getMimeType())
                || (aliasMimeType != null && availableMimeTypes.contains(aliasMimeType));
    }
}

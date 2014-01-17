/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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
        return properties.getProperty(ext) == null ? null : MimeType.valueOf(properties.getProperty(ext));
    }

    @Override
    public boolean isMimeTypeCompliant(final MimeType mimeType, final List<String> availableMimeTypes) {
        MimeType aliasMimeType = getAliasMimeType(mimeType);
        return mimeType == null || Strings.isNullOrEmpty(mimeType.getMimeType())
                || mimeType.isCompliant(availableMimeTypes)
                || (aliasMimeType != null && aliasMimeType.isCompliant(availableMimeTypes));
    }

    /**
     * Get alias mime type.
     *
     * @param mimeType mime type
     * @return alias mime type or null
     */
    private MimeType getAliasMimeType(final MimeType mimeType) {
        return mimeType == null || properties.getProperty(mimeType.getMimeType()) == null ? null : MimeType.valueOf(properties.getProperty(mimeType.getMimeType()));
    }
}

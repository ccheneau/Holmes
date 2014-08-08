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

package net.holmes.core.business.mimetype.dao;

import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.common.exception.HolmesRuntimeException;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.io.Files.getFileExtension;

/**
 * Mime type dao implementation.
 */
public final class MimeTypeDaoImpl implements MimeTypeDao {
    private final Properties properties;

    /**
     * Instantiates a new mime type dao implementation.
     *
     * @param mimeTypePath mime type property file path
     */
    @Inject
    public MimeTypeDaoImpl(@Named("mimeTypePath") final String mimeTypePath) {
        // Load mime types from property file
        try (InputStream in = this.getClass().getResourceAsStream(mimeTypePath)) {
            if (in == null) {
                throw new IOException("Invalid mimeTypePath:" + mimeTypePath);
            }
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new HolmesRuntimeException(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeType getMimeType(String fileName) {
        // Get file extension
        String ext = getFileExtension(fileName).toLowerCase();

        // Get mime type
        return properties.getProperty(ext) == null ? null : MimeType.valueOf(properties.getProperty(ext));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeType getAliasMimeType(MimeType mimeType) {
        return properties.getProperty(mimeType.getMimeType()) == null ? null : MimeType.valueOf(properties.getProperty(mimeType.getMimeType()));
    }
}

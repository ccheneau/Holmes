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

package net.holmes.core.manager.mimetype;

import net.holmes.core.common.MimeType;

import java.util.List;

/**
 * Mime type manager.
 */
public interface MimeTypeManager {

    /**
     * Gets mime type.
     *
     * @param fileName file name
     * @return mime type
     */
    MimeType getMimeType(String fileName);

    /**
     * Check mime type is compliant with available mimetypes.
     *
     * @param mimeType           mime type to check
     * @param availableMimeTypes list of available mime types
     * @return true if mime type is compliant
     */
    boolean isMimeTypeCompliant(MimeType mimeType, List<String> availableMimeTypes);
}

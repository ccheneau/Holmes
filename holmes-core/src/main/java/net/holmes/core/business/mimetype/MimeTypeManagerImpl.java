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

package net.holmes.core.business.mimetype;

import net.holmes.core.business.mimetype.dao.MimeTypeDao;
import net.holmes.core.business.mimetype.model.MimeType;

import javax.inject.Inject;
import java.util.Collection;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Mime type manager implementation.
 */
public final class MimeTypeManagerImpl implements MimeTypeManager {
    private final MimeTypeDao mimeTypeDao;

    /**
     * Instantiates a new mime type manager implementation.
     *
     * @param mimeTypeDao mime type DAO
     */
    @Inject
    public MimeTypeManagerImpl(final MimeTypeDao mimeTypeDao) {
        this.mimeTypeDao = mimeTypeDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeType getMimeType(final String fileName) {
        return mimeTypeDao.getMimeType(fileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMimeTypeCompliant(final MimeType mimeType, final Collection<String> availableMimeTypes) {
        return mimeType == null || isNullOrEmpty(mimeType.getMimeType())
                || isCompliant(mimeType, availableMimeTypes)
                || isAliasMimeTypeCompliant(mimeType, availableMimeTypes);
    }

    /**
     * Check if alias mime type is compliant with available mime types
     *
     * @param mimeType           original mime type
     * @param availableMimeTypes available mime types
     * @return true if alias mime type is compliant
     */
    private boolean isAliasMimeTypeCompliant(final MimeType mimeType, final Collection<String> availableMimeTypes) {
        MimeType aliasMimeType = mimeTypeDao.getAliasMimeType(mimeType);
        return aliasMimeType != null && isCompliant(aliasMimeType, availableMimeTypes);
    }

    /**
     * Check mime type is compliant with available mime types.
     *
     * @param mimeType           mime type
     * @param availableMimeTypes available mime type strings video/avi (mime type may contains wildcard)
     * @return true is mime type is compliant
     */
    private boolean isCompliant(final MimeType mimeType, final Collection<String> availableMimeTypes) {
        if (availableMimeTypes == null || availableMimeTypes.isEmpty() || availableMimeTypes.contains(mimeType.getMimeType())) {
            return true;
        } else {
            for (String availableMimeType : availableMimeTypes) {
                if ("*/*".equals(availableMimeType) || availableMimeType.equals(mimeType.getType().getValue() + "/*")) {
                    return true;
                }
            }
        }
        return false;
    }


}

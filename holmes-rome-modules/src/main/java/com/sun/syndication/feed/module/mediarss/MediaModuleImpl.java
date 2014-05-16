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

package com.sun.syndication.feed.module.mediarss;

import com.sun.syndication.feed.module.ModuleImpl;
import com.sun.syndication.feed.module.mediarss.types.Metadata;
import org.slf4j.Logger;

import static com.sun.syndication.feed.module.RssModule.MEDIA_RSS_URI;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class represents feed/channel level elements for MediaRSS
 *
 * @author cooper
 */
public class MediaModuleImpl extends ModuleImpl implements MediaModule {
    private static final Logger LOGGER = getLogger(MediaModuleImpl.class);

    private Metadata metadata;

    /**
     * Creates a new instance of MediaModuleImpl
     */
    public MediaModuleImpl() {
        super(MediaModule.class, MEDIA_RSS_URI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<MediaModule> getInterface() {
        return MediaModule.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetadata(final Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getThumbnailUrl() {
        return metadata != null && !metadata.getThumbnails().isEmpty() ?
                metadata.getThumbnails().get(0).getUrl().toString() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUri() {
        return MEDIA_RSS_URI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        MediaModule mediaModule = new MediaModuleImpl();
        mediaModule.setMetadata((Metadata) metadata.clone());
        return mediaModule;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copyFrom(final Object obj) {
        MediaModule mediaModule = (MediaModule) obj;
        try {
            this.metadata = (Metadata) mediaModule.getMetadata().clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.error(e.getMessage(), e);
            this.metadata = null;
        }
    }
}

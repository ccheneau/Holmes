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

/**
 * This class represents feed/channel level elements for MediaRSS
 *
 * @author cooper
 */
public class MediaModuleImpl extends ModuleImpl implements MediaModule {
    private static final long serialVersionUID = 1506805082848531979L;

    private Metadata metadata;

    /**
     * Creates a new instance of MediaModuleImpl
     */
    public MediaModuleImpl() {
        this(MediaModule.class);
    }

    /**
     * constructor that passes values up to ModuleImpl.
     *
     * @param clazz the clazz
     */
    MediaModuleImpl(final Class<?> clazz) {
        super(clazz, MediaModule.URI);
    }

    @Override
    public Class<?> getInterface() {
        return MediaModule.class;
    }

    /**
     * Metadata for a feed.
     *
     * @param metadata Metadata for a feed.
     */
    public void setMetadata(final Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Metadata for a feed.
     *
     * @return Metadata for a feed.
     */
    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public String getUri() {
        return MediaModule.URI;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        MediaModuleImpl m = new MediaModuleImpl();
        m.setMetadata((Metadata) metadata.clone());
        return m;
    }

    @Override
    public void copyFrom(final Object obj) {
        MediaModule m = (MediaModule) obj;
        try {
            this.metadata = (Metadata) m.getMetadata().clone();
        } catch (CloneNotSupportedException e) {
            this.metadata = null;
        }
    }
}

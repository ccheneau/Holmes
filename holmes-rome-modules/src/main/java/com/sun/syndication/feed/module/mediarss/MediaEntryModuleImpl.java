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

package com.sun.syndication.feed.module.mediarss;

import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.module.mediarss.types.MediaContent;
import com.sun.syndication.feed.module.mediarss.types.MediaGroup;
import com.sun.syndication.feed.module.mediarss.types.Metadata;

/**
 * Represents information for an Entry/Item level.
 *
 * @author Nathanial X. Freitas
 */
public class MediaEntryModuleImpl extends MediaModuleImpl implements MediaEntryModule {
    private static final long serialVersionUID = -1564409507033924835L;

    /*
     * the variables in the MediaModule are set when they apply to
     * all MediaContent instances in the set
     */
    private MediaContent[] mediaContents = new MediaContent[0];
    private MediaGroup[] mediaGroups = new MediaGroup[0];

    /**
     * Creates a new instance.
     */
    public MediaEntryModuleImpl() {
        super(MediaEntryModule.class);
    }

    /**
     * MediaContent items for the entry
     *
     * @param mediaContents MediaContent items for the entry
     */
    public void setMediaContents(final MediaContent[] mediaContents) {
        this.mediaContents = mediaContents == null ? new MediaContent[0] : mediaContents;
    }

    /**
     * MediaContent items for the entry
     *
     * @return MediaContent items for the entry
     */
    @Override
    public MediaContent[] getMediaContents() {
        return mediaContents;
    }

    /**
     * MediaGroups for the entry
     *
     * @param mediaGroups MediaGroups for the entry
     */
    public void setMediaGroups(final MediaGroup[] mediaGroups) {
        this.mediaGroups = mediaGroups == null ? new MediaGroup[0] : mediaGroups;
    }

    /**
     * MediaGroups for the entry
     *
     * @return MediaGroups for the entry
     */
    @Override
    public MediaGroup[] getMediaGroups() {
        return mediaGroups;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        MediaEntryModuleImpl m = new MediaEntryModuleImpl();
        m.setMediaContents(mediaContents.clone());
        m.setMediaGroups(mediaGroups.clone());
        m.setMetadata((getMetadata() == null) ? null : (Metadata) getMetadata().clone());
        m.setPlayer(getPlayer());

        return m;
    }

    @Override
    public boolean equals(final Object obj) {
        EqualsBean eBean = new EqualsBean(MediaEntryModuleImpl.class, this);
        return eBean.beanEquals(obj);
    }

    @Override
    public int hashCode() {
        EqualsBean equals = new EqualsBean(MediaEntryModuleImpl.class, this);
        return equals.beanHashCode();
    }
}

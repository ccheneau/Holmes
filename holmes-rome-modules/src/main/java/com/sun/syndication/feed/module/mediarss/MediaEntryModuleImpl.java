/*
 * Copyright 2006 Nathanial X. Freitas, openvision.tv
 *
 * This code is currently released under the Mozilla Public License.
 * http://www.mozilla.org/MPL/
 *
 * Alternately you may apply the terms of the Apache Software License
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.feed.module.mediarss;

import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ToStringBean;
import com.sun.syndication.feed.module.mediarss.types.MediaContent;
import com.sun.syndication.feed.module.mediarss.types.MediaGroup;
import com.sun.syndication.feed.module.mediarss.types.Metadata;

/**
 * Represents information for an Entry/Item level.
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
        super(MediaEntryModule.class, MediaModule.URI);
    }

    /**
     * MediaContent items for the entry
     * @param mediaContents MediaContent items for the entry
     */
    public void setMediaContents(final MediaContent[] mediaContents) {
        this.mediaContents = (mediaContents == null) ? new MediaContent[0] : mediaContents;
    }

    /**
     * MediaContent items for the entry
     * @return MediaContent items for the entry
     */
    @Override
    public MediaContent[] getMediaContents() {
        return mediaContents;
    }

    /**
     * MediaGroups for the entry
     * @param mediaGroups MediaGroups for the entry
     */
    public void setMediaGroups(final MediaGroup[] mediaGroups) {
        this.mediaGroups = (mediaGroups == null) ? new MediaGroup[0] : mediaGroups;
    }

    /**
     * MediaGroups for the entry
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

    @Override
    public String toString() {
        ToStringBean tsBean = new ToStringBean(MediaEntryModuleImpl.class, this);
        return tsBean.toString();
    }
}

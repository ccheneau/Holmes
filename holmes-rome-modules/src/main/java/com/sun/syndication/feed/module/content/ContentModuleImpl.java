/*
 * ContentModuleImpl.java
 *
 * Created on January 11, 2005, 1:07 PM
 *
 *  Copyright (C) 2005  Robert Cooper, Temple of the Screaming Penguin
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License or the Apache
 * License at your discretion.
 *
 *  Copyright (C) 2005  Robert Cooper, Temple of the Screaming Penguin
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
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
 *
 */
package com.sun.syndication.feed.module.content;

import java.util.ArrayList;
import java.util.List;

/**
 * @version $Revision: 1.4 $
 * @author  <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class ContentModuleImpl extends com.sun.syndication.feed.module.ModuleImpl implements ContentModule {
    private static final long serialVersionUID = 5321734934971547821L;

    private List<String> encodeds;
    private List<String> contents;
    private List<ContentItem> contentItems;

    public ContentModuleImpl() {
        super(ContentModuleImpl.class, URI);
    }

    protected ContentModuleImpl(java.lang.Class<?> beanClass, java.lang.String uri) {
        super(beanClass, uri);
    }

    @Override
    public List<String> getEncodeds() {
        this.encodeds = this.encodeds == null ? new ArrayList<String>() : this.encodeds;
        return this.encodeds;
    }

    @Override
    public void setEncodeds(List<String> encodeds) {
        this.encodeds = encodeds;
    }

    @Override
    public void copyFrom(Object obj) {
        ContentModule cm = (ContentModule) obj;
        this.setEncodeds(cm.getEncodeds());
        this.setContentItems(cm.getContentItems());
        this.setContents(cm.getContents());
    }

    @Override
    public Class<?> getInterface() {
        return ContentModule.class;
    }

    @Override
    public List<ContentItem> getContentItems() {
        this.contentItems = this.contentItems == null ? new ArrayList<ContentItem>() : this.contentItems;
        return this.contentItems;
    }

    @Override
    public void setContentItems(List<ContentItem> list) {
        this.contentItems = list;
    }

    @Override
    public List<String> getContents() {
        this.contents = this.contents == null ? new ArrayList<String>() : this.contents;
        return this.contents;
    }

    @Override
    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    @Override
    public String toString(String str) {
        return contentItems.toString();
    }
}

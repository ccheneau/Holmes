/*
 * ContentItem.java
 *
 * Created on January 12, 2005, 8:52 AM
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

import java.io.Serializable;
import java.util.List;

import org.jdom.Content;
import org.jdom.Namespace;

/** This class represents a content item per the "Original Syntax".
 * http://purl.org/rss/1.0/modules/content/
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class ContentItem implements Cloneable, Serializable {
    private static final long serialVersionUID = -7770793252868977275L;

    private String contentFormat;
    private String contentEncoding;
    private String contentValue;
    private List<Content> contentValueDOM;
    private String contentAbout;
    private String contentValueParseType;
    private transient List<Namespace> contentValueNamespace;
    private String contentResource;

    /**
     * Creates a new instance of ContentItem.
     */
    public ContentItem() {
        contentFormat = null;
        contentEncoding = null;
        contentValue = null;
        contentAbout = null;
        contentValueParseType = null;
        contentResource = null;
        contentValueNamespace = null;
        contentValueDOM = null;
    }

    /**
     * Gets the content format.
     *
     * @return the content format
     */
    public String getContentFormat() {
        return this.contentFormat;
    }

    /**
     * Sets the content format.
     *
     * @param contentFormat the new content format
     */
    public void setContentFormat(final String contentFormat) {
        this.contentFormat = contentFormat;
    }

    /**
     * Gets the content encoding.
     *
     * @return the content encoding
     */
    public String getContentEncoding() {
        return this.contentEncoding;
    }

    /**
     * Sets the content encoding.
     *
     * @param contentEncoding the new content encoding
     */
    public void setContentEncoding(final String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /**
     * Gets the content value.
     *
     * @return the content value
     */
    public String getContentValue() {
        return this.contentValue;
    }

    /**
     * Sets the content value.
     *
     * @param contentValue the new content value
     */
    public void setContentValue(final String contentValue) {
        this.contentValue = contentValue;
    }

    /**
     * Gets the content value dom.
     *
     * @return the content value dom
     */
    public List<Content> getContentValueDOM() {
        return this.contentValueDOM;
    }

    /**
     * Sets the content value dom.
     *
     * @param contentValueDOM the new content value dom
     */
    public void setContentValueDOM(final List<Content> contentValueDOM) {
        this.contentValueDOM = contentValueDOM;
    }

    /**
     * Gets the content about.
     *
     * @return the content about
     */
    public String getContentAbout() {
        return this.contentAbout;
    }

    /**
     * Sets the content about.
     *
     * @param contentAbout the new content about
     */
    public void setContentAbout(final String contentAbout) {
        this.contentAbout = contentAbout;
    }

    /**
     * Gets the content value parse type.
     *
     * @return the content value parse type
     */
    public String getContentValueParseType() {
        return this.contentValueParseType;
    }

    /**
     * Sets the content value parse type.
     *
     * @param contentValueParseType the new content value parse type
     */
    public void setContentValueParseType(final String contentValueParseType) {
        this.contentValueParseType = contentValueParseType;
    }

    /**
     * Gets the content value namespaces.
     *
     * @return the content value namespaces
     */
    public List<Namespace> getContentValueNamespaces() {
        return this.contentValueNamespace;
    }

    /**
     * Sets the content value namespaces.
     *
     * @param newCcontentValueNamespace the new content value namespaces
     */
    public void setContentValueNamespaces(final List<Namespace> newCcontentValueNamespace) {
        this.contentValueNamespace = newCcontentValueNamespace;
    }

    /**
     * Gets the content resource.
     *
     * @return the content resource
     */
    public String getContentResource() {
        return this.contentResource;
    }

    /**
     * Sets the content resource.
     *
     * @param contentResource the new content resource
     */
    public void setContentResource(final String contentResource) {
        this.contentResource = contentResource;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof ContentItem) {
            ContentItem test = (ContentItem) o;
            return test.contentFormat.equals(contentFormat) && test.contentEncoding.equals(contentEncoding) && test.contentValue.equals(contentValue)
                    && test.contentAbout.equals(contentAbout) && test.contentValueParseType.equals(contentValueParseType)
                    && test.contentValueNamespace.equals(contentValueNamespace) && test.contentResource.equals(contentResource);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contentAbout == null) ? 0 : contentAbout.hashCode());
        result = prime * result + ((contentEncoding == null) ? 0 : contentEncoding.hashCode());
        result = prime * result + ((contentFormat == null) ? 0 : contentFormat.hashCode());
        result = prime * result + ((contentResource == null) ? 0 : contentResource.hashCode());
        result = prime * result + ((contentValue == null) ? 0 : contentValue.hashCode());
        result = prime * result + ((contentValueDOM == null) ? 0 : contentValueDOM.hashCode());
        result = prime * result + ((contentValueNamespace == null) ? 0 : contentValueNamespace.hashCode());
        result = prime * result + ((contentValueParseType == null) ? 0 : contentValueParseType.hashCode());
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        ContentItem o = new ContentItem();
        o.contentAbout = this.contentAbout;
        o.contentEncoding = this.contentEncoding;
        o.contentFormat = this.contentFormat;
        o.contentResource = this.contentResource;
        o.contentValue = this.contentValue;
        o.contentValueDOM = this.contentValueDOM;
        o.contentValueNamespace = this.contentValueNamespace;
        o.contentValueParseType = this.contentValueParseType;

        return o;
    }
}

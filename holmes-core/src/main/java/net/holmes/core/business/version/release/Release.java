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

package net.holmes.core.business.version.release;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Represents a Github release of Holmes
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Release {
    private String name;
    @JsonProperty("html_url")
    private String url;
    private boolean draft;

    /**
     * Default constructor
     */
    public Release() {
    }

    /**
     * Instantiates a new Release
     *
     * @param name  release name
     * @param url   release URL
     * @param draft whether release is a draft
     */
    public Release(final String name, String url, final boolean draft) {
        this.name = name;
        this.url = url;
        this.draft = draft;
    }

    /**
     * Get release name.
     *
     * @return release name
     */
    public String getName() {
        return name;
    }

    /**
     * Set release name.
     *
     * @param name release name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get release URL.
     *
     * @return release URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set release URL.
     *
     * @param url release URL
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Check if release is a draft.
     *
     * @return true if release is a draft
     */
    public boolean isDraft() {
        return draft;
    }

    /**
     * Set whether release is a draft.
     *
     * @param draft draft release
     */
    public void setDraft(final boolean draft) {
        this.draft = draft;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this)
                .add("name", name)
                .add("url", url)
                .add("draft", draft)
                .toString();
    }
}

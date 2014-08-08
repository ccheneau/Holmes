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

package net.holmes.core.business.media.dao;

import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import net.holmes.core.business.media.dao.index.MediaIndexElement;
import net.holmes.core.business.media.model.AbstractNode;
import net.holmes.core.business.media.model.RawUrlNode;
import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.common.exception.HolmesException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.sun.syndication.feed.module.RssModule.*;
import static net.holmes.core.business.media.model.AbstractNode.NodeType.TYPE_PODCAST_ENTRY;
import static net.holmes.core.common.MediaType.TYPE_RAW_URL;

/**
 * Podcast parser.
 */
abstract class PodcastParser {
    /**
     * Parse podcast.
     *
     * @param podcastUrl podcast URL
     * @param podcastId  podcast id
     * @return list of podcast entry nodes
     * @throws HolmesException
     */
    @SuppressWarnings("unchecked")
    public List<AbstractNode> parse(String podcastUrl, String podcastId) throws HolmesException {
        List<AbstractNode> podcastEntryNodes = newArrayList();
        try (XmlReader reader = new XmlReader(new URL(podcastUrl))) {
            // Get RSS feed entries
            List<SyndEntry> rssEntries = new SyndFeedInput().build(reader).getEntries();
            for (SyndEntry rssEntry : rssEntries) {
                // Get RSS entry enclosures
                for (SyndEnclosure enclosure : (List<SyndEnclosure>) rssEntry.getEnclosures()) {
                    addPodcastEntry(podcastId, podcastEntryNodes, rssEntry, enclosure);
                }
            }
        } catch (IOException | FeedException e) {
            throw new HolmesException(e);
        }
        return podcastEntryNodes;
    }

    /**
     * Add Podcast entry.
     *
     * @param podcastId         podcast id
     * @param podcastEntryNodes list of podcast entries
     * @param rssEntry          RSS entry
     * @param enclosure         RSS enclosure
     */
    private void addPodcastEntry(String podcastId, List<AbstractNode> podcastEntryNodes, SyndEntry rssEntry, SyndEnclosure enclosure) {
        MimeType mimeType = enclosure.getType() != null ? MimeType.valueOf(enclosure.getType()) : null;
        if (mimeType != null && mimeType.isMedia()) {
            // Add to media index
            String podcastEntryId = addMediaIndexElement(new MediaIndexElement(podcastId, TYPE_RAW_URL.getValue(), mimeType.getMimeType(), enclosure.getUrl(), rssEntry.getTitle(), false, false));

            // Build podcast entry node
            RawUrlNode podcastEntryNode = new RawUrlNode(TYPE_PODCAST_ENTRY, podcastEntryId, podcastId, rssEntry.getTitle(), mimeType, enclosure.getUrl(), getDuration(rssEntry));
            podcastEntryNode.setIconUrl(getIconUrl(rssEntry));
            podcastEntryNode.setModifiedDate(getPublishedDate(rssEntry));

            // Add podcast entry node
            podcastEntryNodes.add(podcastEntryNode);
        }
    }

    /**
     * Add entry to media index.
     *
     * @param mediaIndexElement media index element
     * @return index element id
     */
    public abstract String addMediaIndexElement(final MediaIndexElement mediaIndexElement);

    /**
     * Get RSS entry duration.
     *
     * @param rssEntry RSS entry
     * @return duration
     */
    private String getDuration(SyndEntry rssEntry) {
        EntryInformation itunesInfo = (EntryInformation) (rssEntry.getModule(ITUNES_URI));
        return itunesInfo != null ? itunesInfo.getDurationString() : null;
    }

    /**
     * Get RSS entry icon Url.
     *
     * @param rssEntry RSS entry
     * @return icon Url
     */
    private String getIconUrl(SyndEntry rssEntry) {
        MediaModule mediaInfo = (MediaModule) (rssEntry.getModule(MEDIA_RSS_URI));
        return mediaInfo != null ? mediaInfo.getThumbnailUrl() : null;
    }

    /**
     * Get RSS entry published date
     *
     * @param rssEntry RSS entry
     * @return published date
     */
    private Long getPublishedDate(SyndEntry rssEntry) {
        return rssEntry.getPublishedDate() != null ? rssEntry.getPublishedDate().getTime() : null;
    }
}


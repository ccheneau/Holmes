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

package net.holmes.core.media.dao;

import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.ITunes;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.media.model.PodcastEntryNode;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * Parser for podcast entries.
 */
abstract class PodcastParser {

    /**
     * Parses podcast entry
     */
    @SuppressWarnings("unchecked")
    public void parse(String podcastUrl, String podcastId) throws IOException, FeedException {
        try (XmlReader reader = new XmlReader(new URL(podcastUrl))) {
            // Get RSS feed entries
            List<SyndEntry> rssEntries = new SyndFeedInput().build(reader).getEntries();
            if (rssEntries != null)
                for (SyndEntry rssEntry : rssEntries) {
                    if (rssEntry.getEnclosures() != null && !rssEntry.getEnclosures().isEmpty()) {
                        for (SyndEnclosure enclosure : (List<SyndEnclosure>) rssEntry.getEnclosures()) {
                            MimeType mimeType = enclosure.getType() != null ? new MimeType(enclosure.getType()) : null;
                            if (mimeType != null && mimeType.isMedia()) {
                                PodcastEntryNode podcastEntryNode = new PodcastEntryNode(UUID.randomUUID().toString(), podcastId, rssEntry.getTitle().trim(), mimeType, enclosure.getUrl(), getDuration(rssEntry));
                                podcastEntryNode.setIconUrl(getIconUrl(rssEntry));
                                podcastEntryNode.setModifiedDate(getPublishedDate(rssEntry));
                                // Fire new podcast entry node
                                newPodcastEntryNode(podcastEntryNode);
                            }
                        }
                    }
                }
        }
    }

    /**
     * New podcast entry node.
     *
     * @param podcastEntryNode new podcast entry node
     */
    public abstract void newPodcastEntryNode(PodcastEntryNode podcastEntryNode);

    /**
     * Get RSS entry duration.
     *
     * @param rssEntry RSS entry
     * @return duration
     */
    private String getDuration(SyndEntry rssEntry) {
        String duration = null;
        EntryInformation itunesInfo = (EntryInformation) (rssEntry.getModule(ITunes.URI));
        if (itunesInfo != null && itunesInfo.getDuration() != null)
            duration = itunesInfo.getDuration().toString();
        return duration;
    }

    /**
     * Get RSS entry icon Url.
     *
     * @param rssEntry RSS entry
     * @return icon Url
     */
    private String getIconUrl(SyndEntry rssEntry) {
        String iconUrl = null;
        MediaModule mediaInfo = (MediaModule) (rssEntry.getModule(MediaModule.URI));
        if (mediaInfo != null && mediaInfo.getMetadata() != null && mediaInfo.getMetadata().getThumbnail() != null && mediaInfo.getMetadata().getThumbnail().length > 0)
            iconUrl = mediaInfo.getMetadata().getThumbnail()[0].getUrl().toString();
        return iconUrl;
    }

    /**
     * Get RSS entry published date
     *
     * @param rssEntry RSS entry
     * @return published date
     */
    private Long getPublishedDate(SyndEntry rssEntry) {
        if (rssEntry.getPublishedDate() != null)
            return rssEntry.getPublishedDate().getTime();
        return null;
    }
}


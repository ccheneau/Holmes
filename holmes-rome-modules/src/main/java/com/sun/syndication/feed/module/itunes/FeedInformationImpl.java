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

package com.sun.syndication.feed.module.itunes;

import com.sun.syndication.feed.module.itunes.types.Category;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class contains information for iTunes podcast feeds that exist at the Channel level.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.2 $
 */
public class FeedInformationImpl extends AbstractITunesObject implements FeedInformation {
    private static final long serialVersionUID = -3672967750764259458L;

    private String ownerName;
    private String ownerEmailAddress;
    private URL image;
    private List<Category> categories;

    /**
     * Creates a new instance of FeedInformationImpl
     */
    public FeedInformationImpl() {
    }

    /**
     * The parent categories for this feed
     *
     * @return The parent categories for this feed
     */
    @Override
    public List<Category> getCategories() {
        return categories == null ? new ArrayList<Category>() : categories;
    }

    /**
     * The parent categories for this feed
     *
     * @param categories The parent categories for this feed
     */
    @Override
    public void setCategories(final List<Category> categories) {
        this.categories = categories;
    }

    /**
     * Returns the owner name for the feed
     *
     * @return Returns the owner name for the feed
     */
    @Override
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Sets the owner name for the feed
     *
     * @param ownerName Sets the owner name for the feed
     */
    @Override
    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * Returns the owner email address for the feed.
     *
     * @return Returns the owner email address for the feed.
     */
    @Override
    public String getOwnerEmailAddress() {
        return ownerEmailAddress;
    }

    /**
     * Sets the owner email address for the feed.
     *
     * @param ownerEmailAddress Sets the owner email address for the feed.
     */
    @Override
    public void setOwnerEmailAddress(final String ownerEmailAddress) {
        this.ownerEmailAddress = ownerEmailAddress;
    }

    /**
     * Returns the URL for the image.
     * <p/>
     * NOTE: To specification images should be in PNG or JPEG format.
     *
     * @return Returns the URL for the image.
     */
    @Override
    public URL getImage() {
        return image;
    }

    /**
     * Sets the URL for the image.
     * <p/>
     * NOTE: To specification images should be in PNG or JPEG format.
     *
     * @param image Sets the URL for the image.
     */
    @Override
    public void setImage(final URL image) {
        this.image = image;
    }

    /**
     * Required by the ROME API
     *
     * @param obj object to copy property values from
     */
    @Override
    public void copyFrom(final Object obj) {
        FeedInformationImpl info = (FeedInformationImpl) obj;
        this.setAuthor(info.getAuthor());
        this.setBlock(info.getBlock());

        this.getCategories().clear();
        this.getCategories().addAll(info.getCategories());

        this.setExplicit(info.getExplicit());

        try {
            if (info.getImage() != null) {
                this.setImage(new URL(info.getImage().toExternalForm()));
            }
        } catch (MalformedURLException e) {
            Logger.getAnonymousLogger().fine("Error copying URL:" + info.getImage());
        }

        this.setKeywords(info.getKeywords().clone());

        this.setOwnerEmailAddress(info.getOwnerEmailAddress());
        this.setOwnerName(info.getOwnerName());
        this.setSubtitle(info.getSubtitle());
        this.setSummary(info.getSummary());
    }

    /**
     * Returns a copy of this FeedInformationImpl object
     *
     * @return Returns a copy of this FeedInformationImpl object
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        FeedInformationImpl info = new FeedInformationImpl();
        info.copyFrom(this);

        return info;
    }
}

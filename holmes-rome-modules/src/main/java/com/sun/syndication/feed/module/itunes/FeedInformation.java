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

import java.net.URL;

/**
 * This class contains information for iTunes podcast feeds that exist at the Channel level.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.2 $
 */
public interface FeedInformation extends ITunes {

    /**
     * Sets the URL for the image.
     * <p/>
     * NOTE: To specification images should be in PNG or JPEG format.
     *
     * @param image Sets the URL for the image.
     */
    void setImage(URL image);

    /**
     * Returns the URL for the image.
     * <p/>
     * NOTE: To specification images should be in PNG or JPEG format.
     *
     * @return Returns the URL for the image.
     */
    URL getImage();

    /**
     * Sets the owner email address for the feed.
     *
     * @param ownerEmailAddress Sets the owner email address for the feed.
     */
    void setOwnerEmailAddress(String ownerEmailAddress);

    /**
     * Returns the owner email address for the feed.
     *
     * @return Returns the owner email address for the feed.
     */
    String getOwnerEmailAddress();

    /**
     * Sets the owner name for the feed
     *
     * @param ownerName Sets the owner name for the feed
     */
    void setOwnerName(String ownerName);

    /**
     * Returns the owner name for the feed
     *
     * @return Returns the owner name for the feed
     */
    String getOwnerName();
}

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

import com.sun.syndication.feed.module.Module;

/**
 * This interface contains the methods common to all iTunes module points.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.3 $
 */
public interface ITunes extends Module {

    String URI = AbstractITunesObject.URI;

    /**
     * Returns the author string for this feed or entry.
     *
     * @return Returns the author string for this feed or entry
     */
    String getAuthor();

    /**
     * Sets the author string for this feed or entry.
     *
     * @param author Sets the author string for this feed or entry
     */
    void setAuthor(String author);

    /**
     * Boolean as to whether to block this feed or entry.
     *
     * @return Boolean as to whether to block this feed or entry
     */
    boolean getBlock();

    /**
     * Boolean as to whether to block this feed or entry.
     *
     * @param block Boolean as to whether to block this feed or entry
     */
    void setBlock(boolean block);

    /**
     * Boolean as to whether this feed or entry contains adult content.
     *
     * @return Boolean as to whether this feed or entry contains adult content
     */
    boolean getExplicit();

    /**
     * Boolean as to whether this feed or entry contains adult content.
     *
     * @param explicit Boolean as to whether this feed or entry contains adult content
     */
    void setExplicit(boolean explicit);

    /**
     * A list of keywords for this feed or entry.
     * <p/>
     * Must not contain spaces
     *
     * @return A list of keywords for this feed or entry
     */
    String[] getKeywords();

    /**
     * A list of keywords for this feed or entry.
     * <p/>
     * Must not contain spaces
     *
     * @param keywords A list of keywords for this feed or entry
     */
    void setKeywords(String[] keywords);

    /**
     * A subtitle for this feed or entry.
     *
     * @return A subtitle for this feed or entry
     */
    String getSubtitle();

    /**
     * A subtitle for this feed or entry.
     *
     * @param subtitle A subtitle for this feed or entry
     */
    void setSubtitle(String subtitle);

    /**
     * A subtitle for this feed or entry.
     *
     * @return A subtitle for this feed or entry
     */
    String getSummary();

    /**
     * A subtitle for this feed or entry.
     *
     * @param summary A subtitle for this feed or entry
     */
    void setSummary(String summary);
}

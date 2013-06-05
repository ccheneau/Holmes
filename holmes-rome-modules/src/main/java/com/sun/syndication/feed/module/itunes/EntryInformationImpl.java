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

import com.sun.syndication.feed.module.itunes.types.Duration;

/**
 * This class contains information for iTunes podcast feeds that exist at the Item level.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.2 $
 */
public class EntryInformationImpl extends AbstractITunesObject implements EntryInformation {
    private static final long serialVersionUID = -4189984062914642656L;

    private Duration duration;

    /**
     * Creates a new instance of EntryInformationImpl
     */
    public EntryInformationImpl() {
        super();
    }

    /**
     * Returns the Duration object for this Item
     *
     * @return Returns the Duration object for this Item
     */
    @Override
    public Duration getDuration() {
        return duration;
    }

    /**
     * Sets the Duration object for this Item
     *
     * @param duration Sets the Duration object for this Item
     */
    @Override
    public void setDuration(final Duration duration) {
        this.duration = duration;
    }

    /**
     * Defined by the ROME module API.
     *
     * @param obj Object to copy from
     */
    @Override
    public void copyFrom(final Object obj) {
        EntryInformationImpl info = (EntryInformationImpl) obj;
        this.setAuthor(info.getAuthor());
        this.setBlock(info.getBlock());

        if (info.getDuration() != null) {
            this.setDuration(new Duration(info.getDuration().getMilliseconds()));
        }

        this.setExplicit(info.getExplicit());

        this.setKeywords(info.getKeywords().clone());

        this.setSubtitle(info.getSubtitle());
        this.setSummary(info.getSummary());
    }

    /**
     * Required by the ROME API.
     *
     * @return A clone of this module object
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        EntryInformationImpl info = new EntryInformationImpl();
        info.copyFrom(this);

        return info;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(" Duration: ");
        sb.append(this.getDuration());
        sb.append("]");
        sb.append(super.toString());

        return sb.toString();
    }
}

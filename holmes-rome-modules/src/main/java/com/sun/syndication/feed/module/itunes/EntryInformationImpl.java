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

package com.sun.syndication.feed.module.itunes;

import com.sun.syndication.feed.module.itunes.types.Duration;

/**
 * This class contains information for iTunes podcast feeds that exist at the Item level.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.2 $
 */
public class EntryInformationImpl extends AbstractITunesObject implements EntryInformation {

    private Duration duration;

    /**
     * Creates a new instance of EntryInformationImpl
     */
    public EntryInformationImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @return Returns the Duration string for this Item
     */
    @Override
    public String getDurationString() {
        return duration != null ? duration.toString() : null;
    }

    /**
     * {@inheritDoc}
     *
     * @param duration Sets the Duration object for this Item
     */
    @Override
    public void setDuration(final Duration duration) {
        this.duration = duration;
    }

    /**
     * {@inheritDoc}
     *
     * @param obj Object to copy from
     */
    @Override
    public void copyFrom(final Object obj) {
        EntryInformationImpl info = (EntryInformationImpl) obj;
        if (info.duration != null) {
            this.setDuration(new Duration(info.duration.getMilliseconds()));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        EntryInformationImpl info = new EntryInformationImpl();
        info.copyFrom(this);

        return info;
    }
}

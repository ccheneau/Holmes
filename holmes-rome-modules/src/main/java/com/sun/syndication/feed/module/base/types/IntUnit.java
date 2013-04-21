/*
 * IntUnit.java
 *
 * Created on November 16, 2005, 12:49 PM
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
 */
package com.sun.syndication.feed.module.base.types;

import java.io.Serializable;

import com.sun.syndication.feed.module.base.io.GoogleBaseParser;

/**
 * The Class IntUnit.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.1 $
 */
public class IntUnit implements CloneableType<IntUnit>, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5856378395819544767L;

    /** The units. */
    private String units;

    /** The value. */
    private int value;

    /**
     * Constructor.
     *
     * @param source the source
     */
    public IntUnit(final String source) {
        String parse = source.trim();
        int space = -1;
        for (int i = 0; i < parse.length(); i++) {
            if (!inCharArray(parse.charAt(i), GoogleBaseParser.INTEGER_CHARS)) {
                space = i;
                break;
            }
        }
        if (space == -1) {
            space = parse.length();
        }

        this.value = Integer.parseInt(GoogleBaseParser.stripNonValidCharacters(GoogleBaseParser.INTEGER_CHARS, parse.substring(0, space)));

        if (space != parse.length()) {
            this.units = parse.substring(space, parse.length()).trim();
        }
    }

    /**
     * In char array.
     *
     * @param find the find
     * @param array the array
     * @return true, if successful
     */
    private boolean inCharArray(final char find, final char[] array) {
        for (int i = 0; i < array.length; i++) {
            if (find == array[i]) return true;
        }
        return false;
    }

    /**
     * Creates a new instance of IntUnit.
     *
     * @param value the value
     * @param units the units
     */
    public IntUnit(final int value, final String units) {
        this.value = value;
        this.units = units;
    }

    public String getUnits() {
        return units;
    }

    public int getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public IntUnit clone() throws CloneNotSupportedException {
        super.clone();
        return new IntUnit(this.value, this.units);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (this.units != null && this.units.trim().length() > 0) {
            return this.value + " " + this.units;
        } else {
            return Integer.toString(value);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IntUnit)) return false;
        IntUnit f = (IntUnit) o;
        if (f.getValue() != this.value) return false;

        if (this.units == null && f.units == null) return true;
        else if (this.units != null && this.units.equals(f.getUnits())) return true;

        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((units == null) ? 0 : units.hashCode());
        result = prime * result + value;
        return result;
    }
}

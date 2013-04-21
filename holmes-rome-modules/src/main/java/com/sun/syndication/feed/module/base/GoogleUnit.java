/**
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

package com.sun.syndication.feed.module.base;

/**
 * The Interface Unit.
 */
public final class GoogleUnit {
    public static final char[] INTEGER_CHARS = "-1234567890".toCharArray();
    public static final char[] FLOAT_CHARS = "-1234567890.".toCharArray();
    public static final String SHORT_DT_FMT = "yyyy-MM-dd";
    public static final String LONG_DT_FMT = "yyyy-MM-dd'T'HH:mm:ss";

    private GoogleUnit() {
    }

    /**
     * Strip non valid characters.
     *
     * @param validCharacters the valid characters
     * @param input the input
     * @return string
     */
    public static String stripNonValidCharacters(final char[] validCharacters, final String input) {
        StringBuffer newString = new StringBuffer();
        for (int i = 0; i < input.length(); i++) {
            for (int j = 0; j < validCharacters.length; j++) {
                if (input.charAt(i) == validCharacters[j]) {
                    newString.append(validCharacters[j]);
                }
            }
        }
        return newString.toString();
    }
}

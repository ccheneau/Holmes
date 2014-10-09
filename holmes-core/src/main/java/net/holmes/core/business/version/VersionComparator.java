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

package net.holmes.core.business.version;

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holmes version comparator
 */
public final class VersionComparator implements Comparator<String>, Serializable {
    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\D*(\\d+)\\.?(\\d+)?\\.?(\\d+)?\\.?(\\d+)?$");
    private static final int MAX_VERSION_ITEM_NUMBER = 100;

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(String version1, String version2) {
        Integer versionNumber1 = convertVersion(version1);
        Integer versionNumber2 = convertVersion(version2);

        if (versionNumber2 == null || versionNumber1 == null) {
            return 0;
        }

        return versionNumber1.compareTo(versionNumber2);
    }

    /**
     * Convert version string to number.
     *
     * @param version version string
     * @return number representation on version string
     */
    private Integer convertVersion(final String version) {
        Integer versionNumber = null;
        if (version != null) {
            Matcher matcher = VERSION_PATTERN.matcher(version);
            if (matcher.matches()) {
                versionNumber = getVersionNumber(matcher);
            }
        }
        return versionNumber;
    }

    /**
     * Get version number.
     *
     * @param matcher version pattern matcher
     * @return version number
     */
    private Integer getVersionNumber(final Matcher matcher) {
        Integer versionNumber;
        versionNumber = 0;
        for (int i = 1; i <= matcher.groupCount(); i++) {
            if (matcher.group(i) != null) {
                versionNumber += Double.valueOf(Math.pow(MAX_VERSION_ITEM_NUMBER, matcher.groupCount() - i) * Integer.valueOf(matcher.group(i))).intValue();
            }
        }
        return versionNumber;
    }
}

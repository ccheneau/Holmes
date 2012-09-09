/**
* Copyright (C) 2012  Cedric Cheneau
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
package net.holmes.core.util;

import java.io.File;

public class HolmesHomeDirectory {
    private static final String HOME_CONF_FOLDER = "conf";
    private static final String HOME_SITE_FOLDER = "site";

    public static String getConfigDirectory() {
        return getSubDirectory(HOME_CONF_FOLDER);
    }

    public static String getSiteDirectory() {
        return getSubDirectory(HOME_SITE_FOLDER);
    }

    private static String getSubDirectory(String subDirName) {
        StringBuilder homeSubDirectory = new StringBuilder();
        homeSubDirectory.append(System.getProperty(SystemProperty.HOLMES_HOME.getValue())).append(File.separator).append(subDirName);
        File confDir = new File(homeSubDirectory.toString());
        if (!confDir.exists()) {
            confDir.mkdir();
        }
        return homeSubDirectory.toString();
    }
}

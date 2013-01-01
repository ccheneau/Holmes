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
package net.holmes.core.util;

import java.io.File;

public class HolmesHomeDirectory {

    private static HolmesHomeDirectory instance = null;

    private final String confDirectory;
    private final String uiDirectory;

    private HolmesHomeDirectory() {
        confDirectory = getSubDirectory("conf");
        uiDirectory = getSubDirectory("ui");
    }

    public static HolmesHomeDirectory getInstance() {
        if (instance == null) instance = new HolmesHomeDirectory();
        return instance;
    }

    public String getConfigDirectory() {
        return confDirectory;
    }

    public String getUIDirectory() {
        return uiDirectory;
    }

    private String getSubDirectory(String subDirName) {
        File confDir = new File(SystemProperty.HOLMES_HOME.getValue(), subDirName);
        if (!confDir.exists()) {
            throw new RuntimeException(confDir.getAbsolutePath() + " does not exist. Check " + SystemProperty.HOLMES_HOME.getName() + " ["
                    + SystemProperty.HOLMES_HOME.getValue() + "] system property");
        }
        return confDir.getAbsolutePath();
    }
}

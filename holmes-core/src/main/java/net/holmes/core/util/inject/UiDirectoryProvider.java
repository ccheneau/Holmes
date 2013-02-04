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

package net.holmes.core.util.inject;

import java.io.File;

import javax.inject.Provider;

import net.holmes.core.util.SystemProperty;

public final class UiDirectoryProvider implements Provider<String> {

    @Override
    public String get() {
        File uiDir = new File(SystemProperty.HOLMES_HOME.getValue(), "ui");
        if (!uiDir.exists()) {
            throw new RuntimeException(uiDir.getAbsolutePath() + " does not exist. Check " + SystemProperty.HOLMES_HOME.getName() + " ["
                    + SystemProperty.HOLMES_HOME.getValue() + "] system property");
        }
        return uiDir.getAbsolutePath();
    }
}

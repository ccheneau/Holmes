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
package net.holmes.common;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public final class SystemUtils {

    private static final String LOCK_FILE = "holmes.lock";

    private SystemUtils() {
    }

    /**
     * Create Holmes lock file
     * @return true on lock success, false if lock file already exists
     */
    public static boolean lockInstance() {
        try {
            // Create lock file
            final File lockFile = new File(getLocalUserDataDir(), LOCK_FILE);
            final RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        // Release lock file on system exit
                        try {
                            fileLock.release();
                            randomAccessFile.close();
                            if (!lockFile.delete()) System.err.println("Unable to remove lock file: " + lockFile.getPath());
                        } catch (IOException e) {
                            System.err.println("Unable to remove lock file: " + lockFile.getPath() + "  " + e.getMessage());
                        }
                    }
                });
                return true;
            }
        } catch (IOException e) {
            System.err.println("Unable to create and/or lock file: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get local data directory where Holmes configuration and logs are saved. 
     * This directory is stored in user home directory
     */
    public static File getLocalUserDataDir() {
        StringBuilder holmesDataDir = new StringBuilder().append(SystemProperty.USER_HOME.getValue()).append(File.separator).append(".holmes");

        // Check directory and create it if it does not exist
        File fDataDir = new File(holmesDataDir.toString());
        if (!fDataDir.exists() || !fDataDir.isDirectory()) if (!fDataDir.mkdirs()) throw new RuntimeException("Failed to create " + holmesDataDir.toString());

        return fDataDir;
    }
}

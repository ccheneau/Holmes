package net.holmes.core.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class AppLock {
    private static final String HOME_CONF_FILE_PATH = ".holmes";

    public static boolean lockInstance() {
        try {
            StringBuilder homePath = new StringBuilder();
            homePath.append(System.getProperty(SystemProperty.USER_HOME.getValue())).append(File.separator).append(HOME_CONF_FILE_PATH);
            File fConfPath = new File(homePath.toString());
            if (!fConfPath.exists() || !fConfPath.isDirectory()) fConfPath.mkdirs();

            final File lockFile = new File(homePath.toString(), "holmes.lock");
            final RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            fileLock.release();
                            randomAccessFile.close();
                            lockFile.delete();
                        }
                        catch (Exception e) {
                            System.err.println("Unable to remove lock file: " + lockFile.getPath() + "  " + e.getMessage());
                        }
                    }
                });
                return true;
            }
        }
        catch (Exception e) {
            System.err.println("Unable to create and/or lock file: " + e.getMessage());
        }
        return false;
    }

}

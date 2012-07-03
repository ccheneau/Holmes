/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.util;

import java.io.File;
import java.util.logging.Logger;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;

public class LogUtil {
    public static void loadConfig() {
        // Redirect java.util.logging to slf4j
        // SLF4JBridgeHandler.install();

        // Configure Log4j
        String homeDir = System.getProperty(SystemProperty.HOLMES_HOME.getValue());
        if (homeDir != null && new File(homeDir).exists()) {
            String logConfig = homeDir + File.separator + "conf" + File.separator + "log4j.xml";
            if (new File(logConfig).exists()) DOMConfigurator.configure(logConfig);
        }
    }

    public static void setLevel(String level) {
        // Set level to java.util.logging
        java.util.logging.Level julLevel = java.util.logging.Level.OFF;
        if (level.equalsIgnoreCase("debug")) julLevel = java.util.logging.Level.FINE;
        else if (level.equalsIgnoreCase("info")) julLevel = java.util.logging.Level.INFO;
        else if (level.equalsIgnoreCase("warn")) julLevel = java.util.logging.Level.WARNING;
        else if (level.equalsIgnoreCase("error")) julLevel = java.util.logging.Level.SEVERE;
        Logger.getLogger("").setLevel(julLevel);

        // Set log4j level
        LogManager.getLoggerRepository().setThreshold(Level.toLevel(level));
        if (LogManager.getRootLogger() != null) LogManager.getRootLogger().setLevel(Level.toLevel(level));
    }
}

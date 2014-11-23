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

package net.holmes.core.common;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;

import static net.holmes.core.common.FileUtils.*;
import static org.junit.Assert.*;

public class FileUtilsTest {

    @Test
    public void testTestPrivateConstructor() throws Exception {
        Constructor<FileUtils> cnt = FileUtils.class.getDeclaredConstructor();
        cnt.setAccessible(true);
        cnt.newInstance();
    }

    @Test
    public void testIsValidFile() {
        File file = new File("file") {
            @Override
            public boolean isFile() {
                return true;
            }

            @Override
            public boolean canRead() {
                return true;
            }

            @Override
            public boolean isHidden() {
                return false;
            }
        };

        assertTrue(isValidFile(file));
    }

    @Test
    public void testIsValidFileNotFile() {
        File file = new File("file") {
            @Override
            public boolean isFile() {
                return false;
            }
        };

        assertFalse(isValidFile(file));
    }

    @Test
    public void testIsValidFileNotReadable() {
        File file = new File("file") {
            @Override
            public boolean isFile() {
                return true;
            }

            @Override
            public boolean canRead() {
                return false;
            }
        };

        assertFalse(isValidFile(file));
    }

    @Test
    public void testIsValidFileHidden() {
        File file = new File("file") {
            @Override
            public boolean canRead() {
                return true;
            }

            @Override
            public boolean isFile() {
                return true;
            }

            @Override
            public boolean isHidden() {
                return true;
            }
        };

        assertFalse(isValidFile(file));
    }

    @Test
    public void testIsValidDirectory() {
        File file = new File("file") {
            @Override
            public boolean isDirectory() {
                return true;
            }

            @Override
            public boolean canRead() {
                return true;
            }

            @Override
            public boolean isHidden() {
                return false;
            }
        };

        assertTrue(isValidDirectory(file));
    }

    @Test
    public void testIsValidDirectoryNotDirectory() {
        File file = new File("file") {
            @Override
            public boolean isDirectory() {
                return false;
            }
        };

        assertFalse(isValidDirectory(file));
    }

    @Test
    public void testIsValidDirectoryNotReadable() {
        File file = new File("file") {
            @Override
            public boolean isDirectory() {
                return true;
            }

            @Override
            public boolean canRead() {
                return false;
            }
        };

        assertFalse(isValidDirectory(file));
    }

    @Test
    public void testIsValidDirectoryHidden() {
        File file = new File("file") {
            @Override
            public boolean isDirectory() {
                return true;
            }

            @Override
            public boolean canRead() {
                return true;
            }

            @Override
            public boolean isHidden() {
                return true;
            }
        };

        assertFalse(isValidDirectory(file));
    }

}

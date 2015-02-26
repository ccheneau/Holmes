/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

import java.util.Enumeration;
import java.util.ResourceBundle;

import static java.nio.charset.StandardCharsets.*;


/**
 * UTF-8 resource bundle. Inspired from https://gist.github.com/enginer/3168dd4a374994718f0e
 */
public class Utf8ResourceBundle extends ResourceBundle {

    /**
     * Bundle with unicode data
     */
    private final ResourceBundle bundle;

    /**
     * Instantiates a new UTF8 resource bundle.
     *
     * @param baseName the base name of the resource bundle
     */
    public Utf8ResourceBundle(final String baseName) {
        this.bundle = getBundle(baseName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<String> getKeys() {
        return bundle.getKeys();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object handleGetObject(final String key) {
        return new String(bundle.getString(key).getBytes(ISO_8859_1), UTF_8);
    }
}
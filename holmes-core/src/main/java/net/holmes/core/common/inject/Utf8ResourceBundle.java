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

package net.holmes.core.common.inject;

import java.util.Enumeration;
import java.util.ResourceBundle;

import static java.nio.charset.StandardCharsets.*;


/**
 * UTF-8 resource bundle. Inspired from https://gist.github.com/enginer/3168dd4a374994718f0e
 */
abstract class Utf8ResourceBundle {

    /**
     * Private constructor.
     */
    private Utf8ResourceBundle() {
    }

    /**
     * Gets the unicode friendly resource bundle
     *
     * @param baseName base name of the resource bundle
     * @return Unicode friendly resource bundle
     * @see ResourceBundle#getBundle(String)
     */
    public static ResourceBundle getUtf8Bundle(final String baseName) {
        return new Utf8PropertyResourceBundle(ResourceBundle.getBundle(baseName));
    }

    /**
     * Resource Bundle that does the hard work
     */
    private static final class Utf8PropertyResourceBundle extends ResourceBundle {

        /**
         * Bundle with unicode data
         */
        private final ResourceBundle bundle;

        /**
         * Initializing constructor
         *
         * @param bundle resource bundle
         */
        private Utf8PropertyResourceBundle(final ResourceBundle bundle) {
            this.bundle = bundle;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings(value = {"unchecked", "NullableProblems"})
        public Enumeration getKeys() {
            return bundle.getKeys();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("NullableProblems")
        protected Object handleGetObject(final String key) {
            return new String(bundle.getString(key).getBytes(ISO_8859_1), UTF_8);
        }
    }
}
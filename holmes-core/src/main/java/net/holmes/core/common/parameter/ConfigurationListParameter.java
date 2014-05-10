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

package net.holmes.core.common.parameter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;

/**
 * String list implementation for configuration parameter.
 */
public class ConfigurationListParameter extends ConfigurationParameter<List<String>> {
    private static final char SEPARATOR = ',';

    /**
     * Instantiates a new String list configuration parameter.
     *
     * @param name         parameter name
     * @param defaultValue default value
     */
    ConfigurationListParameter(String name, String defaultValue) {
        super(name, Splitter.on(SEPARATOR).splitToList(defaultValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> parse(String stringValue) {
        return Splitter.on(SEPARATOR).splitToList(stringValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(List<String> value) {
        return Joiner.on(SEPARATOR).join(value);
    }
}

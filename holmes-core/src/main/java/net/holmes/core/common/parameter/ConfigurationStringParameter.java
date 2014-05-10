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

/**
 * String implementation for configuration parameter.
 */
public class ConfigurationStringParameter extends ConfigurationParameter<String> {

    /**
     * Instantiates a new String configuration parameter.
     *
     * @param name         parameter name
     * @param defaultValue default value
     */
    ConfigurationStringParameter(String name, String defaultValue) {
        super(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String parse(String stringValue) {
        return stringValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(String value) {
        return value;
    }
}

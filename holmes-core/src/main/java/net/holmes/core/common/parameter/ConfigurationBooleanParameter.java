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
 * Boolean implementation for configuration parameter.
 */
public class ConfigurationBooleanParameter extends ConfigurationParameter<Boolean> {

    /**
     * Instantiates a new Boolean configuration parameter.
     *
     * @param name         parameter name
     * @param defaultValue default value
     */
    ConfigurationBooleanParameter(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean parse(String stringValue) {
        return Boolean.valueOf(stringValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(Boolean value) {
        return value.toString();
    }
}

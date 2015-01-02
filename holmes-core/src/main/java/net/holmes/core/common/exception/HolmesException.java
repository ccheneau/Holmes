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

package net.holmes.core.common.exception;

/**
 * Holmes exception
 */
public class HolmesException extends Exception {

    /**
     * Constructs a new Holmes exception.
     *
     * @param message exception message
     */
    protected HolmesException(final String message) {
        super(message);
    }

    /**
     * Constructs a new Holmes exception.
     *
     * @param message exception message
     * @param cause   original exception
     */
    protected HolmesException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new Holmes exception.
     *
     * @param cause original exception
     */
    public HolmesException(final Throwable cause) {
        super(cause);
    }
}

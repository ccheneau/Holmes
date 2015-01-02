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

package net.holmes.core.service.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.holmes.core.common.exception.HolmesException;

/**
 * Http file request exception.
 */
public final class HttpFileRequestException extends HolmesException {
    private final transient HttpResponseStatus status;

    /**
     * Instantiates a new http file request exception.
     *
     * @param message message
     * @param status  status
     */
    public HttpFileRequestException(final String message, final HttpResponseStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Get HTTP response status.
     *
     * @return HTTP response status
     */
    public HttpResponseStatus getStatus() {
        return status;
    }
}

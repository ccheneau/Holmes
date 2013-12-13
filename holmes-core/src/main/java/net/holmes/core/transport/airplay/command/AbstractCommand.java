/*
 * Copyright (C) 2012-2013  Cedric Cheneau
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

package net.holmes.core.transport.airplay.command;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values.CLOSE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Airplay command.
 */
public abstract class AbstractCommand {
    private static final String AIRPLAY_USER_AGENT = "MediaControl/1.0";
    private final CommandType type;
    private final Map<UrlParameter, String> urlParameters = Maps.newHashMap();
    private final Map<PostParameter, String> postParameters = Maps.newHashMap();

    /**
     * Instantiates a new Airplay command.
     *
     * @param type command type
     */
    public AbstractCommand(final CommandType type) {
        this.type = type;
    }

    /**
     * Add Url parameter.
     *
     * @param parameter Url parameter
     * @param value     Url parameter value
     */
    public void addUrlParameter(final UrlParameter parameter, final String value) {
        urlParameters.put(parameter, value);
    }

    /**
     * Add post parameter.
     *
     * @param parameter post parameter
     * @param value     post parameter value
     */
    public void addPostParameter(final PostParameter parameter, final String value) {
        this.postParameters.put(parameter, value);
    }

    /**
     * Get Http request.
     *
     * @param deviceHost device host
     * @param devicePort device port
     * @return Http request
     */
    public HttpRequest getHttpRequest(final String deviceHost, final int devicePort) {
        ByteBuf content;
        int contentLength;
        if (!postParameters.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (PostParameter param : postParameters.keySet()) {
                sb.append(param.getValue()).append("=").append(postParameters.get(param)).append("\r\n");
            }
            content = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);
            contentLength = sb.length();
        } else {
            content = Unpooled.buffer(0);
            contentLength = 0;
        }

        HttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, type.getMethod(), getRequestUri(deviceHost, devicePort), content, false);
        request.headers().set(USER_AGENT, AIRPLAY_USER_AGENT);
        request.headers().set(CONTENT_LENGTH, contentLength);
        request.headers().set(CONNECTION, CLOSE);

        return request;
    }

    /**
     * Get request Uri.
     *
     * @param deviceHost device host
     * @param devicePort device port
     * @return request Uri
     */
    private String getRequestUri(final String deviceHost, final int devicePort) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(deviceHost).append(":").append(devicePort).append("/").append(type.getValue());
        if (!urlParameters.isEmpty()) {
            sb.append("?");
            for (UrlParameter param : urlParameters.keySet()) {
                sb.append(param.getValue()).append("=").append(urlParameters.get(param)).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Airplay command type
     */
    public static enum CommandType {
        PLAY("play", POST),
        PLAY_STATUS("scrub", GET),
        RATE("rate", POST),
        SEEK("scrub", POST),
        STOP("stop", POST);
        private final String value;
        private final HttpMethod method;

        /**
         * Instantiates a new command type
         *
         * @param value value
         */
        CommandType(final String value, final HttpMethod method) {
            this.value = value;
            this.method = method;
        }

        /**
         * Get command type value.
         *
         * @return command type value
         */
        public String getValue() {
            return value;
        }

        public HttpMethod getMethod() {
            return method;
        }
    }

    /**
     * Url parameter
     */
    public static enum UrlParameter {
        VALUE("value"),
        POSITION("position");
        private final String value;

        /**
         * Instantiates a new Url parameter
         *
         * @param value value
         */
        private UrlParameter(final String value) {
            this.value = value;
        }

        /**
         * Get Url parameter value.
         *
         * @return Url parameter value
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * Post parameter
     */
    public static enum PostParameter {
        CONTENT_LOCATION("Content-Location"),
        START_POSITION("Start-Position");
        private final String value;

        /**
         * Instantiates a new post parameter
         *
         * @param value value
         */
        private PostParameter(final String value) {
            this.value = value;
        }

        /**
         * Get post parameter value.
         *
         * @return post parameter value
         */
        public String getValue() {
            return value;
        }
    }
}

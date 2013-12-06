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

package net.holmes.core.transport.airplay.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.USER_AGENT;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;

/**
 * Airplay command.
 */
public abstract class AirplayCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(AirplayCommand.class);
    private static final String AIRPLAY_USER_AGENT = "MediaControl/1.0";
    private final CommandType type;
    private final Map<UrlParameter, String> urlParameters = Maps.newHashMap();
    private final List<NameValuePair> postParameters = Lists.newArrayList();

    /**
     * Instantiates a new Airplay command.
     *
     * @param type command type
     */
    public AirplayCommand(final CommandType type) {
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
        this.postParameters.add(new BasicNameValuePair(parameter.getValue(), value));
    }

    /**
     * Get Http request.
     *
     * @param deviceHost device host
     * @param devicePort device port
     * @return Http request
     */
    public HttpRequestBase getHttpRequest(final String deviceHost, final int devicePort) {
        HttpRequestBase request = null;
        if (type.getMethod().equals(GET)) {
            request = new HttpGet(getRequestUri(deviceHost, devicePort));
            request.setHeader(USER_AGENT, AIRPLAY_USER_AGENT);
            request.addHeader(CONTENT_LENGTH, "0");
        } else if (type.getMethod().equals(POST)) {
            request = new HttpPost(getRequestUri(deviceHost, devicePort));
            request.setHeader(USER_AGENT, AIRPLAY_USER_AGENT);
            if (!postParameters.isEmpty())
                try {
                    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(postParameters));
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error(e.getMessage(), e);
                }
        }
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
     * Airplay Url parameter
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
     * Airplay post parameter
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

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

package net.holmes.core.airplay.command.model;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.http.HttpMethod;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;

/**
 * Airplay command.
 */
public abstract class AbstractCommand {

    private final CommandType type;
    private final Map<CommandParameter, String> parameters = Maps.newHashMap();
    private String content;

    /**
     * Instantiates a new Airplay command.
     *
     * @param type command type
     */
    public AbstractCommand(final CommandType type) {
        this.type = type;
    }

    /**
     * Add command parameter.
     *
     * @param parameter parameter
     * @param value     parameter value
     */
    public void addParameter(final CommandParameter parameter, final String value) {
        parameters.put(parameter, value);
    }

    /**
     * Set command content.
     *
     * @param content command content
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * Get command string.
     *
     * @return command
     */
    public String getCommand() {
        String parameterValue = "";
        if (!parameters.isEmpty()) {
            List<String> params = Lists.newArrayList();
            for (CommandParameter commandParameter : parameters.keySet()) {
                params.add(commandParameter.getValue() + "=" + parameters.get(commandParameter));
            }
            parameterValue = "?" + Joiner.on('&').join(params);
        }

        String headerPart = String.format("%s /%s%s HTTP/1.1\n" +
                "Content-Length: %d\n" +
                "User-Agent: MediaControl/1.0\n", type.getMethod().name(), type.getValue(), parameterValue, content == null ? 0 : content.length());
        if (content == null || content.length() == 0) {
            return headerPart;
        } else {
            return headerPart + "\n" + content;
        }
    }

    /**
     * Airplay command type
     */
    public static enum CommandType {
        PLAY("play", POST),
        RATE("rate", POST),
        SCRUB("scrub", GET),
        STATUS("status", POST),
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
     * Airplay command parameter
     */
    public static enum CommandParameter {
        VALUE("value"),
        POSITION("position");
        private final String value;

        /**
         * Instantiates a new command parameter
         *
         * @param value value
         */
        CommandParameter(final String value) {
            this.value = value;
        }

        /**
         * Get command parameter value.
         *
         * @return command parameter value
         */
        public String getValue() {
            return value;
        }
    }
}

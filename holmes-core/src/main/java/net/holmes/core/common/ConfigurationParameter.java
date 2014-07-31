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

package net.holmes.core.common;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Configuration parameters
 */
public abstract class ConfigurationParameter<T> {

    /**
     * Misc parameters
     */
    /**
     * Enable streaming on Airplay devices
     */
    public static final ConfigurationBooleanParameter AIRPLAY_STREAMING_ENABLE = new ConfigurationBooleanParameter("enable_airplay_streaming", true);
    /**
     * Delay between cache clean attempts (in minutes)
     */
    public static final ConfigurationIntParameter CACHE_CLEAN_DELAY_MINUTES = new ConfigurationIntParameter("cache_clean_delay_minutes", 30);
    /**
     * Delay between Holmes release checks (in hours)
     */
    public static final ConfigurationIntParameter RELEASE_CHECK_DELAY_HOURS = new ConfigurationIntParameter("release_check_delay_hours", 30);
    /**
     * Delay between streaming status updates (in seconds)
     */
    public static final ConfigurationIntParameter STREAMING_STATUS_UPDATE_DELAY_SECONDS = new ConfigurationIntParameter("streaming_status_update_delay_seconds", 3);

    /**
     * Http server parameter
     */
    /**
     * Http server port
     */
    public static final ConfigurationIntParameter HTTP_SERVER_PORT = new ConfigurationIntParameter("http_server_port", 8085);
    /**
     * Duration for Http cache header (in seconds)
     */
    public static final ConfigurationIntParameter HTTP_SERVER_CACHE_SECOND = new ConfigurationIntParameter("http_server_cache_second", 60);
    /**
     * Number of threads used by Netty NIO boss event loop group (O means that Netty uses a default value)
     */
    public static final ConfigurationIntParameter HTTP_SERVER_BOSS_THREADS = new ConfigurationIntParameter("http_server_boss_threads", 0);
    /**
     * Number of threads used by Netty NIO worker event loop group (O means that Netty uses a default value)
     */
    public static final ConfigurationIntParameter HTTP_SERVER_WORKER_THREADS = new ConfigurationIntParameter("http_server_worker_threads", 0);

    /**
     * Icecast directory support parameters
     */
    /**
     * Enable Icecast directory support
     */
    public static final ConfigurationBooleanParameter ICECAST_ENABLE = new ConfigurationBooleanParameter("enable_icecast_directory", false);
    /**
     * List of Icecast genres
     */
    public static final ConfigurationListParameter ICECAST_GENRE_LIST = new ConfigurationListParameter("icecast_genre_list", "70s,80s,90s,adult,alternative,ambient,anime,bass,best,blues,chill,christian,classic,classical,club,college,community,contemporary,country,dance,deep,disco,dj,downtempo,drum,dubstep,eclectic,electro,folk,fun,funk,game,gospel,hip hop,hiphop,hit,house,indie,instrumental,international,jazz,jpop,jrock,latin,live,lounge,metal,minecraft,minimal,misc,mix,mixed,music,musique,news,oldies,pop,progressive,promodj,punk,radio,rap,reggae,religious,rnb,rock,salsa,scanner,smooth,soul,sport,sports,talk,techno,top,trance,urban,various,webradio,world");
    /**
     * Maximum attempts to download Icecast directory
     */
    public static final ConfigurationIntParameter ICECAST_MAX_DOWNLOAD_RETRY = new ConfigurationIntParameter("icecast_max_download_retry", 3);
    /**
     * Delay between Icecast directory downloads (in hours)
     */
    public static final ConfigurationIntParameter ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS = new ConfigurationIntParameter("icecast_yellow_page_download_delay_hours", 24);
    /**
     * URL used to download Icecast directory
     */
    public static final ConfigurationStringParameter ICECAST_YELLOW_PAGE_URL = new ConfigurationStringParameter("icecast_yellow_page_url", "http://dir.xiph.org/yp.xml");

    /**
     * Podcast management parameters
     */
    /**
     * TTL for podcasts in cache
     */
    public static final ConfigurationIntParameter PODCAST_CACHE_EXPIRE_HOURS = new ConfigurationIntParameter("podcast_cache_expire_hours", 2);
    /**
     * Max number of elements in podcast cache
     */
    public static final ConfigurationIntParameter PODCAST_CACHE_MAX_ELEMENTS = new ConfigurationIntParameter("podcast_cache_max_elements", 50);
    /**
     * Whether to add podcast item number to podcast element name (prevent Upnp server to sort elements by name)
     */
    public static final ConfigurationBooleanParameter PODCAST_PREPEND_ENTRY_NAME = new ConfigurationBooleanParameter("podcast_prepend_entry_name", true);

    /**
     * System Tray parameters
     */
    /**
     * Enable system tray icon
     */
    public static final ConfigurationBooleanParameter SYSTRAY_ENABLE = new ConfigurationBooleanParameter("enable_systray", true);

    /**
     * UPnP parameters
     */
    /**
     * Enable UPnP server
     */
    public static final ConfigurationBooleanParameter UPNP_SERVER_ENABLE = new ConfigurationBooleanParameter("enable_upnp_server", true);
    /**
     * UPnP server name
     */
    public static final ConfigurationStringParameter UPNP_SERVER_NAME = new ConfigurationStringParameter("upnp_server_name", "Holmes media server");
    /**
     * Whether to add subtitle files (srt..) to UPnP server
     */
    public static final ConfigurationBooleanParameter UPNP_ADD_SUBTITLE = new ConfigurationBooleanParameter("upnp_add_subtitle", true);
    /**
     * UPnP service port
     */
    public static final ConfigurationIntParameter UPNP_SERVICE_PORT = new ConfigurationIntParameter("upnp_service_port", 5002);

    public static final ImmutableList<? extends ConfigurationParameter> PARAMETERS = ImmutableList.of(
            AIRPLAY_STREAMING_ENABLE,
            CACHE_CLEAN_DELAY_MINUTES,
            HTTP_SERVER_PORT,
            HTTP_SERVER_CACHE_SECOND,
            HTTP_SERVER_BOSS_THREADS,
            HTTP_SERVER_WORKER_THREADS,
            ICECAST_ENABLE,
            ICECAST_GENRE_LIST,
            ICECAST_MAX_DOWNLOAD_RETRY,
            ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS,
            ICECAST_YELLOW_PAGE_URL,
            PODCAST_CACHE_EXPIRE_HOURS,
            PODCAST_CACHE_MAX_ELEMENTS,
            PODCAST_PREPEND_ENTRY_NAME,
            RELEASE_CHECK_DELAY_HOURS,
            STREAMING_STATUS_UPDATE_DELAY_SECONDS,
            SYSTRAY_ENABLE,
            UPNP_ADD_SUBTITLE,
            UPNP_SERVER_ENABLE,
            UPNP_SERVER_NAME,
            UPNP_SERVICE_PORT);

    private final String name;
    private final T defaultValue;

    /**
     * Instantiates a new configuration parameter.
     *
     * @param name         parameter name
     * @param defaultValue default value
     */
    protected ConfigurationParameter(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Get parameter name.
     *
     * @return parameter name
     */
    public String getName() {
        return name;
    }


    /**
     * Parse parameter value from String.
     *
     * @param stringValue string value
     */
    public abstract T parse(String stringValue);

    /**
     * Format parameter value as String
     *
     * @param value value to format
     * @return String value
     */
    public String format(T value) {
        return value.toString();
    }

    /**
     * Get default value
     *
     * @return default value
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * String configuration parameter.
     */
    public static final class ConfigurationStringParameter extends ConfigurationParameter<String> {

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
    }

    /**
     * Boolean configuration parameter.
     */
    public static final class ConfigurationBooleanParameter extends ConfigurationParameter<Boolean> {

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
    }

    /**
     * Integer configuration parameter.
     */
    public static final class ConfigurationIntParameter extends ConfigurationParameter<Integer> {

        /**
         * Instantiates a new Integer configuration parameter.
         *
         * @param name         parameter name
         * @param defaultValue default value
         */
        ConfigurationIntParameter(String name, Integer defaultValue) {
            super(name, defaultValue);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Integer parse(String stringValue) {
            return Integer.valueOf(stringValue);
        }
    }

    /**
     * String list configuration parameter.
     */
    public static final class ConfigurationListParameter extends ConfigurationParameter<List<String>> {
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
}


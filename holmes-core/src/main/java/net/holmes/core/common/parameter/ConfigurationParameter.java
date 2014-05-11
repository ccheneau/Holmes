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
 * Configuration parameters
 */
public abstract class ConfigurationParameter<T> {

    /**
     * Misc parameters
     */
    /**
     * Enable streaming on Airplay devices
     */
    public static ConfigurationBooleanParameter AIRPLAY_STREAMING_ENABLE = new ConfigurationBooleanParameter("enable_airplay_streaming", true);
    /**
     * Delay between cache clean attempts (in minutes)
     */
    public static ConfigurationIntParameter CACHE_CLEAN_DELAY_MINUTES = new ConfigurationIntParameter("cache_clean_delay_minutes", 30);
    /**
     * Delay between Holmes release checks (in hours)
     */
    public static ConfigurationIntParameter RELEASE_CHECK_DELAY_HOURS = new ConfigurationIntParameter("release_check_delay_hours", 30);
    /**
     * Delay between streaming status updates (in seconds)
     */
    public static ConfigurationIntParameter STREAMING_STATUS_UPDATE_DELAY_SECONDS = new ConfigurationIntParameter("streaming_status_update_delay_seconds", 3);

    /**
     * Http server parameter
     */
    /**
     * Http server port
     */
    public static ConfigurationIntParameter HTTP_SERVER_PORT = new ConfigurationIntParameter("http_server_port", 8085);
    /**
     * Duration for Http caching header (in seconds)
     */
    public static ConfigurationIntParameter HTTP_SERVER_CACHE_SECOND = new ConfigurationIntParameter("http_server_cache_second", 60);

    /**
     * Icecast directory support parameters
     */
    /**
     * Enable Icecast directory support
     */
    public static ConfigurationBooleanParameter ICECAST_ENABLE = new ConfigurationBooleanParameter("enable_icecast_directory", true);
    /**
     * List of Icecast genres
     */
    public static ConfigurationListParameter ICECAST_GENRE_LIST = new ConfigurationListParameter("icecast_genre_list", "70s,80s,90s,adult,alternative,ambient,anime,bass,best,blues,chill,christian,classic,classical,club,college,community,contemporary,country,dance,deep,disco,dj,downtempo,drum,dubstep,eclectic,electro,folk,fun,funk,game,gospel,hip hop,hiphop,hit,house,indie,instrumental,international,jazz,jpop,jrock,latin,live,lounge,metal,minecraft,minimal,misc,mix,mixed,music,musique,news,oldies,pop,progressive,promodj,punk,radio,rap,reggae,religious,rnb,rock,salsa,scanner,smooth,soul,sport,sports,talk,techno,top,trance,urban,various,webradio,world");
    /**
     * Maximum attempts to download Icecast directory
     */
    public static ConfigurationIntParameter ICECAST_MAX_DOWNLOAD_RETRY = new ConfigurationIntParameter("icecast_max_download_retry", 3);
    /**
     * Delay between Icecast directory downloads (in hours)
     */
    public static ConfigurationIntParameter ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS = new ConfigurationIntParameter("icecast_yellow_page_download_delay_hours", 24);
    /**
     * URL used to download Icecast directory
     */
    public static ConfigurationStringParameter ICECAST_YELLOW_PAGE_URL = new ConfigurationStringParameter("icecast_yellow_page_url", "http://dir.xiph.org/yp.xml");

    /**
     * Podcast management parameters
     */
    /**
     * TTL for podcasts in cache
     */
    public static ConfigurationIntParameter PODCAST_CACHE_EXPIRE_HOURS = new ConfigurationIntParameter("podcast_cache_expire_hours", 2);
    /**
     * Max number of elements in podcast cache
     */
    public static ConfigurationIntParameter PODCAST_CACHE_MAX_ELEMENTS = new ConfigurationIntParameter("podcast_cache_max_elements", 50);
    /**
     * Whether to add podcast item number to podcast element name (prevent Upnp server to sort elements by name)
     */
    public static ConfigurationBooleanParameter PODCAST_PREPEND_ENTRY_NAME = new ConfigurationBooleanParameter("podcast_prepend_entry_name", true);

    /**
     * System Tray parameters
     */
    /**
     * Enable system tray icon
     */
    public static ConfigurationBooleanParameter SYSTRAY_ENABLE = new ConfigurationBooleanParameter("enable_systray", true);

    /**
     * UPnp parameters
     */
    /**
     * Enable Upnp server
     */
    public static ConfigurationBooleanParameter UPNP_SERVER_ENABLE = new ConfigurationBooleanParameter("enable_upnp_server", true);
    /**
     * Upnp server name
     */
    public static ConfigurationStringParameter UPNP_SERVER_NAME = new ConfigurationStringParameter("upnp_server_name", "Holmes media server");
    /**
     * Whether to show icons on system tray sub menu
     */
    public static ConfigurationBooleanParameter SYSTRAY_ICONS_IN_MENU = new ConfigurationBooleanParameter("icons_in_systray_menu", true);
    /**
     * Whether to add subtitle files (srt..) to Upnp server
     */
    public static ConfigurationBooleanParameter UPNP_ADD_SUBTITLE = new ConfigurationBooleanParameter("upnp_add_subtitle", true);
    /**
     * Upnp service port
     */
    public static ConfigurationIntParameter UPNP_SERVICE_PORT = new ConfigurationIntParameter("upnp_service_port", 5002);

    public static final ConfigurationParameter[] PARAMETERS = {AIRPLAY_STREAMING_ENABLE,
            CACHE_CLEAN_DELAY_MINUTES,
            HTTP_SERVER_PORT,
            HTTP_SERVER_CACHE_SECOND,
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
            SYSTRAY_ICONS_IN_MENU,
            UPNP_ADD_SUBTITLE,
            UPNP_SERVER_ENABLE,
            UPNP_SERVER_NAME,
            UPNP_SERVICE_PORT};

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
}


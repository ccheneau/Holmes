/**
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
package net.holmes.common.configuration;

/**
 * Configuration parameter.
 */
public enum Parameter {
    PREPEND_PODCAST_ENTRY_NAME("prepend_podcast_entry_name", "false"), // 
    ENABLE_SYSTRAY("enable_systray", "true"), //
    ENABLE_UPNP("enable_upnp", "true"), //
    ENABLE_EXTERNAL_SUBTITLES("enable_external_subtitles", "true"), //
    PODCAST_CACHE_MAX_ELEMENTS("podcast_cache_max_elements", "50"), //
    PODCAST_CACHE_EXPIRE_HOURS("podcast_cache_expire_hours", "2"), //
    PODCAST_CACHE_CLEAN_DELAY_MINUTES("podcast_cache_clean_delay_minutes", "60"), //
    MEDIA_INDEX_CLEAN_DELAY_MINUTES("media_index_clean_delay_minutes", "15"), //
    MEDIA_SCAN_ALL_DELAY_MINUTES("media_scan_all_delay_minutes", "0");

    private final String name;
    private final String defaultValue;

    /**
     * Constructor.
     *
     * @param name 
     *      parameter name
     * @param defaultValue 
     *      parameter default value
     */
    Parameter(final String name, final String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}

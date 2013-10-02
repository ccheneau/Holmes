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

package net.holmes.core.backend;

import com.google.common.base.Strings;
import net.holmes.core.backend.exception.BackendException;
import net.holmes.core.backend.response.ConfigurationFolder;
import net.holmes.core.common.NodeFile;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.ConfigurationNode;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Backend manager utils.
 */
final class BackendManagerHelper {

    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp|file)://.+$", Pattern.CASE_INSENSITIVE);

    /**
     * Validate server name.
     *
     * @param serverName server name to validate
     */
    public void validateServerName(final String serverName) {
        checkNonEmptyString(serverName, "backend.settings.server.name.error");
    }

    /**
     * Validate HTTP server port.
     *
     * @param serverPort server port to validate
     */
    public void validateHttpServerPort(final Integer serverPort) {
        if (serverPort == null || serverPort < Configuration.MIN_HTTP_SERVER_PORT || serverPort > Configuration.MAX_HTTP_SERVER_PORT)
            throw new BackendException("backend.settings.http.port.error");
    }

    /**
     * Validate folder.
     *
     * @param folder      folder to validate
     * @param configNodes existing folders
     * @param excludedId  folder id excluded from duplication checking
     */
    public void validateFolder(final ConfigurationFolder folder, final List<ConfigurationNode> configNodes, final String excludedId) {
        // Check folder's name and path are not empty
        checkNonEmptyString(folder.getName(), "backend.folder.name.error");
        checkNonEmptyString(folder.getPath(), "backend.folder.path.error");

        // Check folder path exists
        if (!(new NodeFile(folder.getPath()).isValidDirectory()))
            throw new BackendException("backend.folder.path.unknown.error");

        // Check for duplication
        checkDuplicatedConfigurationFolder(folder, configNodes, excludedId, "backend.folder.already.exist.error");
    }

    /**
     * Validate podcast.
     *
     * @param podcast     podcast to validate
     * @param configNodes existing podcasts
     * @param excludedId  podcast id excluded from duplication check
     */
    public void validatePodcast(final ConfigurationFolder podcast, final List<ConfigurationNode> configNodes, final String excludedId) {
        // Check podcast name and path are not empty
        checkNonEmptyString(podcast.getName(), "backend.podcast.name.error");
        checkNonEmptyString(podcast.getPath(), "backend.podcast.url.error");

        // Check podcast URL is correct
        if (!URL_PATTERN.matcher(podcast.getPath()).matches())
            throw new BackendException("backend.podcast.url.malformed.error");

        // Check for duplication
        checkDuplicatedConfigurationFolder(podcast, configNodes, excludedId, "backend.podcast.already.exist.error");
    }

    /**
     * Find configuration node.
     *
     * @param id          node id
     * @param configNodes existing config nodes
     * @param podcast     podcast or not
     * @return configuration node
     */
    public ConfigurationNode findConfigurationNode(String id, List<ConfigurationNode> configNodes, boolean podcast) {
        for (ConfigurationNode node : configNodes)
            if (node.getId().equals(id)) return node;

        throw new BackendException(podcast ? "backend.podcast.unknown.error" : "backend.folder.unknown.error");
    }

    /**
     * Checks string is not null or empty.
     *
     * @param toCheck      string to check
     * @param errorMessage error message
     */
    private void checkNonEmptyString(String toCheck, String errorMessage) {
        if (Strings.isNullOrEmpty(toCheck)) throw new BackendException((errorMessage));
    }

    /**
     * Checks configuration does not already exist.
     *
     * @param folder       configuration folder to check
     * @param configNodes  existing configuration nodes
     * @param excludedId   folder id to exclude from duplication check
     * @param errorMessage error message
     */
    private void checkDuplicatedConfigurationFolder(final ConfigurationFolder folder, final List<ConfigurationNode> configNodes, final String excludedId, final String errorMessage) {
        for (ConfigurationNode node : configNodes) {
            if (excludedId != null && excludedId.equals(node.getId())) continue;
            if (node.getLabel().equals(folder.getName()) || node.getPath().equals(folder.getPath()))
                throw new BackendException(errorMessage);
        }
    }
}

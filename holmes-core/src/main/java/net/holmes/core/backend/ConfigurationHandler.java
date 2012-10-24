/**
* Copyright (C) 2012  Cedric Cheneau
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

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.holmes.core.backend.response.ConfigNode;
import net.holmes.core.backend.response.ConfigNodeListResponse;
import net.holmes.core.backend.response.ConfigurationResponse;
import net.holmes.core.backend.response.EditConfigNodeResponse;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.util.bundle.Bundle;

import com.google.common.collect.Lists;

/**
 * Backend REST handler for:
 * <ul>
 * <li>add / update / delete configuration nodes</li>
 * <li>edit global configuration</li>
 *</ul>
 */
@Path("/backend/configuration")
public class ConfigurationHandler {
    private static final String ADD_OPERATION = "add";
    private static final String EDIT_OPERATION = "edit";
    private static final String DELETE_OPERATION = "del";

    private final Configuration configuration;
    private final Bundle bundle;

    @Inject
    public ConfigurationHandler(Configuration configuration, Bundle bundle) {
        this.configuration = configuration;
        this.bundle = bundle;
    }

    /**
     * Get video configuration folders
     */
    @GET
    @Path("/getVideoFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigNodeListResponse getVideoFolders() {
        return getConfigurationNodes(configuration.getVideoFolders());
    }

    /**
     * Get audio configuration folders
     */
    @GET
    @Path("/getAudioFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigNodeListResponse getAudioFolders() {
        return getConfigurationNodes(configuration.getAudioFolders());
    }

    /**
     * Get picture configuration folders
     */
    @GET
    @Path("/getPictureFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigNodeListResponse getPictureFolders() {
        return getConfigurationNodes(configuration.getPictureFolders());
    }

    /**
     * Get pod-casts 
     */
    @GET
    @Path("/getPodcasts")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigNodeListResponse getPodcasts() {
        return getConfigurationNodes(configuration.getPodcasts());
    }

    /**
     * Edit video configuration folder
     */
    @POST
    @Path("/editVideoFolder")
    @Produces(MediaType.APPLICATION_JSON)
    public EditConfigNodeResponse editVideoFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editConfigNode(operation, id, label, path, configuration.getVideoFolders(), true);
    }

    /**
     * Edit audio configuration folder
     */
    @POST
    @Path("/editAudioFolder")
    @Produces(MediaType.APPLICATION_JSON)
    public EditConfigNodeResponse editAudioFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editConfigNode(operation, id, label, path, configuration.getAudioFolders(), true);
    }

    /**
     * Edit picture configuration folder
     */
    @POST
    @Path("/editPictureFolder")
    @Produces(MediaType.APPLICATION_JSON)
    public EditConfigNodeResponse editPictureFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editConfigNode(operation, id, label, path, configuration.getPictureFolders(), true);
    }

    /**
     * Edit pod-cast
     */
    @POST
    @Path("/editPodcast")
    @Produces(MediaType.APPLICATION_JSON)
    public EditConfigNodeResponse editPodcast(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editConfigNode(operation, id, label, path, configuration.getPodcasts(), false);
    }

    /**
     * Get configuration
     */
    @GET
    @Path("/getConfiguration")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationResponse getConfiguration() {
        return new ConfigurationResponse(configuration.getUpnpServerName(), configuration.getHttpServerPort(),
                configuration.getParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME));
    }

    /**
     * Edit global configuration
     */
    @POST
    @Path("/editConfiguration")
    @Produces(MediaType.APPLICATION_JSON)
    public EditConfigNodeResponse editConfiguration(@FormParam("serverName") String serverName, @FormParam("httpServerPort") String httpServerPort,
            @FormParam("prependPodcastItem") boolean prependPodcastItem) {
        EditConfigNodeResponse response = new EditConfigNodeResponse();
        response.setStatus(true);
        setResponseErrorCode(response, ErrorCode.NO_ERROR);

        Integer iHttpServerPort = null;
        try {
            iHttpServerPort = Integer.valueOf(httpServerPort);
        } catch (NumberFormatException ignore) {
        }

        // Validate configuration
        if (serverName == null || serverName.trim().length() == 0) {
            response.setStatus(false);
            setResponseErrorCode(response, ErrorCode.EMPTY_SERVER_NAME);
        } else if (iHttpServerPort == null) {
            response.setStatus(false);
            setResponseErrorCode(response, ErrorCode.INVALID_HTTP_SERVER_PORT);
        } else {
            // Save configuration
            configuration.setUpnpServerName(serverName.trim());
            configuration.setHttpServerPort(iHttpServerPort);
            configuration.setParameter(Parameter.PREPEND_PODCAST_ENTRY_NAME, prependPodcastItem);
            configuration.saveConfig();
        }
        return response;
    }

    /**
     * Get configuration nodes
     */
    private ConfigNodeListResponse getConfigurationNodes(List<ConfigurationNode> configNodes) {
        Collection<ConfigNode> folders = Lists.newArrayList();
        Collection<String> cell;
        for (ConfigurationNode node : configNodes) {
            cell = Lists.newArrayList();
            cell.add(node.getId());
            cell.add(node.getLabel());
            cell.add(node.getPath());
            folders.add(new ConfigNode(node.getId(), cell));
        }
        return new ConfigNodeListResponse(1, 1, configNodes.size(), folders);
    }

    /**
     * Edit configuration node
     */
    private EditConfigNodeResponse editConfigNode(String operation, String id, String label, String path, List<ConfigurationNode> nodes, boolean isPath) {
        EditConfigNodeResponse response = new EditConfigNodeResponse();
        response.setStatus(true);
        response.setOperation(operation);
        response.setId(id);
        setResponseErrorCode(response, ErrorCode.NO_ERROR);

        if (ADD_OPERATION.equals(operation)) {
            // Check this node does not exists
            ConfigurationNode existingNode = null;
            for (ConfigurationNode node : nodes) {
                if (node.getLabel().equals(label)) existingNode = node;
                else if (node.getPath().equals(path)) existingNode = node;
            }
            if (existingNode == null) {
                ErrorCode errodeCode = isPath ? validatePath(path) : validateUrl(path);
                if (errodeCode == ErrorCode.NO_ERROR) {
                    // Add a new node
                    ConfigurationNode configNode = new ConfigurationNode(UUID.randomUUID().toString(), label, path);
                    nodes.add(configNode);
                    response.setId(configNode.getId());
                } else {
                    // Path not valid
                    response.setStatus(false);
                    setResponseErrorCode(response, errodeCode);
                }
            } else {
                // Node already exists
                response.setStatus(false);
                if (isPath) setResponseErrorCode(response, ErrorCode.FOLDER_ALREADY_EXISTS);
                else setResponseErrorCode(response, ErrorCode.PODCAST_ALREADY_EXISTS);
            }
        } else if (EDIT_OPERATION.equals(operation)) {
            // Check this node exists
            ConfigurationNode existingNode = null;
            boolean duplicated = false;
            for (ConfigurationNode node : nodes) {
                if (node.getId().equals(id)) existingNode = node;
                else {
                    if (node.getLabel().equals(label)) duplicated = true;
                    else if (node.getPath().equals(path)) duplicated = true;
                }
            }
            if (existingNode != null) {
                if (!duplicated) {
                    ErrorCode errodeCode = isPath ? validatePath(path) : validateUrl(path);
                    if (errodeCode == ErrorCode.NO_ERROR) {
                        // Edit node
                        existingNode.setLabel(label);
                        existingNode.setPath(path);
                    } else {
                        // Path not valid
                        response.setStatus(false);
                        setResponseErrorCode(response, errodeCode);
                    }
                } else {
                    // Duplicated node
                    response.setStatus(false);
                    setResponseErrorCode(response, ErrorCode.DUPLICATED_FOLDER);
                }
            } else {
                // Unknown folder
                response.setStatus(false);
                setResponseErrorCode(response, ErrorCode.UNKNOWN_FOLDER);
            }
        } else if (DELETE_OPERATION.equals(operation)) {
            // Check this node exists
            ConfigurationNode existingNode = null;
            for (ConfigurationNode node : nodes) {
                if (node.getId().equals(id)) existingNode = node;
            }
            if (existingNode != null) {
                // Remove the node
                nodes.remove(existingNode);
            } else {
                // Unknown node
                response.setStatus(false);
                setResponseErrorCode(response, ErrorCode.UNKNOWN_FOLDER);
            }
        } else {
            // Unknown operation
            response.setStatus(false);
            setResponseErrorCode(response, ErrorCode.UNKNOWN_OPERATION);
        }

        // Save configuration on success
        if (response.getStatus()) configuration.saveConfig();

        return response;
    }

    private void setResponseErrorCode(EditConfigNodeResponse response, ErrorCode errorCode) {
        response.setErrorCode(errorCode.getCode());
        response.setMessage(bundle.getString("backend.error." + errorCode.getCode()));
    }

    /**
     * Validate path
     */
    private ErrorCode validatePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return ErrorCode.PATH_NOT_EXIST;
        } else if (!file.canRead() || file.isHidden()) {
            return ErrorCode.PATH_NOT_READABLE;
        } else if (!file.isDirectory()) {
            return ErrorCode.PATH_NOT_DIRECTORY;
        }
        return ErrorCode.NO_ERROR;
    }

    /**
     * Validate URL
     */
    private ErrorCode validateUrl(String url) {
        if (!url.toLowerCase().startsWith("http://")) {
            return ErrorCode.MALFORMATTED_URL;
        }
        return ErrorCode.NO_ERROR;
    }

    public enum ErrorCode {
        NO_ERROR(0), //
        FOLDER_ALREADY_EXISTS(1), //
        UNKNOWN_FOLDER(2), //
        UNKNOWN_OPERATION(3), //
        PATH_NOT_EXIST(4), //
        PATH_NOT_DIRECTORY(5), //
        PATH_NOT_READABLE(6), //
        MALFORMATTED_URL(7), //
        DUPLICATED_FOLDER(8), //
        EMPTY_SERVER_NAME(9), //
        INVALID_HTTP_SERVER_PORT(10), //
        PODCAST_ALREADY_EXISTS(11);

        private final int code;

        ErrorCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }
}

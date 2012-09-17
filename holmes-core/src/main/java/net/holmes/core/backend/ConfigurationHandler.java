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
import java.util.ArrayList;
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

import net.holmes.core.backend.response.ConfigFolder;
import net.holmes.core.backend.response.ConfigFolderListResponse;
import net.holmes.core.backend.response.ConfigurationResponse;
import net.holmes.core.backend.response.EditConfigFolderResponse;
import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.configuration.Parameter;
import net.holmes.core.util.bundle.IBundle;

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

    @Inject
    private IConfiguration configuration;

    @Inject
    private IBundle bundle;

    /**
     * Get video configuration folders
     */
    @GET
    @Path("/getVideoFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigFolderListResponse getVideoFolders() {
        return getConfigurationFolders(configuration.getVideoFolders());
    }

    /**
     * Get audio configuration folders
     */
    @GET
    @Path("/getAudioFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigFolderListResponse getAudioFolders() {
        return getConfigurationFolders(configuration.getAudioFolders());
    }

    /**
     * Get picture configuration folders
     */
    @GET
    @Path("/getPictureFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigFolderListResponse getPictureFolders() {
        return getConfigurationFolders(configuration.getPictureFolders());
    }

    /**
     * Get pod-casts 
     */
    @GET
    @Path("/getPodcasts")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigFolderListResponse getPodcasts() {
        return getConfigurationFolders(configuration.getPodcasts());
    }

    /**
     * Edit video configuration folder
     */
    @POST
    @Path("/editVideoFolder")
    @Produces(MediaType.APPLICATION_JSON)
    public EditConfigFolderResponse editVideoFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editFolder(operation, id, label, path, configuration.getVideoFolders(), true);
    }

    /**
     * Edit audio configuration folder
     */
    @POST
    @Path("/editAudioFolder")
    @Produces(MediaType.APPLICATION_JSON)
    public EditConfigFolderResponse editAudioFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editFolder(operation, id, label, path, configuration.getAudioFolders(), true);
    }

    /**
     * Edit picture configuration folder
     */
    @POST
    @Path("/editPictureFolder")
    @Produces(MediaType.APPLICATION_JSON)
    public EditConfigFolderResponse editPictureFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editFolder(operation, id, label, path, configuration.getPictureFolders(), true);
    }

    /**
     * Edit pod-cast
     */
    @POST
    @Path("/editPodcast")
    @Produces(MediaType.APPLICATION_JSON)
    public EditConfigFolderResponse editPodcast(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editFolder(operation, id, label, path, configuration.getPodcasts(), false);
    }

    /**
     * Get global configuration
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
    public EditConfigFolderResponse editConfiguration(@FormParam("serverName") String serverName, @FormParam("httpServerPort") String httpServerPort,
            @FormParam("prependPodcastItem") boolean prependPodcastItem) {
        EditConfigFolderResponse response = new EditConfigFolderResponse();
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
     * Get configuration folders
     */
    private ConfigFolderListResponse getConfigurationFolders(List<ConfigurationNode> configFolders) {
        ConfigFolderListResponse response = new ConfigFolderListResponse();
        response.setPage(1);
        response.setTotal(1);
        response.setRecords(configFolders.size());

        Collection<ConfigFolder> folders = new ArrayList<ConfigFolder>();
        Collection<String> cell = null;
        for (ConfigurationNode folder : configFolders) {
            cell = new ArrayList<String>();
            cell.add(folder.getId());
            cell.add(folder.getLabel());
            cell.add(folder.getPath());
            folders.add(new ConfigFolder(folder.getId(), cell));
        }
        response.setRows(folders);

        return response;
    }

    /**
     * Edit configuration folder
     */
    private EditConfigFolderResponse editFolder(String operation, String id, String label, String path, List<ConfigurationNode> folders, boolean isPath) {
        EditConfigFolderResponse response = new EditConfigFolderResponse();
        response.setStatus(true);
        response.setOperation(operation);
        response.setId(id);
        setResponseErrorCode(response, ErrorCode.NO_ERROR);

        if (ADD_OPERATION.equals(operation)) {
            // Check this folders does not exists
            ConfigurationNode existingFolder = null;
            for (ConfigurationNode folder : folders) {
                if (folder.getLabel().equals(label)) existingFolder = folder;
                else if (folder.getPath().equals(path)) existingFolder = folder;
            }
            if (existingFolder == null) {
                ErrorCode validate = validatePath(path, isPath);
                if (validate.getCode() == 0) {
                    // Add a new folder
                    ConfigurationNode configDirectory = new ConfigurationNode(UUID.randomUUID().toString(), label, path);
                    folders.add(configDirectory);
                    response.setId(configDirectory.getId());
                } else {
                    // Path not valid
                    response.setStatus(false);
                    setResponseErrorCode(response, validate);
                }
            } else {
                // Folder or Podcast already exists
                response.setStatus(false);
                if (isPath) setResponseErrorCode(response, ErrorCode.FOLDER_ALREADY_EXISTS);
                else setResponseErrorCode(response, ErrorCode.PODCAST_ALREADY_EXISTS);
            }
        } else if (EDIT_OPERATION.equals(operation)) {
            // Check this folders exists
            ConfigurationNode existingFolder = null;
            boolean duplicated = false;
            for (ConfigurationNode folder : folders) {
                if (folder.getId().equals(id)) existingFolder = folder;
                else {
                    if (folder.getLabel().equals(label)) duplicated = true;
                    else if (folder.getPath().equals(path)) duplicated = true;
                }
            }
            if (existingFolder != null) {
                if (!duplicated) {
                    ErrorCode validate = validatePath(path, isPath);
                    if (validate == ErrorCode.NO_ERROR) {
                        // Edit the folder
                        existingFolder.setLabel(label);
                        existingFolder.setPath(path);
                    } else {
                        // Path not valid
                        response.setStatus(false);
                        setResponseErrorCode(response, validate);
                    }
                } else {
                    // Duplicated folder
                    response.setStatus(false);
                    setResponseErrorCode(response, ErrorCode.DUPLICATED_FOLDER);
                }
            } else {
                // Unknown folder
                response.setStatus(false);
                setResponseErrorCode(response, ErrorCode.UNKNOWN_FOLDER);
            }
        } else if (DELETE_OPERATION.equals(operation)) {
            // Check this folders exists
            ConfigurationNode existingFolder = null;
            for (ConfigurationNode folder : folders) {
                if (folder.getId().equals(id)) existingFolder = folder;
            }
            if (existingFolder != null) {
                // Remove the folder
                folders.remove(existingFolder);
            } else {
                // Unknown folder
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

    private void setResponseErrorCode(EditConfigFolderResponse response, ErrorCode errorCode) {
        response.setErrorCode(errorCode.getCode());
        response.setMessage(bundle.getString("backend.error." + errorCode.getCode()));
    }

    /**
     * Validate path or URL
     */
    private ErrorCode validatePath(String path, boolean isPath) {
        if (isPath) {
            // Validate path
            File file = new File(path);
            if (!file.exists()) {
                return ErrorCode.PATH_NOT_EXIST;
            } else if (!file.canRead() || file.isHidden()) {
                return ErrorCode.PATH_NOT_READABLE;
            } else if (!file.isDirectory()) {
                return ErrorCode.PATH_NOT_DIRECTORY;
            }
        } else {
            // Validate URL
            if (!path.toLowerCase().startsWith("http://")) {
                return ErrorCode.MALFORMATTED_URL;
            }
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

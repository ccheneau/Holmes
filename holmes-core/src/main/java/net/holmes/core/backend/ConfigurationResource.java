/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.holmes.core.backend.response.ConfigurationResponse;
import net.holmes.core.backend.response.EditFolderResponse;
import net.holmes.core.backend.response.Folder;
import net.holmes.core.backend.response.FolderListResponse;
import net.holmes.core.configuration.ConfigurationNode;
import net.holmes.core.configuration.IConfiguration;

import com.google.inject.Inject;

/**
 * Back-end REST resource for:
 * <ul>
 * <li>add / update / delete configuration nodes</li>
 * <li>edit global configuration</li>
 *</ul>
 */
@Path("/backend/configuration")
public class ConfigurationResource {

    private static final String ADD_OPERATION = "add";
    private static final String EDIT_OPERATION = "edit";
    private static final String DELETE_OPERATION = "del";

    @Inject
    private IConfiguration configuration;

    /**
     * Get holmes version
     */
    @GET
    @Path("/getVersion")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVersion() {
        return "" + this.getClass().getPackage().getImplementationVersion();
    }

    /**
     * Get video configuration folders
     */
    @GET
    @Path("/getVideoFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public FolderListResponse getVideoFolders() {
        return getConfigurationFolders(configuration.getVideoFolders());
    }

    /**
     * Get audio configuration folders
     */
    @GET
    @Path("/getAudioFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public FolderListResponse getAudioFolders() {
        return getConfigurationFolders(configuration.getAudioFolders());
    }

    /**
     * Get picture configuration folders
     */
    @GET
    @Path("/getPictureFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public FolderListResponse getPictureFolders() {
        return getConfigurationFolders(configuration.getPictureFolders());
    }

    /**
     * Get pod-casts 
     */
    @GET
    @Path("/getPodcasts")
    @Produces(MediaType.APPLICATION_JSON)
    public FolderListResponse getPodcasts() {
        return getConfigurationFolders(configuration.getPodcasts());
    }

    /**
     * Edit video configuration folder
     */
    @POST
    @Path("/editVideoFolder")
    @Produces(MediaType.APPLICATION_JSON)
    public EditFolderResponse editVideoFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editFolder(operation, id, label, path, configuration.getVideoFolders(), true);
    }

    /**
     * Edit audio configuration folder
     */
    @POST
    @Path("/editAudioFolder")
    @Produces(MediaType.APPLICATION_JSON)
    public EditFolderResponse editAudioFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editFolder(operation, id, label, path, configuration.getAudioFolders(), true);
    }

    /**
     * Edit picture configuration folder
     */
    @POST
    @Path("/editPictureFolder")
    @Produces(MediaType.APPLICATION_JSON)
    public EditFolderResponse editPictureFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) {
        return editFolder(operation, id, label, path, configuration.getPictureFolders(), true);
    }

    /**
     * Edit pod-cast
     */
    @POST
    @Path("/editPodcast")
    @Produces(MediaType.APPLICATION_JSON)
    public EditFolderResponse editPodcast(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
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
        ConfigurationResponse response = new ConfigurationResponse();
        response.setHttpServerPort(configuration.getHttpServerPort());
        response.setServerName(configuration.getUpnpServerName());

        return response;
    }

    /**
     * Edit global configuration
     */
    @POST
    @Path("/editConfiguration")
    @Produces(MediaType.APPLICATION_JSON)
    public EditFolderResponse editConfiguration(@FormParam("serverName") String serverName, @FormParam("httpServerPort") int httpServerPort) {
        EditFolderResponse response = new EditFolderResponse();
        response.setStatus(true);
        response.setErrorCode(ErrorCode.NO_ERROR);

        // Validate configuration
        if (serverName == null || serverName.trim().length() == 0) {
            response.setStatus(false);
            response.setErrorCode(ErrorCode.EMPTY_SERVER_NAME);
        }
        else if (httpServerPort == 0) {
            response.setStatus(false);
            response.setErrorCode(ErrorCode.EMPTY_HTTP_SERVER_PORT);
        }
        else {
            // Save configuration
            configuration.setUpnpServerName(serverName.trim());
            configuration.setHttpServerPort(httpServerPort);
            configuration.saveConfig();
        }
        return response;
    }

    /**
     * Get configuration folders
     */
    private FolderListResponse getConfigurationFolders(List<ConfigurationNode> configFolders) {
        FolderListResponse response = new FolderListResponse();
        response.setPage(1);
        response.setTotal(1);
        response.setRecords(configFolders.size());

        Collection<Folder> folders = new ArrayList<Folder>();
        Collection<String> cell = null;
        for (ConfigurationNode folder : configFolders) {
            cell = new ArrayList<String>();
            cell.add(folder.getId());
            cell.add(folder.getLabel());
            cell.add(folder.getPath());
            folders.add(new Folder(folder.getId(), cell));
        }

        response.setRows(folders);
        return response;
    }

    /**
     * Edit configuration folder
     */
    private EditFolderResponse editFolder(String operation, String id, String label, String path, List<ConfigurationNode> folders, boolean isPath) {
        EditFolderResponse response = new EditFolderResponse();
        response.setStatus(true);
        response.setOperation(operation);
        response.setId(id);
        response.setErrorCode(ErrorCode.NO_ERROR);

        if (ADD_OPERATION.equals(operation)) {
            // Checks this folders does not exists
            ConfigurationNode existingFolder = null;
            for (ConfigurationNode folder : folders) {
                if (folder.getLabel().equals(label)) existingFolder = folder;
                else if (folder.getPath().equals(path)) existingFolder = folder;
            }
            if (existingFolder == null) {
                ErrorCode validate = validatePath(path, isPath);
                if (validate.code() == 0) {
                    // Adds a new folder
                    ConfigurationNode configDirectory = new ConfigurationNode(UUID.randomUUID().toString(), label, path);
                    folders.add(configDirectory);
                    response.setId(configDirectory.getId());
                }
                else {
                    // Path not valid
                    response.setStatus(false);
                    response.setErrorCode(validate);
                }
            }
            else {
                // Folder already exists
                response.setStatus(false);
                response.setErrorCode(ErrorCode.FOLDER_ALREADY_EXISTS);
            }
        }
        else if (EDIT_OPERATION.equals(operation)) {
            // Checks this folders exists
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
                    if (validate.code() == 0) {
                        // Edits the folder
                        existingFolder.setLabel(label);
                        existingFolder.setPath(path);
                    }
                    else {
                        // Path not valid
                        response.setStatus(false);
                        response.setErrorCode(validate);
                    }
                }
                else {
                    // Duplicated folder
                    response.setStatus(false);
                    response.setErrorCode(ErrorCode.DUPLICATED_FOLDER);
                }
            }
            else {
                // Unknown folder
                response.setStatus(false);
                response.setErrorCode(ErrorCode.UNKNOWN_FOLDER);
            }
        }
        else if (DELETE_OPERATION.equals(operation)) {
            // Checks this folders exists
            ConfigurationNode existingFolder = null;
            for (ConfigurationNode folder : folders) {
                if (folder.getId().equals(id)) existingFolder = folder;
            }
            if (existingFolder != null) {
                // Removes the folder
                folders.remove(existingFolder);
            }
            else {
                // Unknown folder
                response.setStatus(false);
                response.setErrorCode(ErrorCode.UNKNOWN_FOLDER);
            }
        }
        else {
            // Unknown operation
            response.setStatus(false);
            response.setErrorCode(ErrorCode.UNKNOWN_OPERATION);
        }

        // Save configuration on success
        if (response.getStatus()) configuration.saveConfig();

        return response;
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
            }
            else if (!file.canRead() || file.isHidden()) {
                return ErrorCode.PATH_NOT_READABLE;
            }
            else if (!file.isDirectory()) {
                return ErrorCode.PATH_NOT_DIRECTORY;
            }
        }
        else {
            // Validate URL
            if (!path.toLowerCase().startsWith("http://")) {
                return ErrorCode.MALFORMATTED_URL;
            }
        }
        return ErrorCode.NO_ERROR;
    }
}

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
import java.io.IOException;
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

import net.holmes.core.backend.model.ConfigurationResponse;
import net.holmes.core.backend.model.EditResponse;
import net.holmes.core.backend.model.GridRow;
import net.holmes.core.backend.model.ListGridRowsResponse;
import net.holmes.core.common.ErrorCode;
import net.holmes.core.configuration.ContentFolder;
import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.service.IMediaService;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * The Class ConfigurationResource.
 */
@Path("/backend/configuration")
public class ConfigurationResource
{
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(ConfigurationResource.class);

    /** The Constant ADD_GRID_ROW_OPERATION. */
    private static final String ADD_GRID_ROW_OPERATION = "add";

    /** The Constant EDIT_GRID_ROW_OPERATION. */
    private static final String EDIT_GRID_ROW_OPERATION = "edit";

    /** The Constant DELETE_GRID_ROW_OPERATION. */
    private static final String DELETE_GRID_ROW_OPERATION = "del";

    /** The configuration. */
    @Inject
    private IConfiguration configuration;

    /** The media service. */
    @Inject
    private IMediaService mediaService;

    /** The mapper. */
    @Inject
    private ObjectMapper mapper;

    /**
     * Gets the video folders.
     *
     * @return the video folders
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @GET
    @Path("/getVideoFolders")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVideoFolders() throws IOException
    {
        ListGridRowsResponse response = getConfigurationFolders(configuration.getConfig().getVideoFolders());
        return mapper.writeValueAsString(response);
    }

    /**
     * Gets the audio folders.
     *
     * @return the audio folders
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @GET
    @Path("/getAudioFolders")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAudioFolders() throws IOException
    {
        ListGridRowsResponse response = getConfigurationFolders(configuration.getConfig().getAudioFolders());
        return mapper.writeValueAsString(response);
    }

    /**
     * Gets the picture folders.
     *
     * @return the picture folders
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @GET
    @Path("/getPictureFolders")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPictureFolders() throws IOException
    {
        ListGridRowsResponse response = getConfigurationFolders(configuration.getConfig().getPictureFolders());
        return mapper.writeValueAsString(response);
    }

    /**
     * Gets the podcasts.
     *
     * @return the podcasts
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @GET
    @Path("/getPodcasts")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPodcasts() throws IOException
    {
        ListGridRowsResponse response = getConfigurationFolders(configuration.getConfig().getPodcasts());
        return mapper.writeValueAsString(response);
    }

    /**
     * Edits the video folder.
     *
     * @param operation the operation
     * @param id the id
     * @param label the label
     * @param path the path
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @POST
    @Path("/editVideoFolder")
    @Produces(MediaType.TEXT_PLAIN)
    public String editVideoFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) throws IOException
    {
        EditResponse response = editFolder(operation, id, label, path, configuration.getConfig().getVideoFolders(), true);
        if (response.getStatus()) mediaService.scanVideos();
        return mapper.writeValueAsString(response);
    }

    /**
     * Edits the audio folder.
     *
     * @param operation the operation
     * @param id the id
     * @param label the label
     * @param path the path
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @POST
    @Path("/editAudioFolder")
    @Produces(MediaType.TEXT_PLAIN)
    public String editAudioFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) throws IOException
    {
        EditResponse response = editFolder(operation, id, label, path, configuration.getConfig().getAudioFolders(), true);
        if (response.getStatus()) mediaService.scanAudios();
        return mapper.writeValueAsString(response);
    }

    /**
     * Edits the picture folder.
     *
     * @param operation the operation
     * @param id the id
     * @param label the label
     * @param path the path
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @POST
    @Path("/editPictureFolder")
    @Produces(MediaType.TEXT_PLAIN)
    public String editPictureFolder(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label,
            @FormParam("path") String path) throws IOException
    {
        EditResponse response = editFolder(operation, id, label, path, configuration.getConfig().getPictureFolders(), true);
        if (response.getStatus()) mediaService.scanPictures();
        return mapper.writeValueAsString(response);
    }

    /**
     * Edits the podcast.
     *
     * @param operation the operation
     * @param id the id
     * @param label the label
     * @param path the path
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @POST
    @Path("/editPodcast")
    @Produces(MediaType.TEXT_PLAIN)
    public String editPodcast(@FormParam("oper") String operation, @FormParam("id") String id, @FormParam("label") String label, @FormParam("path") String path)
            throws IOException
    {
        EditResponse response = editFolder(operation, id, label, path, configuration.getConfig().getPodcasts(), false);
        if (response.getStatus()) mediaService.scanPodcasts();
        return mapper.writeValueAsString(response);
    }

    /**
     * Gets the configuration.
     *
     * @return the configuration
     */
    @GET
    @Path("/getConfiguration")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConfiguration()
    {
        String result = null;
        ConfigurationResponse response = new ConfigurationResponse();
        response.setHttpServerPort(configuration.getConfig().getHttpServerPort());
        response.setServerName(configuration.getConfig().getServerName());
        response.setLogLevel(configuration.getConfig().getLogLevel());

        try
        {
            result = mapper.writeValueAsString(response);
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Edits the configuration.
     *
     * @param serverName the server name
     * @param httpServerPort the http server port
     * @param logLevel the log level
     * @return the string
     */
    @POST
    @Path("/editConfiguration")
    @Produces(MediaType.TEXT_PLAIN)
    public String editConfiguration(@FormParam("serverName") String serverName, @FormParam("httpServerPort") int httpServerPort,
            @FormParam("logLevel") String logLevel)
    {
        String result = null;
        EditResponse response = new EditResponse();
        response.setStatus(true);
        response.setErrorCode(ErrorCode.NO_ERROR);

        if (serverName == null || serverName.trim().length() == 0)
        {
            response.setStatus(false);
            response.setErrorCode(ErrorCode.EMPTY_SERVER_NAME);
        }
        else if (httpServerPort == 0)
        {
            response.setStatus(false);
            response.setErrorCode(ErrorCode.EMPTY_HTTP_SERVER_PORT);
        }
        else if (logLevel == null || logLevel.trim().length() == 0)
        {
            response.setStatus(false);
            response.setErrorCode(ErrorCode.EMPTY_LOG_LEVEL);
        }
        else
        {
            configuration.getConfig().setServerName(serverName.trim());
            configuration.getConfig().setHttpServerPort(httpServerPort);
            configuration.getConfig().setLogLevel(logLevel.trim());
            configuration.saveConfig();
        }
        try
        {
            result = mapper.writeValueAsString(response);
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Launch scan.
     *
     * @return the string
     */
    @POST
    @Path("/launchScan")
    @Produces(MediaType.TEXT_PLAIN)
    public String launchScan()
    {
        String result = null;
        EditResponse response = new EditResponse();
        response.setStatus(true);
        response.setErrorCode(ErrorCode.NO_ERROR);

        mediaService.scanAll();

        try
        {
            result = mapper.writeValueAsString(response);
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Gets the configuration folders.
     *
     * @param folders the folders
     * @return the configuration folders
     */
    private ListGridRowsResponse getConfigurationFolders(List<ContentFolder> folders)
    {
        ListGridRowsResponse response = new ListGridRowsResponse();
        response.setPage(1);
        response.setTotal(1);
        response.setRecords(folders.size());

        Collection<GridRow> rows = new ArrayList<GridRow>();
        Collection<String> cell = null;
        for (ContentFolder folder : folders)
        {
            cell = new ArrayList<String>();
            cell.add(folder.getId());
            cell.add(folder.getLabel());
            cell.add(folder.getPath());
            rows.add(new GridRow(folder.getId(), cell));
        }

        response.setRows(rows);
        return response;
    }

    /**
     * Edits the folder.
     *
     * @param operation the operation
     * @param id the id
     * @param label the label
     * @param path the path
     * @param folders the folders
     * @param isPath the is path
     * @return the edits the response
     */
    private EditResponse editFolder(String operation, String id, String label, String path, List<ContentFolder> folders, boolean isPath)
    {
        EditResponse response = new EditResponse();
        response.setStatus(true);
        response.setOperation(operation);
        response.setId(id);
        response.setErrorCode(ErrorCode.NO_ERROR);

        if (ADD_GRID_ROW_OPERATION.equals(operation))
        {
            // Checks this folders does not exists
            ContentFolder existingFolder = null;
            for (ContentFolder folder : folders)
            {
                if (folder.getLabel().equals(label)) existingFolder = folder;
                else if (folder.getPath().equals(path)) existingFolder = folder;
            }
            if (existingFolder == null)
            {
                ErrorCode validate = validatePath(path, isPath);
                if (validate.code() == 0)
                {
                    // Adds a new folder
                    ContentFolder configDirectory = new ContentFolder(UUID.randomUUID().toString(), label, path);
                    folders.add(configDirectory);
                    response.setId(configDirectory.getId());
                }
                else
                {
                    // Path not valid
                    response.setStatus(false);
                    response.setErrorCode(validate);
                }
            }
            else
            {
                // Folder already exists
                response.setStatus(false);
                response.setErrorCode(ErrorCode.FOLDER_ALREADY_EXISTS);
            }
        }
        else if (EDIT_GRID_ROW_OPERATION.equals(operation))
        {
            // Checks this folders exists
            ContentFolder existingFolder = null;
            boolean duplicated = false;
            for (ContentFolder folder : folders)
            {
                if (folder.getId().equals(id)) existingFolder = folder;
                else
                {
                    if (folder.getLabel().equals(label)) duplicated = true;
                    else if (folder.getPath().equals(path)) duplicated = true;
                }
            }
            if (existingFolder != null)
            {
                if (!duplicated)
                {
                    ErrorCode validate = validatePath(path, isPath);
                    if (validate.code() == 0)
                    {
                        // Edits the folder
                        existingFolder.setLabel(label);
                        existingFolder.setPath(path);
                    }
                    else
                    {
                        // Path not valid
                        response.setStatus(false);
                        response.setErrorCode(validate);
                    }
                }
                else
                {
                    // Duplicated folder
                    response.setStatus(false);
                    response.setErrorCode(ErrorCode.DUPLICATED_FOLDER);
                }
            }
            else
            {
                // Unknown folder
                response.setStatus(false);
                response.setErrorCode(ErrorCode.UNKNOWN_FOLDER);
            }
        }
        else if (DELETE_GRID_ROW_OPERATION.equals(operation))
        {
            // Checks this folders exists
            ContentFolder existingFolder = null;
            for (ContentFolder folder : folders)
            {
                if (folder.getId().equals(id)) existingFolder = folder;
            }
            if (existingFolder != null)
            {
                // Removes the folder
                folders.remove(existingFolder);
            }
            else
            {
                // Unknown folder
                response.setStatus(false);
                response.setErrorCode(ErrorCode.UNKNOWN_FOLDER);
            }
        }
        else
        {
            // Unknown operation
            response.setStatus(false);
            response.setErrorCode(ErrorCode.UNKNOWN_OPERATION);
        }

        // Save configuration on success
        if (response.getStatus()) configuration.saveConfig();

        return response;
    }

    /**
     * Validate path.
     *
     * @param path the path
     * @param isPath the is path
     * @return the error code
     */
    private ErrorCode validatePath(String path, boolean isPath)
    {
        if (isPath)
        {
            // Validate path
            File file = new File(path);
            if (!file.exists())
            {
                return ErrorCode.PATH_NOT_EXIST;
            }
            else if (!file.canRead() || file.isHidden())
            {
                return ErrorCode.PATH_NOT_READABLE;
            }
            else if (!file.canRead() || file.isHidden())
            {
                return ErrorCode.PATH_NOT_READABLE;
            }
            else if (!file.isDirectory())
            {
                return ErrorCode.PATH_NOT_DIRECTORY;
            }
        }
        else
        {
            // Validate URL
            if (!path.toLowerCase().startsWith("http://"))
            {
                return ErrorCode.MALFORMATTED_URL;
            }
        }
        return ErrorCode.NO_ERROR;
    }
}

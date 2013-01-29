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
package net.holmes.core.backend;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.holmes.core.backend.response.backbone.ConfigurationFolder;
import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.ConfigurationNode;

import com.google.common.collect.Lists;

@Path("/backend/backbone")
public class BackboneHandler {

    private final Configuration configuration;

    @Inject
    public BackboneHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    @GET
    @Path("/videoFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getVideoFolders() {
        return getFolders(configuration.getVideoFolders());
    }

    @GET
    @Path("/videoFolders/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder getVideoFolder(@PathParam("id") String id) {
        return getFolder(id, configuration.getVideoFolders());
    }

    @POST
    @Path("/videoFolders")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder addVideoFolder(ConfigurationFolder folder) {
        addFolder(folder, configuration.getVideoFolders());
        return folder;
    }

    @PUT
    @Path("/videoFolders/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder editVideoFolder(@PathParam("id") String id, ConfigurationFolder folder) {
        editFolder(id, folder, configuration.getVideoFolders());
        return folder;
    }

    @DELETE
    @Path("/videoFolders/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder removeVideoFolder(@PathParam("id") String id) {
        removeFolder(id, configuration.getVideoFolders());
        return new ConfigurationFolder(id, null, null);
    }

    @GET
    @Path("/audioFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getAudioFolders() {
        return getFolders(configuration.getAudioFolders());
    }

    @POST
    @Path("/audioFolders")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder addAudioFolder(ConfigurationFolder folder) {
        addFolder(folder, configuration.getAudioFolders());
        return folder;
    }

    @GET
    @Path("/pictureFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getPictureFolders() {
        return getFolders(configuration.getPictureFolders());
    }

    @POST
    @Path("/pictureFolders")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder addPictureFolder(ConfigurationFolder folder) {
        addFolder(folder, configuration.getPictureFolders());
        return folder;
    }

    @GET
    @Path("/podcasts")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getPodcasts() {
        return getFolders(configuration.getPodcasts());
    }

    @POST
    @Path("/podcasts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationFolder addPodcast(ConfigurationFolder folder) {
        addFolder(folder, configuration.getPodcasts());
        return folder;
    }

    private Collection<ConfigurationFolder> getFolders(List<ConfigurationNode> configNodes) {
        Collection<ConfigurationFolder> folders = Lists.newArrayList();
        for (ConfigurationNode node : configNodes) {
            folders.add(new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath()));
        }
        return folders;
    }

    private ConfigurationFolder getFolder(String id, List<ConfigurationNode> configNodes) {
        //TODO validation
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) return new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath());
        }
        return null;
    }

    private void addFolder(ConfigurationFolder folder, List<ConfigurationNode> configNodes) {
        //TODO validation
        folder.setId(UUID.randomUUID().toString());
        configNodes.add(new ConfigurationNode(folder.getId(), folder.getName(), folder.getPath()));
        configuration.saveConfig();
    }

    private void editFolder(String id, ConfigurationFolder folder, List<ConfigurationNode> configNodes) {
        //TODO validation
        ConfigurationNode currentNode = null;
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) {
                currentNode = node;
                break;
            }
        }
        if (currentNode != null) {
            currentNode.setLabel(folder.getName());
            currentNode.setPath(folder.getPath());
            configuration.saveConfig();
        }
    }

    private void removeFolder(String id, List<ConfigurationNode> configNodes) {
        //TODO validation
        ConfigurationNode currentNode = null;
        for (ConfigurationNode node : configNodes) {
            if (node.getId().equals(id)) {
                currentNode = node;
                break;
            }
        }
        if (currentNode != null) {
            configNodes.remove(currentNode);
            configuration.saveConfig();
        }
    }
}

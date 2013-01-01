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

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
	public BackboneHandler(Configuration configuration){
		this.configuration = configuration;
	}
	
    @GET
    @Path("/videoFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getVideoFolders() {
        return getConfigurationFolders(configuration.getVideoFolders());
    }
	
    @GET
    @Path("/audioFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getAudioFolders() {
        return getConfigurationFolders(configuration.getAudioFolders());
    }
    
    @GET
    @Path("/pictureFolders")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getPictureFolders() {
        return getConfigurationFolders(configuration.getPictureFolders());
    }
    
    @GET
    @Path("/podcasts")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ConfigurationFolder> getPodcasts() {
        return getConfigurationFolders(configuration.getPodcasts());
    }
    
    private Collection<ConfigurationFolder> getConfigurationFolders(List<ConfigurationNode> configNodes) {
        Collection<ConfigurationFolder> folders = Lists.newArrayList();
        for (ConfigurationNode node : configNodes) {
            folders.add(new ConfigurationFolder(node.getId(), node.getLabel(), node.getPath()));
        }
        return folders;
    }    
}

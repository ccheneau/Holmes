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
package net.holmes.core;

import net.holmes.core.configuration.IConfiguration;
import net.holmes.core.configuration.TestConfiguration;
import net.holmes.core.media.IMediaService;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.index.IMediaIndex;
import net.holmes.core.media.index.MediaIndex;
import net.holmes.core.util.mimetype.IMimeTypeFactory;
import net.holmes.core.util.mimetype.MimeTypeFactory;
import net.holmes.core.util.resource.IResource;
import net.holmes.core.util.resource.Resource;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * The Class TestModule.
 */
public class TestModule extends AbstractModule {

    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        bind(IConfiguration.class).to(TestConfiguration.class).in(Singleton.class);
        bind(IResource.class).to(Resource.class).in(Singleton.class);

        bind(IMediaService.class).to(MediaService.class).in(Singleton.class);
        bind(IMediaIndex.class).to(MediaIndex.class).in(Singleton.class);

        bind(IMimeTypeFactory.class).to(MimeTypeFactory.class).in(Singleton.class);

    }

}

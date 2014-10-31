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

package net.holmes.core.business.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import net.holmes.core.business.configuration.ConfigurationDao;
import net.holmes.core.business.configuration.XmlConfigurationDaoImpl;
import net.holmes.core.business.media.MediaManager;
import net.holmes.core.business.media.MediaManagerImpl;
import net.holmes.core.business.media.dao.MediaDao;
import net.holmes.core.business.media.dao.MediaDaoImpl;
import net.holmes.core.business.media.dao.index.MediaIndexDao;
import net.holmes.core.business.media.dao.index.MediaIndexDaoImpl;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.business.mimetype.MimeTypeManagerImpl;
import net.holmes.core.business.mimetype.dao.MimeTypeDao;
import net.holmes.core.business.mimetype.dao.MimeTypeDaoImpl;
import net.holmes.core.business.streaming.StreamingManager;
import net.holmes.core.business.streaming.StreamingManagerImpl;
import net.holmes.core.business.streaming.airplay.AirplayStreamerImpl;
import net.holmes.core.business.streaming.airplay.controlpoint.AsyncSocketControlPoint;
import net.holmes.core.business.streaming.airplay.controlpoint.ControlPoint;
import net.holmes.core.business.streaming.device.DeviceDao;
import net.holmes.core.business.streaming.device.DeviceDaoImpl;
import net.holmes.core.business.streaming.device.DeviceStreamer;
import net.holmes.core.business.streaming.session.SessionDao;
import net.holmes.core.business.streaming.session.SessionDaoImpl;
import net.holmes.core.business.streaming.upnp.UpnpStreamerImpl;
import net.holmes.core.business.version.VersionManager;
import net.holmes.core.business.version.VersionManagerImpl;
import net.holmes.core.business.version.release.ReleaseDao;
import net.holmes.core.business.version.release.ReleaseDaoImpl;

import static com.google.inject.name.Names.named;

/**
 * Holmes business Guice injector.
 */
public class BusinessInjector extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        // Bind dao
        bind(ConfigurationDao.class).to(XmlConfigurationDaoImpl.class).in(Singleton.class);
        bind(MediaDao.class).to(MediaDaoImpl.class).in(Singleton.class);
        bind(MediaIndexDao.class).to(MediaIndexDaoImpl.class).in(Singleton.class);
        bind(DeviceDao.class).to(DeviceDaoImpl.class).in(Singleton.class);
        bind(SessionDao.class).to(SessionDaoImpl.class).in(Singleton.class);
        bind(ReleaseDao.class).to(ReleaseDaoImpl.class).in(Singleton.class);
        bind(MimeTypeDao.class).to(MimeTypeDaoImpl.class).in(Singleton.class);

        // Bind business managers
        bind(MimeTypeManager.class).to(MimeTypeManagerImpl.class).in(Singleton.class);
        bind(MediaManager.class).to(MediaManagerImpl.class).in(Singleton.class);
        bind(StreamingManager.class).to(StreamingManagerImpl.class).in(Singleton.class);
        bind(VersionManager.class).to(VersionManagerImpl.class).in(Singleton.class);


        // Bind streaming utils
        bind(DeviceStreamer.class).annotatedWith(named("upnp")).to(UpnpStreamerImpl.class).in(Singleton.class);
        bind(DeviceStreamer.class).annotatedWith(named("airplay")).to(AirplayStreamerImpl.class).in(Singleton.class);
        bind(ControlPoint.class).to(AsyncSocketControlPoint.class);
    }
}

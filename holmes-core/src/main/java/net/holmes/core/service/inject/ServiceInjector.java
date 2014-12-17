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

package net.holmes.core.service.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import net.holmes.core.service.ReleaseCheckService;
import net.holmes.core.service.Service;
import net.holmes.core.service.airplay.AirplayService;
import net.holmes.core.service.http.HttpFileRequestDecoder;
import net.holmes.core.service.http.HttpFileRequestHandler;
import net.holmes.core.service.http.HttpService;
import net.holmes.core.service.systray.SystrayService;
import net.holmes.core.service.upnp.UpnpService;

import javax.net.SocketFactory;

import static com.google.inject.name.Names.named;

/**
 * Holmes service Guice injector.
 */
public class ServiceInjector extends AbstractModule {
    private final SocketFactory socketFactory;

    /**
     * Instantiates a new service injector.
     */
    public ServiceInjector() {
        socketFactory = SocketFactory.getDefault();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        // Bind socket factory
        bind(SocketFactory.class).toInstance(socketFactory);

        // Bind services
        bind(Service.class).annotatedWith(named("http")).to(HttpService.class);
        bind(Service.class).annotatedWith(named("upnp")).to(UpnpService.class);
        bind(Service.class).annotatedWith(named("airplay")).to(AirplayService.class);
        bind(Service.class).annotatedWith(named("systray")).to(SystrayService.class);
        bind(Service.class).annotatedWith(named("release")).to(ReleaseCheckService.class);

        // Bind Upnp service
        bind(org.fourthline.cling.UpnpService.class).toProvider(UpnpServiceProvider.class).in(Singleton.class);

        // Bind Http file request decoder and handler
        bind(HttpFileRequestDecoder.class);
        bind(HttpFileRequestHandler.class);
    }
}

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
import net.holmes.core.service.upnp.UpnpServiceProvider;

import static com.google.inject.name.Names.named;

/**
 * Holmes business Guice injector.
 */
public class ServiceInjector extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        // Bind services
        bind(Service.class).annotatedWith(named("http")).to(HttpService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(named("upnp")).to(UpnpService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(named("airplay")).to(AirplayService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(named("systray")).to(SystrayService.class).in(Singleton.class);
        bind(Service.class).annotatedWith(named("release")).to(ReleaseCheckService.class).in(Singleton.class);

        // Bind Upnp service
        bind(org.fourthline.cling.UpnpService.class).toProvider(UpnpServiceProvider.class).in(Singleton.class);

        // Bind Http file request decoder and handler
        bind(HttpFileRequestDecoder.class);
        bind(HttpFileRequestHandler.class);
    }
}

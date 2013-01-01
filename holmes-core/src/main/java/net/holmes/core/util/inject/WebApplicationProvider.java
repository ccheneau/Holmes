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
package net.holmes.core.util.inject;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationFactory;

/**
 * Guice provider for Jersey web application
 */
public class WebApplicationProvider implements Provider<WebApplication> {

    private final Injector injector;

    @Inject
    public WebApplicationProvider(Injector injector) {
        this.injector = injector;
    }

    @Override
    public WebApplication get() {
        // Jersey initialization
        WebApplication application = WebApplicationFactory.createWebApplication();

        // Set web application properties
        Map<String, Object> props = Maps.newHashMap();
        props.put(PackagesResourceConfig.PROPERTY_PACKAGES, "net.holmes.core.backend");
        props.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        // Initialize web application
        ResourceConfig rcf = new PackagesResourceConfig(props);
        application.initiate(rcf, new GuiceComponentProviderFactory(rcf, injector));

        return application;
    }
}

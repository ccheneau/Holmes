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
package net.holmes.core;

import net.holmes.core.configuration.Configuration;
import net.holmes.core.configuration.TestConfiguration;
import net.holmes.core.media.MediaService;
import net.holmes.core.media.MediaServiceImpl;
import net.holmes.core.media.index.MediaIndex;
import net.holmes.core.media.index.MediaIndexImpl;
import net.holmes.core.util.bundle.Bundle;
import net.holmes.core.util.bundle.BundleImpl;
import net.holmes.core.util.inject.LoggerTypeListener;
import net.holmes.core.util.mimetype.MimeTypeFactory;
import net.holmes.core.util.mimetype.MimeTypeFactoryImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new LoggerTypeListener());

        bind(Configuration.class).to(TestConfiguration.class).in(Singleton.class);
        bind(Bundle.class).to(BundleImpl.class).in(Singleton.class);

        bind(MediaService.class).to(MediaServiceImpl.class).in(Singleton.class);
        bind(MediaIndex.class).to(MediaIndexImpl.class).in(Singleton.class);

        bind(MimeTypeFactory.class).to(MimeTypeFactoryImpl.class).in(Singleton.class);
    }
}

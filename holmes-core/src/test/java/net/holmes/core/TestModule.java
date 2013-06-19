/*
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

import com.google.common.cache.Cache;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.mimetype.MimeTypeManager;
import net.holmes.core.common.mimetype.MimeTypeManagerImpl;
import net.holmes.core.inject.CustomTypeListener;
import net.holmes.core.inject.provider.ImageCacheProvider;
import net.holmes.core.inject.provider.PodcastCacheProvider;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.MediaManagerImpl;
import net.holmes.core.media.index.MediaIndexManager;
import net.holmes.core.media.index.MediaIndexManagerImpl;
import net.holmes.core.media.model.AbstractNode;

import java.util.List;
import java.util.ResourceBundle;

public class TestModule extends AbstractModule {
    private final EventBus eventBus = new EventBus("Holmes EventBus");
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("message");

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new CustomTypeListener(eventBus));

        bind(Configuration.class).to(TestConfiguration.class).in(Singleton.class);
        bind(ResourceBundle.class).toInstance(resourceBundle);

        bind(MediaManager.class).to(MediaManagerImpl.class).in(Singleton.class);
        bind(MediaIndexManager.class).to(MediaIndexManagerImpl.class).in(Singleton.class);

        bind(MimeTypeManager.class).to(MimeTypeManagerImpl.class).in(Singleton.class);

        bind(new TypeLiteral<Cache<String, List<AbstractNode>>>() {
        }).annotatedWith(Names.named("podcastCache")).toProvider(PodcastCacheProvider.class).in(Singleton.class);
        bind(new TypeLiteral<Cache<String, String>>() {
        }).annotatedWith(Names.named("imageCache")).toProvider(ImageCacheProvider.class).in(Singleton.class);
    }
}

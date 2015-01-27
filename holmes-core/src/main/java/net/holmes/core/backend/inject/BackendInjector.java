/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

package net.holmes.core.backend.inject;

import com.google.inject.AbstractModule;
import net.holmes.core.backend.exception.BackendExceptionMapper;
import net.holmes.core.backend.handler.*;
import net.holmes.core.backend.manager.BackendManager;
import net.holmes.core.backend.manager.BackendManagerImpl;

/**
 * Holmes backend Guice injector.
 */
public class BackendInjector extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        // Bind backend
        bind(BackendManager.class).to(BackendManagerImpl.class);

        // Bind Rest handlers
        bind(AudioFoldersHandler.class);
        bind(PictureFoldersHandler.class);
        bind(PodcastsHandler.class);
        bind(SettingsHandler.class);
        bind(UtilHandler.class);
        bind(VideoFoldersHandler.class);
        bind(BackendExceptionMapper.class);
        bind(StreamingHandler.class);

    }
}

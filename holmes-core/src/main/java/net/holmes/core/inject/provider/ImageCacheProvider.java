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

package net.holmes.core.inject.provider;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import net.holmes.common.configuration.Configuration;
import net.holmes.common.configuration.Parameter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author Cedric
 *
 */
public class ImageCacheProvider implements Provider<Cache<File, String>> {

    private final Configuration configuration;

    /**
     * Instantiates a new image cache provider.
     *
     * @param configuration the configuration
     */
    @Inject
    public ImageCacheProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Cache<File, String> get() {
        return CacheBuilder.newBuilder() //
                .maximumSize(configuration.getIntParameter(Parameter.IMAGE_CACHE_MAX_ELEMENTS)) //
                .expireAfterWrite(configuration.getIntParameter(Parameter.IMAGE_CACHE_EXPIRE_HOURS), TimeUnit.HOURS) //
                .build();
    }
}

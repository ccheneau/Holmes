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

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Type listener for slf4j logger injection
 */
public class LoggerListener implements TypeListener {

    public LoggerListener() {
    }

    @Override
    public <T> void hear(TypeLiteral<T> type, TypeEncounter<T> encounter) {
        if (type.getRawType().isAnnotationPresent(Loggable.class)) {
            for (Field field : type.getRawType().getDeclaredFields()) {
                if (field.getType() == Logger.class) {
                    encounter.register(new Slf4jMembersInjector<T>(field));
                    break;
                }

            }
        }
    }

    private static class Slf4jMembersInjector<T> implements MembersInjector<T> {
        private final Field field;
        private final Logger logger;

        public Slf4jMembersInjector(Field field) {
            this.field = field;
            this.logger = LoggerFactory.getLogger(field.getDeclaringClass());
            this.field.setAccessible(true);
        }

        @Override
        public void injectMembers(T t) {
            try {
                field.set(t, logger);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

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
package net.holmes.core.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.holmes.common.inject.Loggable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Guice type listener for slf4j logger injection and event bus registration 
 */
public final class CustomTypeListener implements TypeListener {

    private final EventBus eventBus;

    public CustomTypeListener(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public <T> void hear(TypeLiteral<T> type, TypeEncounter<T> encounter) {
        // Inject slf4j logger
        if (type.getRawType().isAnnotationPresent(Loggable.class)) {
            for (Field field : type.getRawType().getDeclaredFields()) {
                if (field.getType() == Logger.class) {
                    encounter.register(new Slf4jMembersInjector<T>(field));
                    break;
                }
            }
        }

        // Register to event bus
        for (Method method : type.getRawType().getMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                encounter.register(new EventBusRegisterListener<T>(eventBus));
                break;
            }
        }
    }

    /**
     * Inject SLF4J logger
     */
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

    /**
     * Register class to event bus
     */
    private static class EventBusRegisterListener<T> implements InjectionListener<T> {
        private final EventBus eventBus;

        public EventBusRegisterListener(EventBus eventBus) {
            this.eventBus = eventBus;
        }

        @Override
        public void afterInjection(T injectee) {
            eventBus.register(injectee);
        }
    }
}

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

package net.holmes.core.common;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Method;

/**
 * Guice type listener: automatically register class to eventBus when a method is annotated with @Subscribe
 */
public final class EventBusListener implements TypeListener {
    private final EventBus eventBus;

    /**
     * Instantiates a new eventBus listener.
     *
     * @param eventBus event bus
     */
    public EventBusListener(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void hear(final TypeLiteral<T> type, final TypeEncounter<T> encounter) {
        // Register to event bus
        for (Method method : type.getRawType().getMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                encounter.register(new EventBusRegisterListener<T>(eventBus));
                break;
            }
        }
    }

    /**
     * Register class to event bus.
     */
    private static class EventBusRegisterListener<T> implements InjectionListener<T> {
        private final EventBus eventBus;

        /**
         * Instantiates a new event bus register listener.
         *
         * @param eventBus event bus
         */
        public EventBusRegisterListener(final EventBus eventBus) {
            this.eventBus = eventBus;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void afterInjection(final T injectee) {
            eventBus.register(injectee);
        }
    }
}

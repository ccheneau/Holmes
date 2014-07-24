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

package net.holmes.core.business.streaming.session;

import java.util.Map;

import static com.google.common.collect.Maps.newConcurrentMap;


/**
 * Streaming session dao implementation.
 */
public final class SessionDaoImpl implements SessionDao {
    private final Map<String, StreamingSession> sessions;

    /**
     * Instantiates a new streaming session dao implementation.
     */
    public SessionDaoImpl() {
        this.sessions = newConcurrentMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initSession(final String deviceId, final String contentUrl, final String contentName) {
        sessions.put(deviceId, new StreamingSession(contentName, contentUrl));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSessionStatus(final String deviceId, final SessionStatus status) throws UnknownSessionException {
        StreamingSession session = getSession(deviceId);
        session.setStatus(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSessionPosition(final String deviceId, final Long position, final Long duration) throws UnknownSessionException {
        StreamingSession session = getSession(deviceId);

        // If duration is already set and end of streaming is reached, update session's status
        if (session.getDuration() > 0 && (position >= duration || duration == 0)) {
            session.setStatus(SessionStatus.WAITING);
        }

        // Update position and duration
        session.setPosition(position);
        session.setDuration(duration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeDevice(final String deviceId) {
        sessions.remove(deviceId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamingSession getSession(final String deviceId) throws UnknownSessionException {
        StreamingSession session = sessions.get(deviceId);
        if (session == null) {
            throw new UnknownSessionException(deviceId);
        }
        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, StreamingSession> getSessions() {
        return sessions;
    }
}

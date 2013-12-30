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

package net.holmes.core.transport.session;

import com.google.common.collect.Maps;

import java.util.Map;

import static net.holmes.core.transport.session.SessionStatus.WAITING;

/**
 * Streaming session DAO implementation.
 */
public class SessionDaoImpl implements SessionDao {
    private final Map<String, StreamingSession> sessions;

    /**
     * Instantiates a new streaming session DAO implementation.
     */
    public SessionDaoImpl() {
        this.sessions = Maps.newConcurrentMap();
    }

    @Override
    public void initSession(final String deviceId, final String contentUrl, final String contentName) {
        StreamingSession session = new StreamingSession();
        session.setContentUrl(contentUrl);
        session.setContentName(contentName);
        session.setStatus(WAITING);
        session.setPosition(0l);
        session.setDuration(0l);
        sessions.put(deviceId, session);
    }

    @Override
    public void updateSessionStatus(final String deviceId, final SessionStatus status) throws UnknownSessionException {
        StreamingSession session = getSession(deviceId);
        session.setStatus(status);
    }

    @Override
    public void updateSessionPosition(final String deviceId, final Long position, final Long duration) throws UnknownSessionException {
        StreamingSession session = getSession(deviceId);

        // If duration is already set and end of streaming is reached, update session's status
        if (session.getDuration() > 0 && (position.equals(duration) || duration == 0))
            session.setStatus(WAITING);

        // Update position and duration
        session.setPosition(position);
        session.setDuration(duration);
    }

    @Override
    public void removeDevice(final String deviceId) {
        sessions.remove(deviceId);
    }

    @Override
    public StreamingSession getSession(final String deviceId) throws UnknownSessionException {
        StreamingSession session = sessions.get(deviceId);
        if (session == null) throw new UnknownSessionException(deviceId);
        return session;
    }

    @Override
    public Map<String, StreamingSession> getSessions() {
        return sessions;
    }
}

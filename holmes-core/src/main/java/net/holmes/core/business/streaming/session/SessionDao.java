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

/**
 * Streaming session dao.
 */
public interface SessionDao {

    /**
     * Init streaming session on device.
     *
     * @param deviceId    device Id
     * @param contentUrl  content url
     * @param contentName content name
     */
    void initSession(String deviceId, String contentUrl, String contentName);

    /**
     * Update session status.
     *
     * @param deviceId device Id
     * @param status   session status
     * @throws UnknownSessionException
     */
    void updateSessionStatus(String deviceId, SessionStatus status) throws UnknownSessionException;

    /**
     * Update session position.
     *
     * @param deviceId device Id
     * @param position position
     * @param duration duration
     * @throws UnknownSessionException
     */
    void updateSessionPosition(String deviceId, Long position, Long duration) throws UnknownSessionException;

    /**
     * Remove device.
     *
     * @param deviceId device Id
     */
    void removeDevice(String deviceId);

    /**
     * Get streaming session on device.
     *
     * @param deviceId device Id
     * @return streaming session on device
     * @throws UnknownSessionException
     */
    StreamingSession getSession(String deviceId) throws UnknownSessionException;

    /**
     * Get all streaming sessions.
     *
     * @return streaming sessions map
     */
    Map<String, StreamingSession> getSessions();
}

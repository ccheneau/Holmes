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

package net.holmes.core.transport.session;

import org.junit.Test;

import java.util.Map;

import static net.holmes.core.transport.session.SessionStatus.PLAYING;
import static net.holmes.core.transport.session.SessionStatus.WAITING;
import static org.junit.Assert.*;

public class SessionDaoImplTest {

    @Test
    public void testInitSession() throws UnknownSessionException {
        SessionDaoImpl sessionDao = new SessionDaoImpl();
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        Map<String, StreamingSession> sessions = sessionDao.getSessions();
        StreamingSession session = sessionDao.getSession("deviceId");
        assertNotNull(sessions);
        assertEquals(1, sessions.size());
        assertNotNull(session);
        assertEquals("contentUrl", session.getContentUrl());
        assertEquals("contentName", session.getContentName());
        assertTrue(session.getDuration() == 0l);
        assertTrue(session.getPosition() == 0l);
        assertTrue(session.getStatus() == WAITING);
        assertNotNull(session.toString());
    }

    @Test
    public void testRemoveDevice() throws UnknownSessionException {
        SessionDaoImpl sessionDao = new SessionDaoImpl();
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        Map<String, StreamingSession> sessions = sessionDao.getSessions();
        assertNotNull(sessions);
        assertEquals(1, sessions.size());
        sessionDao.removeDevice("deviceId");
        sessions = sessionDao.getSessions();
        assertEquals(0, sessions.size());
    }

    @Test(expected = UnknownSessionException.class)
    public void testGetUnknownSession() throws UnknownSessionException {
        SessionDaoImpl sessionDao = new SessionDaoImpl();
        sessionDao.getSession("deviceId");
    }

    @Test
    public void testUpdateSessionStatus() throws UnknownSessionException {
        SessionDaoImpl sessionDao = new SessionDaoImpl();
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        sessionDao.updateSessionStatus("deviceId", PLAYING);
        StreamingSession session = sessionDao.getSession("deviceId");
        assertNotNull(session);
        assertTrue(session.getStatus() == PLAYING);
    }

    @Test
    public void testUpdateSessionPosition() throws UnknownSessionException {
        SessionDaoImpl sessionDao = new SessionDaoImpl();
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        sessionDao.updateSessionPosition("deviceId", 1l, 2l);
        StreamingSession session = sessionDao.getSession("deviceId");
        assertNotNull(session);
        assertTrue(session.getPosition() == 1l);
        assertTrue(session.getDuration() == 2l);
    }

    @Test
    public void testUpdateSessionPositionEndOfStreaming() throws UnknownSessionException {
        SessionDaoImpl sessionDao = new SessionDaoImpl();
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        sessionDao.updateSessionPosition("deviceId", 1l, 2l);
        sessionDao.updateSessionPosition("deviceId", 3l, 2l);
        StreamingSession session = sessionDao.getSession("deviceId");
        assertNotNull(session);
        assertTrue(session.getStatus() == WAITING);
    }

    @Test
    public void testUpdateSessionPositionEndOfStreaming2() throws UnknownSessionException {
        SessionDaoImpl sessionDao = new SessionDaoImpl();
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        sessionDao.updateSessionPosition("deviceId", 1l, 2l);
        sessionDao.updateSessionPosition("deviceId", 0l, 0l);
        StreamingSession session = sessionDao.getSession("deviceId");
        assertNotNull(session);
        assertTrue(session.getStatus() == WAITING);
    }

    @Test
    public void testUpdateSessionPositionEndOfStreaming3() throws UnknownSessionException {
        SessionDaoImpl sessionDao = new SessionDaoImpl();
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        sessionDao.updateSessionPosition("deviceId", 1l, 2l);
        sessionDao.updateSessionPosition("deviceId", -1l, 0l);
        StreamingSession session = sessionDao.getSession("deviceId");
        assertNotNull(session);
        assertTrue(session.getStatus() == WAITING);
    }

    @Test
    public void testUpdateSessionPositionEndOfStreaming4() throws UnknownSessionException {
        SessionDaoImpl sessionDao = new SessionDaoImpl();
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        sessionDao.updateSessionPosition("deviceId", 1l, 2l);
        sessionDao.updateSessionPosition("deviceId", 0l, 2l);
        StreamingSession session = sessionDao.getSession("deviceId");
        assertNotNull(session);
        assertTrue(session.getStatus() == WAITING);
    }

}

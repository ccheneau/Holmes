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

package net.holmes.core.business.configuration;

import com.google.common.collect.Lists;
import net.holmes.core.business.configuration.dao.ConfigurationDao;
import net.holmes.core.business.configuration.exception.UnknownNodeException;
import net.holmes.core.business.configuration.model.ConfigurationNode;
import net.holmes.core.common.ConfigurationParameter;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.holmes.core.business.media.model.RootNode.ROOT;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class ConfigurationManagerImplTest {

    @Test
    public void testGetNodes() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getNodes(eq(ROOT))).andReturn(null);

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        List<ConfigurationNode> result = configurationManager.getNodes(ROOT);

        verify(configurationDao);
        assertNull(result);
    }

    @Test
    public void testGetNode() throws UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode configurationNode = new ConfigurationNode("nodeId", "label", "path");

        expect(configurationDao.getNode(eq(ROOT), eq("nodeId"))).andReturn(configurationNode);

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        ConfigurationNode result = configurationManager.getNode(ROOT, "nodeId");

        verify(configurationDao);

        assertEquals(result, configurationNode);
    }

    @Test(expected = UnknownNodeException.class)
    public void testGetUnknownNode() throws UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        expect(configurationDao.getNode(eq(ROOT), eq("nodeId"))).andThrow(new UnknownNodeException("nodeId"));

        replay(configurationDao);

        try {
            ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
            configurationManager.getNode(ROOT, "nodeId");
        } finally {
            verify(configurationDao);
        }
    }

    @Test
    public void testFindNode() throws UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode configurationNode = new ConfigurationNode("nodeId", "label", "path");

        expect(configurationDao.findNode(eq(ROOT), eq("excludedNodeId"), eq("label"), eq("path"))).andReturn(Optional.of(configurationNode));

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        Optional<ConfigurationNode> result = configurationManager.findNode(ROOT, "excludedNodeId", "label", "path");

        verify(configurationDao);

        assertTrue(result.isPresent());
        assertEquals(result.get(), configurationNode);
    }

    @Test
    public void testSave() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        configurationDao.save();
        expectLastCall();

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        configurationManager.save();

        verify(configurationDao);
    }

    @Test(expected = IOException.class)
    public void testSaveIOException() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        configurationDao.save();
        expectLastCall().andThrow(new IOException());

        replay(configurationDao);

        try {
            ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
            configurationManager.save();
        } finally {
            verify(configurationDao);
        }
    }

    @Test
    public void testGetParameter() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        Integer port = 8085;

        expect(configurationDao.getParameter(eq(ConfigurationParameter.HTTP_SERVER_PORT))).andReturn(port);
        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        Integer result = configurationManager.getParameter(ConfigurationParameter.HTTP_SERVER_PORT);

        verify(configurationDao);

        assertEquals(result, port);
    }

    @Test
    public void testSetParameter() {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        Integer port = 8085;

        configurationDao.setParameter(eq(ConfigurationParameter.HTTP_SERVER_PORT), eq(port));
        expectLastCall();

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        configurationManager.setParameter(ConfigurationParameter.HTTP_SERVER_PORT, port);

        verify(configurationDao);
    }

    @Test
    public void testAddNode() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode configurationNode = new ConfigurationNode("nodeId", "label", "path");

        expect(configurationDao.getNodes(eq(ROOT))).andReturn(new ArrayList<>(0));
        configurationDao.save();
        expectLastCall();

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        configurationManager.addNode(ROOT, configurationNode);

        verify(configurationDao);
    }

    @Test
    public void testAddExistingNode() throws IOException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode configurationNode = new ConfigurationNode("nodeId", "label", "path");

        expect(configurationDao.getNodes(eq(ROOT))).andReturn(Lists.newArrayList(configurationNode));

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        configurationManager.addNode(ROOT, configurationNode);

        verify(configurationDao);
    }

    @Test
    public void testEditNode() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode newNode = new ConfigurationNode("nodeId", "label", "path");
        ConfigurationNode oldNode = new ConfigurationNode("nodeId", "oldLabel", "oldPath");

        expect(configurationDao.getNode(eq(ROOT), eq(newNode.getId()))).andReturn(oldNode);
        configurationDao.save();
        expectLastCall();

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        Optional<ConfigurationNode> result = configurationManager.editNode(ROOT, newNode.getId(), newNode.getLabel(), newNode.getPath());

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get(), newNode);

        verify(configurationDao);
    }

    @Test(expected = UnknownNodeException.class)
    public void testEditUnknownNode() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode newNode = new ConfigurationNode("nodeId", "label", "path");

        expect(configurationDao.getNode(eq(ROOT), eq(newNode.getId()))).andThrow(new UnknownNodeException("nodeId"));

        replay(configurationDao);
        try {
            ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
            configurationManager.editNode(ROOT, newNode.getId(), newNode.getLabel(), newNode.getPath());
        } finally {
            verify(configurationDao);
        }
    }

    @Test(expected = IOException.class)
    public void testEditNodeIOException() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode newNode = new ConfigurationNode("nodeId", "label", "path");
        ConfigurationNode oldNode = new ConfigurationNode("nodeId", "oldLabel", "oldPath");

        expect(configurationDao.getNode(eq(ROOT), eq(newNode.getId()))).andReturn(oldNode);
        configurationDao.save();
        expectLastCall().andThrow(new IOException());

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        configurationManager.editNode(ROOT, newNode.getId(), newNode.getLabel(), newNode.getPath());

        verify(configurationDao);
    }

    @Test
    public void testEditNodeWithSameLabel() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode newNode = new ConfigurationNode("nodeId", "label", "path");
        ConfigurationNode oldNode = new ConfigurationNode("nodeId", "label", "oldPath");

        expect(configurationDao.getNode(eq(ROOT), eq(newNode.getId()))).andReturn(oldNode);
        configurationDao.save();
        expectLastCall();

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        Optional<ConfigurationNode> result = configurationManager.editNode(ROOT, newNode.getId(), newNode.getLabel(), newNode.getPath());

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get(), newNode);

        verify(configurationDao);
    }

    @Test
    public void testEditNodeWithSamePath() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode newNode = new ConfigurationNode("nodeId", "label", "path");
        ConfigurationNode oldNode = new ConfigurationNode("nodeId", "oldLabel", "path");

        expect(configurationDao.getNode(eq(ROOT), eq(newNode.getId()))).andReturn(oldNode);
        configurationDao.save();
        expectLastCall();

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        Optional<ConfigurationNode> result = configurationManager.editNode(ROOT, newNode.getId(), newNode.getLabel(), newNode.getPath());

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get(), newNode);

        verify(configurationDao);
    }

    @Test
    public void testEditNodeWithSameLabelAndPath() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode newNode = new ConfigurationNode("nodeId", "label", "path");
        ConfigurationNode oldNode = new ConfigurationNode("nodeId", "label", "path");

        expect(configurationDao.getNode(eq(ROOT), eq(newNode.getId()))).andReturn(oldNode);

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        Optional<ConfigurationNode> result = configurationManager.editNode(ROOT, newNode.getId(), newNode.getLabel(), newNode.getPath());

        assertNotNull(result);
        assertFalse(result.isPresent());

        verify(configurationDao);
    }

    @Test
    public void testRemoveNode() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode configurationNode = new ConfigurationNode("nodeId", "label", "path");

        expect(configurationDao.getNode(eq(ROOT), eq(configurationNode.getId()))).andReturn(configurationNode);
        expect(configurationDao.getNodes(eq(ROOT))).andReturn(Lists.newArrayList(configurationNode));
        configurationDao.save();
        expectLastCall();

        replay(configurationDao);

        ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
        ConfigurationNode result = configurationManager.removeNode(configurationNode.getId(), ROOT);

        assertNotNull(result);
        assertEquals(result, configurationNode);

        verify(configurationDao);
    }

    @Test(expected = UnknownNodeException.class)
    public void testRemoveUnknownNode() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode configurationNode = new ConfigurationNode("nodeId", "label", "path");

        expect(configurationDao.getNode(eq(ROOT), eq(configurationNode.getId()))).andThrow(new UnknownNodeException("nodeId"));

        replay(configurationDao);

        try {
            ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
            configurationManager.removeNode(configurationNode.getId(), ROOT);
        } finally {
            verify(configurationDao);
        }
    }

    @Test(expected = IOException.class)
    public void testRemoveNodeIOException() throws IOException, UnknownNodeException {
        ConfigurationDao configurationDao = createMock(ConfigurationDao.class);

        ConfigurationNode configurationNode = new ConfigurationNode("nodeId", "label", "path");

        expect(configurationDao.getNode(eq(ROOT), eq(configurationNode.getId()))).andReturn(configurationNode);
        expect(configurationDao.getNodes(eq(ROOT))).andReturn(Lists.newArrayList(configurationNode));
        configurationDao.save();
        expectLastCall().andThrow(new IOException());

        replay(configurationDao);

        try {
            ConfigurationManager configurationManager = new ConfigurationManagerImpl(configurationDao);
            configurationManager.removeNode(configurationNode.getId(), ROOT);
        } finally {
            verify(configurationDao);
        }
    }
}

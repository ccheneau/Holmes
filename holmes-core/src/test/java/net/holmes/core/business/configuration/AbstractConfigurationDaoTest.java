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

package net.holmes.core.business.configuration;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static net.holmes.core.business.media.model.RootNode.PICTURE;
import static org.easymock.EasyMock.*;

public class AbstractConfigurationDaoTest {

    @Test
    public void testAddNode() throws IOException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        expect(configurationDao.getNodes(PICTURE)).andReturn(new ArrayList<ConfigurationNode>(0));
        configurationDao.save();
        expectLastCall();

        replay(configurationDao);
        configurationDao.addNode(PICTURE, new ConfigurationNode("id", "label", "path"));

        verify(configurationDao);
    }

    @Test(expected = IOException.class)
    public void testAddNodeIOException() throws IOException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        expect(configurationDao.getNodes(PICTURE)).andReturn(new ArrayList<ConfigurationNode>(0));
        configurationDao.save();
        expectLastCall().andThrow(new IOException());

        replay(configurationDao);
        configurationDao.addNode(PICTURE, new ConfigurationNode("id", "label", "path"));

        verify(configurationDao);
    }

    @Test
    public void testAddExistingNode() throws IOException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        expect(configurationDao.getNodes(PICTURE)).andReturn(newArrayList(node));

        replay(configurationDao);
        configurationDao.addNode(PICTURE, node);

        verify(configurationDao);
    }

    @Test
    public void testEditNode() throws IOException, UnknownNodeException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        expect(configurationDao.getNode(PICTURE, "id")).andReturn(node);
        configurationDao.save();
        expectLastCall();

        replay(configurationDao);
        configurationDao.editNode(PICTURE, "id", "newLabel", "newPath");

        verify(configurationDao);
    }

    @Test
    public void testEditNodeSameLabel() throws IOException, UnknownNodeException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        expect(configurationDao.getNode(PICTURE, "id")).andReturn(node);
        configurationDao.save();
        expectLastCall();

        replay(configurationDao);
        configurationDao.editNode(PICTURE, "id", "label", "newPath");

        verify(configurationDao);
    }

    @Test
    public void testEditNodeSamePath() throws IOException, UnknownNodeException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        expect(configurationDao.getNode(PICTURE, "id")).andReturn(node);
        configurationDao.save();
        expectLastCall();

        replay(configurationDao);
        configurationDao.editNode(PICTURE, "id", "newLabel", "path");

        verify(configurationDao);
    }

    @Test
    public void testEditNodeSameLabelAndPath() throws IOException, UnknownNodeException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        expect(configurationDao.getNode(PICTURE, "id")).andReturn(node);

        replay(configurationDao);
        configurationDao.editNode(PICTURE, "id", "label", "path");

        verify(configurationDao);
    }

    @Test(expected = UnknownNodeException.class)
    public void testEditUnknownNode() throws IOException, UnknownNodeException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        expect(configurationDao.getNode(PICTURE, "id")).andThrow(new UnknownNodeException("id", null));

        replay(configurationDao);
        configurationDao.editNode(PICTURE, "id", "newLabel", "newPath");

        verify(configurationDao);
    }

    @Test(expected = IOException.class)
    public void testEditNodeIOException() throws IOException, UnknownNodeException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        expect(configurationDao.getNode(PICTURE, "id")).andReturn(node);
        configurationDao.save();
        expectLastCall().andThrow(new IOException());

        replay(configurationDao);
        configurationDao.editNode(PICTURE, "id", "newLabel", "newPath");

        verify(configurationDao);
    }

    @Test
    public void testRemoveNode() throws IOException, UnknownNodeException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        expect(configurationDao.getNode(PICTURE, "id")).andReturn(node);
        expect(configurationDao.getNodes(PICTURE)).andReturn(newArrayList(node));
        configurationDao.save();
        expectLastCall();

        replay(configurationDao);
        configurationDao.removeNode("id", PICTURE);

        verify(configurationDao);
    }

    @Test(expected = UnknownNodeException.class)
    public void testRemoveUnknownNode() throws IOException, UnknownNodeException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        expect(configurationDao.getNode(PICTURE, "id")).andThrow(new UnknownNodeException("id", null));

        replay(configurationDao);
        configurationDao.removeNode("id", PICTURE);

        verify(configurationDao);
    }


    @Test(expected = IOException.class)
    public void testRemoveNodeIOException() throws IOException, UnknownNodeException {
        AbstractConfigurationDao configurationDao = createMock(AbstractConfigurationDao.class);

        ConfigurationNode node = new ConfigurationNode("id", "label", "path");
        expect(configurationDao.getNode(PICTURE, "id")).andReturn(node);
        expect(configurationDao.getNodes(PICTURE)).andReturn(newArrayList(node));
        configurationDao.save();
        expectLastCall().andThrow(new IOException());

        replay(configurationDao);
        configurationDao.removeNode("id", PICTURE);

        verify(configurationDao);
    }

}

package net.holmes.core.backend.response;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * ConfigurationFolder Tester.
 */
public class ConfigurationFolderTest {

    @Test
    public void testDefaultConstructor() throws Exception {
        ConfigurationFolder folder = new ConfigurationFolder();
        assertNotNull(folder);
    }

    /**
     * Method: getId()
     */
    @Test
    public void testGetId() throws Exception {
        ConfigurationFolder folder = new ConfigurationFolder("id", "name", "path");
        assertEquals(folder.getId(), "id");
    }

    /**
     * Method: setId(final String id)
     */
    @Test
    public void testSetId() throws Exception {
        ConfigurationFolder folder = new ConfigurationFolder("id", "name", "path");
        folder.setId("id2");
        assertEquals(folder.getId(), "id2");
    }

    /**
     * Method: getName()
     */
    @Test
    public void testGetName() throws Exception {
        ConfigurationFolder folder = new ConfigurationFolder("id", "name", "path");
        assertEquals(folder.getName(), "name");
    }

    /**
     * Method: setName(final String name)
     */
    @Test
    public void testSetName() throws Exception {
        ConfigurationFolder folder = new ConfigurationFolder("id", "name", "path");
        folder.setName("name2");
        assertEquals(folder.getName(), "name2");

    }

    /**
     * Method: getPath()
     */
    @Test
    public void testGetPath() throws Exception {
        ConfigurationFolder folder = new ConfigurationFolder("id", "name", "path");
        assertEquals(folder.getPath(), "path");
    }

    /**
     * Method: setPath(final String path)
     */
    @Test
    public void testSetPath() throws Exception {
        ConfigurationFolder folder = new ConfigurationFolder("id", "name", "path");
        folder.setPath("path2");
        assertEquals(folder.getPath(), "path2");

    }

    /**
     * Method: hashCode()
     */
    @Test
    public void testHashCode() throws Exception {
        ConfigurationFolder folder = new ConfigurationFolder("id", "name", "path");
        ConfigurationFolder folder2 = new ConfigurationFolder("id", "name", "path");
        ConfigurationFolder folder3 = new ConfigurationFolder("id3", "name", "path");
        assertEquals(folder.hashCode(), folder2.hashCode());
        assertNotEquals(folder.hashCode(), folder3.hashCode());
    }

    /**
     * Method: equals(Object obj)
     */
    @Test
    public void testEquals() throws Exception {
        ConfigurationFolder folder = new ConfigurationFolder("id", "name", "path");
        ConfigurationFolder folder2 = new ConfigurationFolder("id", "name", "path");
        ConfigurationFolder folder3 = new ConfigurationFolder("id3", "name", "path");
        assertEquals(folder, folder2);
        assertNotEquals(folder, folder3);
    }
}

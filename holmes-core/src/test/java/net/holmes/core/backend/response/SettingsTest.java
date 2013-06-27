package net.holmes.core.backend.response;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Settings Tester.
 */
public class SettingsTest {

    @Test
    public void testDefaultConstructor() throws Exception {
        Settings settings = new Settings();
        assertNotNull(settings);
    }

    /**
     * Method: getServerName()
     */
    @Test
    public void testGetServerName() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        assertEquals(settings.getServerName(), "serverName");
    }

    /**
     * Method: setServerName(final String serverName)
     */
    @Test
    public void testSetServerName() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        settings.setServerName("newServerName");
        assertEquals(settings.getServerName(), "newServerName");
    }

    /**
     * Method: getHttpServerPort()
     */
    @Test
    public void testGetHttpServerPort() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        assertTrue(settings.getHttpServerPort() == 999);
    }

    /**
     * Method: setHttpServerPort(final Integer httpServerPort)
     */
    @Test
    public void testSetHttpServerPort() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        settings.setHttpServerPort(1000);
        assertTrue(settings.getHttpServerPort() == 1000);
    }

    /**
     * Method: getPrependPodcastItem()
     */
    @Test
    public void testGetPrependPodcastItem() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        assertTrue(settings.getPrependPodcastItem());
    }

    /**
     * Method: setPrependPodcastItem(final Boolean prependPodcastItem)
     */
    @Test
    public void testSetPrependPodcastItem() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        settings.setPrependPodcastItem(false);
        assertFalse(settings.getPrependPodcastItem());
    }

    /**
     * Method: getEnableExternalSubtitles()
     */
    @Test
    public void testGetEnableExternalSubtitles() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        assertTrue(settings.getEnableExternalSubtitles());
    }

    /**
     * Method: setEnableExternalSubtitles(final Boolean enableExternalSubtitles)
     */
    @Test
    public void testSetEnableExternalSubtitles() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        settings.setEnableExternalSubtitles(false);
        assertFalse(settings.getEnableExternalSubtitles());
    }

    /**
     * Method: getHideEmptyRootNodes()
     */
    @Test
    public void testGetHideEmptyRootNodes() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        assertTrue(settings.getHideEmptyRootNodes());
    }

    /**
     * Method: setHideEmptyRootNodes(Boolean hideEmptyRootNodes)
     */
    @Test
    public void testSetHideEmptyRootNodes() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        settings.setHideEmptyRootNodes(false);
        assertFalse(settings.getHideEmptyRootNodes());
    }

    /**
     * Method: hashCode()
     */
    @Test
    public void testHashCode() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        Settings settings2 = new Settings("serverName", 999, true, true, true);
        Settings settings3 = new Settings("serverName3", 999, true, true, true);
        assertEquals(settings.hashCode(), settings2.hashCode());
        assertNotEquals(settings.hashCode(), settings3.hashCode());
    }

    /**
     * Method: equals(Object obj)
     */
    @Test
    public void testEquals() throws Exception {
        Settings settings = new Settings("serverName", 999, true, true, true);
        Settings settings2 = new Settings("serverName", 999, true, true, true);
        Settings settings3 = new Settings("serverName3", 999, true, true, true);
        assertEquals(settings, settings2);
        assertNotEquals(settings, settings3);
    }
}

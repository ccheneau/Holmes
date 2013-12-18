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
        Settings settings = new Settings("serverName", true, true);
        assertEquals(settings.getServerName(), "serverName");
    }

    /**
     * Method: setServerName(final String serverName)
     */
    @Test
    public void testSetServerName() throws Exception {
        Settings settings = new Settings("serverName", true, true);
        settings.setServerName("newServerName");
        assertEquals(settings.getServerName(), "newServerName");
    }

    /**
     * Method: getPrependPodcastItem()
     */
    @Test
    public void testGetPrependPodcastItem() throws Exception {
        Settings settings = new Settings("serverName", true, true);
        assertTrue(settings.getPrependPodcastItem());
    }

    /**
     * Method: setPrependPodcastItem(final Boolean prependPodcastItem)
     */
    @Test
    public void testSetPrependPodcastItem() throws Exception {
        Settings settings = new Settings("serverName", true, true);
        settings.setPrependPodcastItem(false);
        assertFalse(settings.getPrependPodcastItem());
    }

    /**
     * Method: getEnableIcecastDirectory()
     */
    @Test
    public void testGetEnableIcecastDirectory() throws Exception {
        Settings settings = new Settings("serverName", true, true);
        assertTrue(settings.getEnableIcecastDirectory());
    }

    /**
     * Method: setEnableIcecastDirectory(final Boolean enableIcecastDirectory)
     */
    @Test
    public void testSetEnableIcecastDirectory() throws Exception {
        Settings settings = new Settings("serverName", true, true);
        settings.setEnableIcecastDirectory(false);
        assertFalse(settings.getEnableIcecastDirectory());
    }
}

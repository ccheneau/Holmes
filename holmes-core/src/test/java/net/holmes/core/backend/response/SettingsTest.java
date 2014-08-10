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
        Settings settings = new Settings("serverName", true);
        assertEquals(settings.getServerName(), "serverName");
    }

    /**
     * Method: setServerName(final String serverName)
     */
    @Test
    public void testSetServerName() throws Exception {
        Settings settings = new Settings("serverName", true);
        settings.setServerName("newServerName");
        assertEquals(settings.getServerName(), "newServerName");
    }

    /**
     * Method: getPrependPodcastItem()
     */
    @Test
    public void testGetPrependPodcastItem() throws Exception {
        Settings settings = new Settings("serverName", true);
        assertTrue(settings.getPrependPodcastItem());
    }

    /**
     * Method: setPrependPodcastItem(final Boolean prependPodcastItem)
     */
    @Test
    public void testSetPrependPodcastItem() throws Exception {
        Settings settings = new Settings("serverName", true);
        settings.setPrependPodcastItem(false);
        assertFalse(settings.getPrependPodcastItem());
    }
}

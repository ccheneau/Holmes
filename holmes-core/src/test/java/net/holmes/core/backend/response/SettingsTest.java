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
        Settings settings = new Settings("serverName");
        assertEquals(settings.getServerName(), "serverName");
    }

    /**
     * Method: setServerName(final String serverName)
     */
    @Test
    public void testSetServerName() throws Exception {
        Settings settings = new Settings("serverName");
        settings.setServerName("newServerName");
        assertEquals(settings.getServerName(), "newServerName");
    }
}

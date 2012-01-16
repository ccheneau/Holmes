/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.test;

import java.util.LinkedList;
import java.util.UUID;

import junit.framework.TestCase;
import net.holmes.core.TestModule;
import net.holmes.core.configuration.ContentFolder;
import net.holmes.core.configuration.IConfiguration;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * The Class ConfigurationTest.
 */
public class ConfigurationTest extends TestCase
{

    /** The configuration. */
    @Inject
    private IConfiguration configuration;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    @Before
    public void setUp()
    {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    /**
     * Test configuration sample.
     */
    @Test
    public void testConfigurationSample()
    {
        try
        {
            configuration.getConfig().setServerName("Sèrveur à moi");
            configuration.getConfig().setLogLevel("DEBUG");

            configuration.getConfig().setVideoFolders(new LinkedList<ContentFolder>());
            ContentFolder videoDirectory = new ContentFolder(UUID.randomUUID().toString(), "myVideos", "C:\\Dev\\upnp-test\\video");
            configuration.getConfig().getVideoFolders().add(videoDirectory);

            configuration.getConfig().setAudioFolders(new LinkedList<ContentFolder>());
            ContentFolder audioDirectory = new ContentFolder(UUID.randomUUID().toString(), "myAudios", "C:\\Dev\\upnp-test\\audio");
            configuration.getConfig().getAudioFolders().add(audioDirectory);

            configuration.getConfig().setPictureFolders(new LinkedList<ContentFolder>());
            ContentFolder pictureDirectory = new ContentFolder(UUID.randomUUID().toString(), "myImages", "C:\\Dev\\upnp-test\\image");
            configuration.getConfig().getPictureFolders().add(pictureDirectory);

            configuration.getConfig().setPodcasts(new LinkedList<ContentFolder>());
            ContentFolder podcastDirectory = new ContentFolder(UUID.randomUUID().toString(), "Cast coders", "http://lescastcodeurs.libsyn.com/rss");
            configuration.getConfig().getPodcasts().add(podcastDirectory);

            configuration.saveConfig();
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }
}

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

public class ConfigurationTest extends TestCase
{
    @Inject
    private IConfiguration configuration;

    @Override
    @Before
    public void setUp()
    {
        Injector injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    @Test
    public void testConfigurationSample()
    {
        try
        {
            configuration.getConfig().setServerName("Sèrveur à");
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

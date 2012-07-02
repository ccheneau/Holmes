package net.holmes.core.configuration;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;

public class TestConfiguration implements IConfiguration {

    private Configuration config = null;

    public TestConfiguration() {
        config = new Configuration();
        config.setVideoFolders(new LinkedList<ConfigurationNode>());
        config.getVideoFolders().add(getTestContentFolder("videosTest", "/videosTest/"));
        config.setAudioFolders(new LinkedList<ConfigurationNode>());
        config.getAudioFolders().add(getTestContentFolder("audiosTest", "/audiosTest/"));
        config.setPictureFolders(new LinkedList<ConfigurationNode>());
        config.getPictureFolders().add(getTestContentFolder("imagesTest", "/imagesTest/"));
        config.setPodcasts(new LinkedList<ConfigurationNode>());
        config.getPodcasts().add(new ConfigurationNode("castcodersTest", "castcodersTest", "http://lescastcodeurs.libsyn.com/rss"));
    }

    @Override
    public void saveConfig() {
    }

    @Override
    public Configuration getConfig() {
        return config;
    }

    @Override
    public String getHomeDirectory() {
        return null;
    }

    @Override
    public String getHomeConfigDirectory() {
        return null;
    }

    @Override
    public String getHomeSiteDirectory() {
        return null;
    }

    private ConfigurationNode getTestContentFolder(String label, String path) {
        ConfigurationNode contentFolder = null;

        URL rs = this.getClass().getResource(path);
        if (rs != null) {
            String fpath = new File(rs.getFile()).getAbsolutePath();
            contentFolder = new ConfigurationNode(label, label, fpath);
        }

        return contentFolder;
    }

}

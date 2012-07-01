package net.holmes.core.configuration;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;

public class TestConfiguration implements IConfiguration {

    private Configuration config = null;

    public TestConfiguration() {
        config = new Configuration();
        config.setVideoFolders(new LinkedList<ContentFolder>());
        config.getVideoFolders().add(getTestContentFolder("videosTest", "/videosTest/"));
        config.setAudioFolders(new LinkedList<ContentFolder>());
        config.getAudioFolders().add(getTestContentFolder("audiosTest", "/audiosTest/"));
        config.setPictureFolders(new LinkedList<ContentFolder>());
        config.getPictureFolders().add(getTestContentFolder("imagesTest", "/imagesTest/"));
        config.setPodcasts(new LinkedList<ContentFolder>());
        config.getPodcasts().add(new ContentFolder("castcodersTest", "castcodersTest", "http://lescastcodeurs.libsyn.com/rss"));
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

    private ContentFolder getTestContentFolder(String label, String path) {
        ContentFolder contentFolder = null;

        URL rs = this.getClass().getResource(path);
        if (rs != null) {
            String fpath = new File(rs.getFile()).getAbsolutePath();
            contentFolder = new ContentFolder(label, label, fpath);
        }

        return contentFolder;
    }

}

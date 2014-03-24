package net.holmes.core.business;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;

public class TestRelease {

    @Test
    public void testGetRelease() throws IOException {
        URLConnection con = new URL("https://api.github.com/repos/ccheneau/Holmes/releases").openConnection();

        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String line = in.readLine();
            while (line != null) {
                content.append(line);
                line = in.readLine();
            }
        }
        Gson gson = new Gson();
        Release[] releases = gson.fromJson(content.toString(), Release[].class);
        for (Release release : releases) {
            System.out.println(release.toString());
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public class Release {
        private String name;
        @SerializedName("tag_name")
        private String tagName;
        private boolean draft;
        private List<ReleaseAsset> assets;
        @SerializedName("published_at")
        private Date publishedDate;

        @Override
        public String toString() {
            return "Release{" +
                    "name='" + name + '\'' +
                    ", tagName='" + tagName + '\'' +
                    ", draft=" + draft +
                    ", assets=" + assets +
                    ", publishedDate=" + publishedDate +
                    '}';
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public class ReleaseAsset {
        private String name;
        private long size;
        @SerializedName("download_count")
        private int downloadCount;

        @Override
        public String toString() {
            return "ReleaseAsset{" +
                    "name='" + name + '\'' +
                    ", size=" + size +
                    ", downloadCount=" + downloadCount +
                    '}';
        }
    }
}

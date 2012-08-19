package net.holmes.core.backend.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Folder implements Serializable {
    private static final long serialVersionUID = 1951551250233853849L;

    private String data;

    private String state;

    private Map<String, String> metadata;

    public Folder() {
        metadata = new HashMap<String, String>();
        state = "closed";
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}

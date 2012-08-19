package net.holmes.core.backend.response;

import java.util.HashMap;
import java.util.Map;

public class SimpleFolder {

    private String data;

    private String state;

    private Map<String, String> metadata;

    public SimpleFolder() {
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

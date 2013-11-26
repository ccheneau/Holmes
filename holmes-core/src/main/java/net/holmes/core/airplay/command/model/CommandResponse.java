package net.holmes.core.airplay.command.model;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * Airplay command response.
 */
public class CommandResponse {

    private final HttpResponseStatus status;
    private final String message;
    private final String content;
    private final Map<String, String> headerMap = new HashMap<>();
    private final Map<String, String> contentParameterMap = new HashMap<>();

    /**
     * Instantiates a new Airplay command response.
     *
     * @param headers headers
     * @param content content
     */
    public CommandResponse(final String headers, final String content) {
        String headerSplit[] = headers.split("\n");
        String responseSplit[] = headerSplit[0].split(" ");
        status = HttpResponseStatus.valueOf(Integer.parseInt(responseSplit[1]));
        message = responseSplit[2];

        for (int i = 1; i < headerSplit.length; i++) {
            String headerValueSplit[] = headerSplit[i].split(":");
            headerMap.put(headerValueSplit[0], headerValueSplit[1].trim());
        }

        this.content = content;
        if (content != null) {
            if ("text/parameters".equalsIgnoreCase(headerMap.get(CONTENT_TYPE))) {
                for (String paramLine : content.split("\n")) {
                    String paramSplit[] = paramLine.split(":");
                    contentParameterMap.put(paramSplit[0], paramSplit[1].trim());
                }
            }
        }
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public Map<String, String> getContentParameterMap() {
        return contentParameterMap;
    }
}

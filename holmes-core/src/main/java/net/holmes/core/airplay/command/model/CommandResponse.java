package net.holmes.core.airplay.command.model;

import java.util.Map;

/**
 * Airplay command response.
 */
public final class CommandResponse {

    private final int statusCode;
    private final String message;
    private final Map<String, String> contentParameters;

    /**
     * Instantiates a new Airplay command response.
     *
     * @param statusCode        status code
     * @param message           message
     * @param contentParameters content parameters
     */
    public CommandResponse(final int statusCode, final String message, final Map<String, String> contentParameters) {
        this.statusCode = statusCode;
        this.message = message;
        this.contentParameters = contentParameters;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getContentParameters() {
        return contentParameters;
    }
}

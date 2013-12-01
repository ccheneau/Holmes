package net.holmes.core.airplay.command.exception;

/**
 * Unknown device exception.
 */
public class UnknownDeviceException extends Exception {

    /**
     * Instantiates a new UnknownDeviceException.
     *
     * @param deviceId device Id
     */
    public UnknownDeviceException(final Integer deviceId) {
        super("Unknown device: " + deviceId);
    }
}

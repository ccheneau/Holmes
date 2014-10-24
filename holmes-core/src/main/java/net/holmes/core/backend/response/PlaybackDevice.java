/*
 * Copyright (C) 2012-2014  Cedric Cheneau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.holmes.core.backend.response;

/**
 * Playback device.
 */
public class PlaybackDevice {

    private String deviceId;
    private String deviceName;
    private String deviceType;
    private boolean videoSupported;
    private boolean audioSupported;
    private boolean imageSupported;
    private boolean slideShowSupported;

    /**
     * Get device id.
     *
     * @return device id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Set device id.
     *
     * @param deviceId new device id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Get device name.
     *
     * @return device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Set device name.
     *
     * @param deviceName new device name
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Get device type.
     *
     * @return device type
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Set device type.
     *
     * @param deviceType new device type
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Check if video is supported on device.
     *
     * @return true if video is supported
     */
    public boolean isVideoSupported() {
        return videoSupported;
    }

    /**
     * Set whether video is supported.
     *
     * @param videoSupported video supported
     */
    public void setVideoSupported(boolean videoSupported) {
        this.videoSupported = videoSupported;
    }

    /**
     * Check if audio is supported on device.
     *
     * @return true if audio is supported
     */
    public boolean isAudioSupported() {
        return audioSupported;
    }

    /**
     * Set whether audio is supported.
     *
     * @param audioSupported audio supported
     */
    public void setAudioSupported(boolean audioSupported) {
        this.audioSupported = audioSupported;
    }

    /**
     * Check if image is supported on device.
     *
     * @return true if image is supported
     */
    public boolean isImageSupported() {
        return imageSupported;
    }

    /**
     * Set whether image is supported.
     *
     * @param imageSupported image supported
     */
    public void setImageSupported(boolean imageSupported) {
        this.imageSupported = imageSupported;
    }

    /**
     * Check if slide show is supported on device.
     *
     * @return true if slide show is supported
     */
    public boolean isSlideShowSupported() {
        return slideShowSupported;
    }

    /**
     * Set whether slide show is supported.
     *
     * @param slideShowSupported slide show supported
     */
    public void setSlideShowSupported(boolean slideShowSupported) {
        this.slideShowSupported = slideShowSupported;
    }
}

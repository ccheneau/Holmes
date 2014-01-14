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
    private boolean videoSupported;
    private boolean audioSupported;
    private boolean imageSupported;
    private boolean slideShowSupported;

    /**
     * Instantiates a new playback device.
     */
    public PlaybackDevice() {
    }

    /**
     * Instantiates a new playback device.
     *
     * @param deviceId           device Id
     * @param deviceName         device name
     * @param videoSupported     is video supported
     * @param audioSupported     is audio supported
     * @param imageSupported     is image supported
     * @param slideShowSupported is slide show supported
     */
    public PlaybackDevice(String deviceId, String deviceName, boolean videoSupported, boolean audioSupported, boolean imageSupported, boolean slideShowSupported) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.videoSupported = videoSupported;
        this.audioSupported = audioSupported;
        this.imageSupported = imageSupported;
        this.slideShowSupported = slideShowSupported;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isVideoSupported() {
        return videoSupported;
    }

    public void setVideoSupported(boolean videoSupported) {
        this.videoSupported = videoSupported;
    }

    public boolean isAudioSupported() {
        return audioSupported;
    }

    public void setAudioSupported(boolean audioSupported) {
        this.audioSupported = audioSupported;
    }

    public boolean isImageSupported() {
        return imageSupported;
    }

    public void setImageSupported(boolean imageSupported) {
        this.imageSupported = imageSupported;
    }

    public boolean isSlideShowSupported() {
        return slideShowSupported;
    }

    public void setSlideShowSupported(boolean slideShowSupported) {
        this.slideShowSupported = slideShowSupported;
    }
}

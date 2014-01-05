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

package net.holmes.core.transport.airplay;

import com.google.common.base.Objects;

import java.math.BigInteger;

/**
 * Airplay device features
 */
public class AirplayFeatures {
    private static final int VIDEO_SUPPORTED_INDEX = 0;
    private static final int AUDIO_SUPPORTED_INDEX = 9;
    private static final int IMAGE_SUPPORTED_INDEX = 1;
    private static final int SLIDE_SHOW_SUPPORTED_INDEX = 5;
    private boolean videoSupported = false;
    private boolean audioSupported = false;
    private boolean imageSupported = false;
    private boolean slideShowSupported = false;

    /**
     * Instantiates a new Airplay features
     *
     * @param features features
     */
    public AirplayFeatures(final String features) {
        if (features != null) {
            String featuresBit = new BigInteger(features.replace("0x", ""), 16).toString(2);
            videoSupported = getBitValue(featuresBit, VIDEO_SUPPORTED_INDEX);
            audioSupported = getBitValue(featuresBit, AUDIO_SUPPORTED_INDEX);
            imageSupported = getBitValue(featuresBit, IMAGE_SUPPORTED_INDEX);
            slideShowSupported = getBitValue(featuresBit, SLIDE_SHOW_SUPPORTED_INDEX);
        }
    }

    /**
     * Get features bit value
     *
     * @param featuresBit features bit
     * @param bitIndex    bit index
     * @return true if bit value is '1'
     */
    private boolean getBitValue(final String featuresBit, final int bitIndex) {
        return featuresBit.length() > bitIndex && featuresBit.charAt(featuresBit.length() - 1 - bitIndex) == '1';
    }

    public boolean isVideoSupported() {
        return videoSupported;
    }

    public boolean isAudioSupported() {
        return audioSupported;
    }

    public boolean isImageSupported() {
        return imageSupported;
    }

    public boolean isSlideShowSupported() {
        return slideShowSupported;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("videoSupported", videoSupported)
                .add("audioSupported", audioSupported)
                .add("imageSupported", imageSupported)
                .add("slideShowSupported", slideShowSupported)
                .toString();
    }
}

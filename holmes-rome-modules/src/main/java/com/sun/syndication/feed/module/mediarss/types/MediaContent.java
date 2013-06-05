/*
 * Copyright (C) 2012-2013  Cedric Cheneau
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

package com.sun.syndication.feed.module.mediarss.types;

import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ToStringBean;

import java.io.Serializable;

/**
 * <strong>&lt;media:content&gt;</strong></p>
 * <p>&lt;media:content&gt; is a sub-element of either &lt;item&gt; or &lt;media:group&gt;.&nbsp;Media objects that are not the same content should not be included in the same &lt;media:group&gt; element.&nbsp;The sequence of these items implies the order of presentation.
 * While many of the attributes appear to be audio/video specific, this element can be used to publish any type of media.
 * It contains 14 attributes, most of which are optional.</p>
 * <p/>
 * <pre>
 *        &lt;media:content
 *               url="http://www.foo.com/movie.mov"
 *               fileSize="12216320"
 *               type="video/quicktime"
 *               medium="video"
 *               isDefault="true"
 *               expression="full"
 *               bitrate="128"
 *               framerate="25"
 *               samplingrate="44.1"
 *               channels="2"
 *               duration="185"
 *               height="200"
 *               width="300"
 *               lang="en" /&gt;</pre>
 *
 * <p><em>url</em> should specify the direct url to the media object. If not included, a &lt;media:player&gt; element must be specified.</p><p><em>fileSize</em> is the number of bytes of the media object. It is an optional attribute.</p>
 * <p><em>type</em> is the standard MIME type of the object. It is an optional attribute.</p>
 *
 * <p><em>medium</em> is the type of object (image | audio | video | document | executable). While this attribute can at times seem redundant if <em>type</em> is supplied, it is included because it simplifies decision making on the reader side,
 * as well as flushes out any ambiguities between MIME type and object type. It is an optional attribute.</p>
 *
 * <p><em>isDefault</em> determines if this is the default object that should be used for the &lt;media:group&gt;.  There should only be one default object per &lt;media:group&gt;. It is an optional attribute.</p>
 *
 * <p><em>expression</em> determines if the object is a sample or the full version of the object, or even if it is a continuous stream (sample | full | nonstop).
 * Default value is 'full'.
 * It is an optional attribute. </p>
 *
 * <p><em>bit rate</em> is the kilobits per second rate of media. It is an optional attribute.</p>
 * <p><em>frame rate</em> is the number of frames per second for the media object. It is an optional attribute.</p>
 * <p><em>sampling rate</em> is the number of samples per second taken to create the media object. It is expressed in thousands of samples per second (kHz). It is an optional attribute.</p>
 * <p><em>channels</em> is number of audio channels in the media object. It is an optional attribute.
 * </p><p><em>duration</em> is the number of seconds the media object plays. It is an optional attribute.</p>
 *
 *
 *
 *
 *
 * <p><em>height</em> is the height of the media object. It is an optional attribute.</p>
 * <p><em>width</em> is the width of the media object. It is an optional attribute.</p>
 *
 * <p><em>lang</em> is the primary language encapsulated in the media object. Language codes possible are detailed in RFC 3066. This attribute is used similar to the <em>xml:lang</em> attribute detailed in the XML 1.0 Specification (Third Edition). It is an optional attribute.</p>
 *
 *
 * <p> These optional attributes, along with the optional elements below, contain the primary metadata entries needed to index and organize media content.
 * Additional supported attributes for describing images, audio, and video may be added in future revisions of this document.</p>
 *
 * @author Nathanial X. Freitas
 *         <p/>
 *         MediaContent corresponds to the <madia:content> element defined within the MediaRSS specification.
 *         There may be one or more <media:content> instances within each instance of an <item> within an
 *         RSS 2.0 document.
 */
public final class MediaContent implements Serializable, Cloneable {
    private static final long serialVersionUID = -4990262574794352616L;
    private Expression expression;
    private Float bitrate = null;
    private Float framerate = null;
    private Float samplingrate = null;
    private Integer audioChannels = null;
    /*the height in pixels of the resource*/
    private Integer height = null;
    /*the width in pixels of the resource*/
    private Integer width = null;
    /*the duration in seconds of the resource*/
    private Long duration = null;
    /*the file size in bytes of the resource*/
    private Long fileSize = null;
    private Metadata metadata;
    private PlayerReference player;
    private Reference reference;
    private String language;
    /*the MIME type of the resource*/
    private String type = null;
    private boolean defaultContent;

    /**
     * Creates a new MediaContent
     *
     * @param reference UrlReference or Player reference for the item.
     */
    public MediaContent(final Reference reference) {
        super();

        if (reference == null) {
            throw new NullPointerException("You must provide either a PlayerReference or URL reference.");
        }

        this.setReference(reference);
    }

    /**
     * channels is number of audio channels in the media object. It is an optional attribute.
     *
     * @return channels is number of audio channels in the media object. It is an optional attribute.
     */
    public Integer getAudioChannels() {
        return audioChannels;
    }

    /**
     * channels is number of audio channels in the media object. It is an optional attribute.
     *
     * @param audioChannels channels is number of audio channels in the media object. It is an optional attribute.
     */
    public void setAudioChannels(final Integer audioChannels) {
        this.audioChannels = audioChannels;
    }

    /**
     * bitrate is the kilobits per second rate of media. It is an optional attribute.
     *
     * @return bitrate is the kilobits per second rate of media. It is an optional attribute.
     */
    public Float getBitrate() {
        return bitrate;
    }

    /**
     * bitrate is the kilobits per second rate of media. It is an optional attribute.
     *
     * @param bitrate bitrate is the kilobits per second rate of media. It is an optional attribute.
     */
    public void setBitrate(final Float bitrate) {
        this.bitrate = bitrate;
    }

    /**
     * isDefault determines if this is the default object that should be used for the <media:group>. There should only be one default object per <media:group>. It is an optional attribute.
     *
     * @return isDefault determines if this is the default object that should be used for the <media:group>. There should only be one default object per <media:group>. It is an optional attribute.
     */
    public boolean isDefaultContent() {
        return defaultContent;
    }

    /**
     * isDefault determines if this is the default object that should be used for the <media:group>. There should only be one default object per <media:group>. It is an optional attribute.
     *
     * @param defaultContent isDefault determines if this is the default object that should be used for the <media:group>. There should only be one default object per <media:group>. It is an optional attribute.
     */
    public void setDefaultContent(final boolean defaultContent) {
        this.defaultContent = defaultContent;
    }

    /**
     * duration is the number of seconds the media object plays. It is an optional attribute.
     *
     * @return duration is the number of seconds the media object plays. It is an optional attribute.
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * duration is the number of seconds the media object plays. It is an optional attribute.
     *
     * @param duration duration is the number of seconds the media object plays. It is an optional attribute.
     */
    public void setDuration(final Long duration) {
        this.duration = duration;
    }

    /**
     * expression determines if the object is a sample or the full version of the object, or even if it is a continuous stream (sample | full | nonstop). Default value is 'full'. It is an optional attribute.
     *
     * @return expression determines if the object is a sample or the full version of the object, or even if it is a continuous stream (sample | full | nonstop). Default value is 'full'. It is an optional attribute.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * expression determines if the object is a sample or the full version of the object, or even if it is a continuous stream (sample | full | nonstop). Default value is 'full'. It is an optional attribute.
     *
     * @param expression expression determines if the object is a sample or the full version of the object, or even if it is a continuous stream (sample | full | nonstop). Default value is 'full'. It is an optional attribute.
     */
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }

    /**
     * fileSize is the number of bytes of the media object. It is an optional attribute.
     *
     * @return Returns the fileSize.
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * fileSize is the number of bytes of the media object. It is an optional attribute.
     *
     * @param fileSize The fileSize to set.
     */
    public void setFileSize(final Long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * frame rate is the number of frames per second for the media object. It is an optional attribute.
     *
     * @return frame rate is the number of frames per second for the media object. It is an optional attribute.
     */
    public Float getFramerate() {
        return framerate;
    }

    /**
     * frame rate is the number of frames per second for the media object. It is an optional attribute.
     *
     * @param framerate frame rate is the number of frames per second for the media object. It is an optional attribute.
     */
    public void setFramerate(final Float framerate) {
        this.framerate = framerate;
    }

    /**
     * height is the height of the media object. It is an optional attribute.
     *
     * @return height is the height of the media object. It is an optional attribute.
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * height is the height of the media object. It is an optional attribute.
     *
     * @param height height is the height of the media object. It is an optional attribute.
     */
    public void setHeight(final Integer height) {
        this.height = height;
    }

    /**
     * lang is the primary language encapsulated in the media object. Language codes possible are detailed in RFC 3066. This attribute is used similar to the xml:lang attribute detailed in the XML 1.0 Specification (Third Edition). It is an optional attribute.
     *
     * @return lang is the primary language encapsulated in the media object. Language codes possible are detailed in RFC 3066. This attribute is used similar to the xml:lang attribute detailed in the XML 1.0 Specification (Third Edition). It is an optional attribute.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * lang is the primary language encapsulated in the media object. Language codes possible are detailed in RFC 3066. This attribute is used similar to the xml:lang attribute detailed in the XML 1.0 Specification (Third Edition). It is an optional attribute.
     *
     * @param language lang is the primary language encapsulated in the media object. Language codes possible are detailed in RFC 3066. This attribute is used similar to the xml:lang attribute detailed in the XML 1.0 Specification (Third Edition). It is an optional attribute.
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * The metadata for the item.
     *
     * @return The metadata for the item
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * The metadata for the item
     *
     * @param metadata The metadata for the item
     */
    public void setMetadata(final Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * <strong>&lt;media:player&gt;</strong></p>
     * <p>Allows the media object to be accessed through a web browser media player console.
     * This element is required only if a direct media <em>url</em> attribute is not specified in the &lt;media:content&gt; element. It has 1 required attribute, and 2 optional attributes.</p>
     * <pre>        &lt;media:player url="http://www.foo.com/player?id=1111" height="200" width="400" /&gt;</pre>
     * <p><em>url</em> is the url of the player console that plays the media. It is a required attribute.</p>
     * <p/>
     * <p><em>height</em> is the height of the browser window that the <em>url</em> should be opened in. It is an optional attribute.</p>
     * <p><em>width</em> is the width of the browser window that the <em>url</em> should be opened in. It is an optional attribute.</p>
     *
     * @return PlayerReference for the item.
     */
    public PlayerReference getPlayer() {
        return player;
    }

    /**
     * <strong>&lt;media:player&gt;</strong></p>
     * <p>Allows the media object to be accessed through a web browser media player console.
     * This element is required only if a direct media <em>url</em> attribute is not specified in the &lt;media:content&gt; element. It has 1 required attribute, and 2 optional attributes.</p>
     * <pre>        &lt;media:player url="http://www.foo.com/player?id=1111" height="200" width="400" /&gt;</pre>
     * <p><em>url</em> is the url of the player console that plays the media. It is a required attribute.</p>
     * <p/>
     * <p><em>height</em> is the height of the browser window that the <em>url</em> should be opened in. It is an optional attribute.</p>
     * <p><em>width</em> is the width of the browser window that the <em>url</em> should be opened in. It is an optional attribute.</p>
     * <p/>
     * <p/>
     *
     * @param player PlayerReference for the item.
     */
    public void setPlayer(final PlayerReference player) {
        this.player = player;
    }

    /**
     * The player or URL reference for the item
     *
     * @return The player or URL reference for the item
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * The player or URL reference for the item
     *
     * @param reference The player or URL reference for the item
     */
    public void setReference(final Reference reference) {
        this.reference = reference;

        if (reference instanceof PlayerReference) {
            this.setPlayer((PlayerReference) reference);
        }
    }

    /**
     * samplingrate is the number of samples per second taken to create the media object. It is expressed in thousands of samples per second (kHz). It is an optional attribute.
     *
     * @return samplingrate is the number of samples per second taken to create the media object. It is expressed in thousands of samples per second (kHz). It is an optional attribute.
     */
    public Float getSamplingrate() {
        return samplingrate;
    }

    /**
     * samplingrate is the number of samples per second taken to create the media object. It is expressed in thousands of samples per second (kHz). It is an optional attribute.
     *
     * @param samplingrate samplingrate is the number of samples per second taken to create the media object. It is expressed in thousands of samples per second (kHz). It is an optional attribute.
     */
    public void setSamplingrate(final Float samplingrate) {
        this.samplingrate = samplingrate;
    }

    /**
     * type is the standard MIME type of the object. It is an optional attribute.
     *
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * type is the standard MIME type of the object. It is an optional attribute.
     *
     * @param type The type to set.
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * width is the width of the media object. It is an optional attribute.
     *
     * @return width is the width of the media object. It is an optional attribute.
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * width is the width of the media object. It is an optional attribute.
     *
     * @param width width is the width of the media object. It is an optional attribute.
     */
    public void setWidth(final Integer width) {
        this.width = width;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        MediaContent c = new MediaContent(getReference());
        c.setAudioChannels(getAudioChannels());
        c.setBitrate(getBitrate());
        c.setDefaultContent(isDefaultContent());
        c.setDuration(getDuration());
        c.setExpression(getExpression());
        c.setFileSize(getFileSize());
        c.setFramerate(getFramerate());
        c.setHeight(getHeight());
        c.setLanguage(getLanguage());
        c.setMetadata((getMetadata() == null) ? null : (Metadata) getMetadata().clone());
        c.setPlayer(getPlayer());
        c.setSamplingrate(getSamplingrate());
        c.setType(getType());
        c.setWidth(getWidth());

        return c;
    }

    @Override
    public boolean equals(final Object obj) {
        EqualsBean eBean = new EqualsBean(MediaContent.class, this);
        return eBean.beanEquals(obj);
    }

    @Override
    public int hashCode() {
        EqualsBean equals = new EqualsBean(MediaContent.class, this);
        return equals.beanHashCode();
    }

    @Override
    public String toString() {
        ToStringBean tsBean = new ToStringBean(MediaContent.class, this);
        return tsBean.toString();
    }
}

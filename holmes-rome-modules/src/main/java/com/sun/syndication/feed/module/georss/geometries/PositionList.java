/*
 * PositionList.java
 *
 * Created on 8. februar 2007, 11:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.syndication.feed.module.georss.geometries;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A list of geographic positions, latitude, longitude decimal degrees WGS84
 * @author runaas
 */
public class PositionList implements Cloneable, Serializable {
    private static final long serialVersionUID = 8859237113588387308L;

    private double[] latitude;
    private double[] longitude;
    private int size;

    /** Creates a new empty instance of PositionList */
    public PositionList() {
        size = 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        PositionList retval = (PositionList) super.clone();
        if (latitude != null) retval.latitude = latitude.clone();
        if (longitude != null) retval.longitude = longitude.clone();
        retval.size = size;
        return retval;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;

        PositionList p = (PositionList) obj;
        if (p.size != size) return false;
        for (int i = 0; i < size; ++i) {
            if (p.latitude[i] != latitude[i] || p.longitude[i] != longitude[i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(latitude);
        result = prime * result + Arrays.hashCode(longitude);
        result = prime * result + size;
        return result;
    }

    /**
     * Ensure capacity.
     *
     * @param newSize the new size
     */
    private void ensureCapacity(final int newSize) {
        int arraySize = newSize;
        if (longitude != null && longitude.length >= arraySize) return;
        if (arraySize < 4) arraySize = 4;
        else arraySize = (int) Math.ceil(Math.pow(2, Math.ceil(Math.log(arraySize) / Math.log(2))));
        double[] tmp = new double[arraySize];
        if (longitude != null) System.arraycopy(longitude, 0, tmp, 0, size);
        longitude = tmp;
        tmp = new double[arraySize];
        if (latitude != null) System.arraycopy(latitude, 0, tmp, 0, size);
        latitude = tmp;
    }

    /**
     * @return the number of positions in the list
     */
    public int size() {
        return size;
    }

    /**
     * @param pos position index
     * @return longitude for position
     */
    public double getLongitude(final int pos) {
        return longitude[pos];
    }

    /**
     * @param pos position index
     * @return latitude for position
     */
    public double getLatitude(final int pos) {
        return latitude[pos];
    }

    /**
     * Add a position at the end of the list.
     *
     * @param newLatitude the latitude
     * @param newLongitude the longitude
     */
    public void add(final double newLatitude, final double newLongitude) {
        ensureCapacity(size + 1);
        this.longitude[size] = newLongitude;
        this.latitude[size] = newLatitude;
        ++size;
    }

    /**
     * Add a position at a given index in the list. The rest of the list is
     * shifted one place to the "right"
     *
     * @param pos position index
     * @param newLatitude the latitude
     * @param newLongitude the longitude
     */
    public void insert(final int pos, final double newLatitude, final double newLongitude) {
        ensureCapacity(size + 1);
        System.arraycopy(this.longitude, pos, this.longitude, pos + 1, size - pos);
        System.arraycopy(this.latitude, pos, this.latitude, pos + 1, size - pos);
        this.longitude[pos] = newLongitude;
        this.latitude[pos] = newLatitude;
        ++size;
    }

    /**
     * Replace the position at the index with new values.
     *
     * @param pos position index
     * @param newLatitude the latitude
     * @param newLongitude the longitude
     */
    public void replace(final int pos, final double newLatitude, final double newLongitude) {
        this.longitude[pos] = newLongitude;
        this.latitude[pos] = newLatitude;
    }

    /**
     * Remove the position at the index, the rest of the list is shifted one place to the "left"
     * 
     * @param pos position index
     */
    public void remove(final int pos) {
        System.arraycopy(longitude, pos + 1, longitude, pos, size - pos - 1);
        System.arraycopy(latitude, pos + 1, latitude, pos, size - pos - 1);
        --size;
    }
}

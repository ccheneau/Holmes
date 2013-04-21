/*
 * Position.java
 *
 * Created on 8. februar 2007, 11:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.syndication.feed.module.georss.geometries;

import java.io.Serializable;

/**
 * A two dimensional position represented by latitude and longitude decimal degrees in WGS84
 * @author runaas
 */
public class Position implements Cloneable, Serializable {
    private static final long serialVersionUID = -6494530683094579620L;

    private double latitude;
    private double longitude;

    /** Creates a new instance of Position */
    public Position() {
        latitude = Double.NaN;
        longitude = Double.NaN;
    }

    /**
     * Create Position from a pair of coordinate values.
     *
     * @param latitude the latitude
     * @param longitude the longitude
     */
    public Position(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;

        Position p = (Position) obj;
        return p.latitude == latitude && p.longitude == longitude;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set the latitude
     *
     * @param latitude the new latitude
     */
    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set the longitude
     *
     * @param longitude the new longitude
     */
    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }
}

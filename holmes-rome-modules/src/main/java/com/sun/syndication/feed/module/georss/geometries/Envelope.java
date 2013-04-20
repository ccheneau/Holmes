/*
 * Envelope.java
 *
 * Created on 12. februar 2007, 13:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.syndication.feed.module.georss.geometries;

/**
 * Envelope, a bounding box spanned by an upper right and lower left corner point.
 * Note that if the box spans the -180 180 degree meridian the numerical value of the
 * minLongitude may be greater than the maxLongitude.
 *
 * @author runaas
 */
public class Envelope extends AbstractGeometry {
    private static final long serialVersionUID = 1555722515672546906L;

    protected double minLatitude, minLongitude, maxLatitude, maxLongitude;

    /** Creates a new instance of Envelope */
    public Envelope() {
        minLatitude = minLongitude = maxLatitude = maxLongitude = Double.NaN;
    }

    /**
     * Construct object from coordinate values
     *
     * @param minLatitude
     * @param minLongitude
     * @param maxLatitude
     * @param maxLongitude
     */
    public Envelope(double minLatitude, double minLongitude, double maxLatitude, double maxLongitude) {
        this.minLatitude = minLatitude;
        this.minLongitude = minLongitude;
        this.maxLatitude = maxLatitude;
        this.maxLongitude = maxLongitude;
    }

    /**
     * @return the minimum longitude
     */
    public double getMinLongitude() {
        return minLongitude;
    }

    /**
     * @return the minimum latitude
     */
    public double getMinLatitude() {
        return minLatitude;
    }

    /**
     * @return the maximum longitude
     */
    public double getMaxLongitude() {
        return maxLongitude;
    }

    /**
     * @return the maximum latitude
     */
    public double getMaxLatitude() {
        return maxLatitude;
    }

    /**
     * @param v  minimum longitude
     */
    public void setMinLongitude(double v) {
        minLongitude = v;
    }

    /**
     * @param v minimum latitude
     */
    public void setMinLatitude(double v) {
        minLatitude = v;
    }

    /**
     * @param v maximum longitude
     */
    public void setMaxLongitude(double v) {
        maxLongitude = v;
    }

    /**
     * @param v maximum latitude
     */
    public void setMaxLatitude(double v) {
        maxLatitude = v;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(maxLatitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxLongitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minLatitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(minLongitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Envelope other = (Envelope) obj;
        if (Double.doubleToLongBits(maxLatitude) != Double.doubleToLongBits(other.maxLatitude)) return false;
        if (Double.doubleToLongBits(maxLongitude) != Double.doubleToLongBits(other.maxLongitude)) return false;
        if (Double.doubleToLongBits(minLatitude) != Double.doubleToLongBits(other.minLatitude)) return false;
        if (Double.doubleToLongBits(minLongitude) != Double.doubleToLongBits(other.minLongitude)) return false;
        return true;
    }

}

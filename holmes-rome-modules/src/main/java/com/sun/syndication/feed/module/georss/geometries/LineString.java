/*
 * LineString.java
 *
 * Created on 8. februar 2007, 10:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.syndication.feed.module.georss.geometries;

/**
 * Linear object constructed by linear interpolation between points
 * @author runaas
 */
public final class LineString extends AbstractCurve {
    private static final long serialVersionUID = 379833585445502546L;

    private PositionList posList;

    /** Creates a new instance of LineString */
    public LineString() {

    }

    /**
     * Construct object from a position list.
     *
     * @param posList the pos list
     */
    public LineString(final PositionList posList) {
        this.posList = posList;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        LineString retval = (LineString) super.clone();
        if (posList != null) retval.posList = (PositionList) posList.clone();
        return retval;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        return getPositionList().equals(((LineString) obj).getPositionList());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((posList == null) ? 0 : posList.hashCode());
        return result;
    }

    /**
     * Get the position list
     *
     * @return the positionlist
     */
    public PositionList getPositionList() {
        if (posList == null) posList = new PositionList();
        return posList;
    }

    /**
     * Set the position list.
     *
     * @param newPosList the new position list
     */
    public void setPositionList(final PositionList newPosList) {
        this.posList = newPosList;
    }
}

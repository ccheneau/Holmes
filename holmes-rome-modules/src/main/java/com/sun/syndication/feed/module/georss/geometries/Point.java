/*
 * Point.java
 *
 * Created on 8. februar 2007, 10:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.syndication.feed.module.georss.geometries;

/**
 * Point object, contains a position
 * @author runaas
 */
public final class Point extends AbstractGeometricPrimitive {
    private static final long serialVersionUID = -4313019631466637375L;

    private Position pos;

    /**
     * Creates a new instance of Point.
     */
    public Point() {

    }

    /**
     * Constructor.
     *
     * @param newPos the new pos
     */
    public Point(final Position newPos) {
        this.pos = newPos;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Point retval = (Point) super.clone();
        if (pos != null) retval.pos = (Position) pos.clone();
        return retval;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        return getPosition().equals(((Point) obj).getPosition());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pos == null) ? 0 : pos.hashCode());
        return result;
    }

    /**
      * Get the position
      *
      * @return the position
      */
    public Position getPosition() {
        if (pos == null) pos = new Position();
        return pos;
    }

    /**
     * Set the position.
     *
     * @param newPos the new position
     */
    public void setPosition(final Position newPos) {
        this.pos = newPos;
    }
}

/*
 * Polygon.java
 *
 * Created on 8. februar 2007, 10:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.syndication.feed.module.georss.geometries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Polygon, a surface object bounded by one external ring and zero or more internal rings 
 * @author runaas
 */
public final class Polygon extends AbstractSurface {
    private static final long serialVersionUID = -691747021168579678L;

    private AbstractRing exterior;
    private List<AbstractRing> interior;

    /** Creates a new instance of Polygon */
    public Polygon() {
        interior = new ArrayList<AbstractRing>();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Polygon retval = (Polygon) super.clone();
        if (exterior != null) retval.exterior = (AbstractRing) exterior.clone();
        if (interior != null) {
            retval.interior = new ArrayList<AbstractRing>();
            Iterator<AbstractRing> it = interior.iterator();
            while (it.hasNext()) {
                AbstractRing r = it.next();
                retval.interior.add((AbstractRing) r.clone());
            }
        }
        return retval;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        Polygon pol = (Polygon) obj;

        if (exterior == null || pol.exterior == null) return false;
        else if (!exterior.equals(pol.exterior)) return false;

        // Not efficient.... (but the number of internal ringr is usually small).
        Iterator<AbstractRing> it = interior.iterator();
        while (it.hasNext()) {
            if (!pol.interior.contains(it.next())) return false;
        }
        it = pol.interior.iterator();
        while (it.hasNext()) {
            if (!interior.contains(it.next())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((exterior == null) ? 0 : exterior.hashCode());
        result = prime * result + ((interior == null) ? 0 : interior.hashCode());
        return result;
    }

    /**
      * Retrieve the outer border
      *
      * @return the border ring
      */
    public AbstractRing getExterior() {
        return exterior;
    }

    /**
     * Retrieve the inner border
     *
     * @return the list of border rings
     */
    public List<AbstractRing> getInterior() {
        if (interior == null) interior = new ArrayList<AbstractRing>();
        return interior;
    }

    /**
     * Set the outer border
     *
     * @param exterior the outer ring
     */
    public void setExterior(final AbstractRing exterior) {
        this.exterior = exterior;
    }

    /**
     * Set the list of inner borders (holes)
     *
     * @param interior the list of inner rings
     */
    public void setInterior(final List<AbstractRing> interior) {
        this.interior = interior;
    }
}

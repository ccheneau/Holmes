/*
 * AbstractRing.java
 *
 * Created on 8. februar 2007, 11:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.sun.syndication.feed.module.georss.geometries;

import java.io.Serializable;

/**
 * Abstract base class for rings (closed linear objects used for polygon borders)
 * @author runaas
 */
public abstract class AbstractRing implements Cloneable, Serializable {
    private static final long serialVersionUID = 2889624751037045817L;

    /** Creates a new instance of AbstractRing */
    public AbstractRing() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

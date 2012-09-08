/*
 * Copyright 2006 Marc Wick, geonames.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.feed.module.georss;

import org.jdom.Namespace;

import com.sun.syndication.feed.module.ModuleImpl;
import com.sun.syndication.feed.module.georss.geometries.AbstractGeometry;
import com.sun.syndication.feed.module.georss.geometries.Point;
import com.sun.syndication.feed.module.georss.geometries.Position;

/**
 * GeoRSSModule is the main georss interface defining the methods to produce and
 * consume georss elements.
 *
 * @author Marc Wick
 * @version $Id: GeoRSSModule.java,v 1.8 2007/06/06 09:47:32 marcwick Exp $
 */
public abstract class GeoRSSModule extends ModuleImpl implements Cloneable {
    private static final long serialVersionUID = 1712562163154541544L;

    protected AbstractGeometry geometry;

    public static final String version = "0.9.8";

    /**
     * namespace URI for georss simple: <i>"http://www.georss.org/georss"</i>
     */
    public static final String GEORSS_GEORSS_URI = "http://www.georss.org/georss";

    /**
     * namespace URI for w3c georss :
     * <i>"http://www.w3.org/2003/01/geo/wgs84_pos#"</i>
     */
    public static final String GEORSS_W3CGEO_URI = "http://www.w3.org/2003/01/geo/wgs84_pos#";

    /**
     * namespace URI for GML georss : <i>"http://www.opengis.net/gml"</i>
     */
    public static final String GEORSS_GML_URI = "http://www.opengis.net/gml";

    /**
     * Namespace for georss simple :
     * <i>xmlns:georss="http://www.georss.org/georss"</i>
     */
    public static final Namespace SIMPLE_NS = Namespace.getNamespace("georss", GeoRSSModule.GEORSS_GEORSS_URI);

    /**
     *
     * Namespace for w3c georss :
     * <i>xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"</i>
     */
    public static final Namespace W3CGEO_NS = Namespace.getNamespace("geo", GeoRSSModule.GEORSS_W3CGEO_URI);

    /**
     *
     * Namespace for gml georss : <i>xmlns:gml="http://www.opengis.net/gml"</i>
     */
    public static final Namespace GML_NS = Namespace.getNamespace("gml", GeoRSSModule.GEORSS_GML_URI);

    protected GeoRSSModule(java.lang.Class<?> beanClass, java.lang.String uri) {
        super(beanClass, uri);
    }

    /**
     * Set geometry of georss element
     *
     * @param geometry
     *            geometry
     */
    public void setGeometry(AbstractGeometry geometry) {
        this.geometry = geometry;
    }

    /**
     * returns the geometry
     *
     * @return geometry
     */
    public AbstractGeometry getGeometry() {
        return geometry;
    }

    /**
     * Convenience method to return point geometry.
     * Returns null if the geometry is non-point.
     *
     * @return geometry
     */
    public Position getPosition() {
        if (geometry instanceof Point) return ((Point) geometry).getPosition();
        return null;
    }

    /**
     * Convenience method to set point geometry.
     *
     * @return geometry
     */
    public void setPosition(Position pos) {
        if (pos != null) geometry = new Point(pos);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sun.syndication.feed.CopyFrom#copyFrom(java.lang.Object)
     */
    @Override
    public void copyFrom(Object obj) {
        GeoRSSModule geoRSSModule = (GeoRSSModule) obj;
        geometry = geoRSSModule.getGeometry();
        try {
            geometry = (AbstractGeometry) geometry.clone();
        }
        catch (CloneNotSupportedException ex) {
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            GeoRSSModule retval = (GeoRSSModule) super.clone();
            if (geometry != null) retval.geometry = (AbstractGeometry) geometry.clone();
            return retval;
        }
        catch (Exception ex) {
            throw new CloneNotSupportedException();
        }
    }
}

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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.georss.geometries.AbstractGeometry;
import com.sun.syndication.feed.module.georss.geometries.AbstractRing;
import com.sun.syndication.feed.module.georss.geometries.Envelope;
import com.sun.syndication.feed.module.georss.geometries.LineString;
import com.sun.syndication.feed.module.georss.geometries.LinearRing;
import com.sun.syndication.feed.module.georss.geometries.Point;
import com.sun.syndication.feed.module.georss.geometries.Polygon;
import com.sun.syndication.feed.module.georss.geometries.Position;
import com.sun.syndication.feed.module.georss.geometries.PositionList;
import com.sun.syndication.io.ModuleGenerator;

/**
 * SimpleGenerator produces georss elements in georss simple format.
 * 
 * @author Marc Wick
 * @version $Id: SimpleGenerator.java,v 1.4 2007/04/18 09:59:29 marcwick Exp $
 * 
 */
public class SimpleGenerator implements ModuleGenerator {
    private static final Logger logger = Logger.getLogger(SimpleGenerator.class.getName());

    private static final Set<Namespace> NAMESPACES;
    static {
        Set<Namespace> nss = new HashSet<Namespace>();
        nss.add(GeoRSSModule.SIMPLE_NS);
        NAMESPACES = Collections.unmodifiableSet(nss);
    }

    private String posListToString(PositionList posList) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < posList.size(); ++i)
            sb.append(posList.getLatitude(i)).append(" ").append(posList.getLongitude(i)).append(" ");
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.syndication.io.ModuleGenerator#getNamespaceUri()
     */
    @Override
    public String getNamespaceUri() {
        return GeoRSSModule.GEORSS_GEORSS_URI;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.syndication.io.ModuleGenerator#getNamespaces()
     */
    @Override
    public Set<Namespace> getNamespaces() {
        return NAMESPACES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.syndication.io.ModuleGenerator#generate(com.sun.syndication.feed.module.Module,
     *      org.jdom.Element)
     */
    @Override
    public void generate(Module module, Element element) {
        // this is not necessary, it is done to avoid the namespace definition
        // in every item.
        Element root = element;
        while (root.getParent() != null && root.getParent() instanceof Element) {
            root = (Element) element.getParent();
        }
        root.addNamespaceDeclaration(GeoRSSModule.SIMPLE_NS);

        GeoRSSModule geoRSSModule = (GeoRSSModule) module;

        AbstractGeometry geometry = geoRSSModule.getGeometry();
        if (geometry instanceof Point) {
            Position pos = ((Point) geometry).getPosition();

            Element pointElement = new Element("point", GeoRSSModule.SIMPLE_NS);
            pointElement.addContent(pos.getLatitude() + " " + pos.getLongitude());
            element.addContent(pointElement);
        } else if (geometry instanceof LineString) {
            PositionList posList = ((LineString) geometry).getPositionList();

            Element lineElement = new Element("line", GeoRSSModule.SIMPLE_NS);

            lineElement.addContent(posListToString(posList));
            element.addContent(lineElement);
        } else if (geometry instanceof Polygon) {
            AbstractRing ring = ((Polygon) geometry).getExterior();
            if (ring instanceof LinearRing) {
                PositionList posList = ((LinearRing) ring).getPositionList();
                Element polygonElement = new Element("polygon", GeoRSSModule.SIMPLE_NS);

                polygonElement.addContent(posListToString(posList));
                element.addContent(polygonElement);
            } else {
                logger.log(Level.WARNING, "GeoRSS simple format can't handle rings of type: " + ring.getClass().getName());
            }
        } else if (geometry instanceof Envelope) {
            Envelope envelope = (Envelope) geometry;
            Element boxElement = new Element("box", GeoRSSModule.SIMPLE_NS);
            boxElement.addContent(envelope.getMinLatitude() + " " + envelope.getMinLongitude() + " " + envelope.getMaxLatitude() + " "
                    + envelope.getMaxLongitude());
            element.addContent(boxElement);
        } else {
            logger.log(Level.WARNING, "GeoRSS simple format can't handle geometries of type: " + geometry.getClass().getName());
        }
    }

}

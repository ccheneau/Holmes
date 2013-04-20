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
import java.util.Iterator;
import java.util.List;
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
 * GMLGenerator produces georss elements in georss GML format.
 *
 * @author Marc Wick
 * @version $Id: GMLGenerator.java,v 1.1 2007/04/18 09:59:29 marcwick Exp $
 *
 */
public class GMLGenerator implements ModuleGenerator {
    private static final Logger logger = Logger.getLogger(GMLGenerator.class.getName());

    private static final Set<Namespace> NAMESPACES;

    static {
        Set<Namespace> nss = new HashSet<Namespace>();
        nss.add(GeoRSSModule.GML_NS);
        NAMESPACES = Collections.unmodifiableSet(nss);
    }

    private Element createPosListElement(PositionList posList) {
        Element posElement = new Element("posList", GeoRSSModule.GML_NS);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < posList.size(); ++i)
            sb.append(posList.getLatitude(i)).append(" ").append(posList.getLongitude(i)).append(" ");

        posElement.addContent(sb.toString());
        return posElement;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sun.syndication.io.ModuleGenerator#getNamespaceUri()
     */
    @Override
    public String getNamespaceUri() {
        return GeoRSSModule.GEORSS_GML_URI;
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
        root.addNamespaceDeclaration(GeoRSSModule.GML_NS);

        Element whereElement = new Element("where", GeoRSSModule.SIMPLE_NS);
        element.addContent(whereElement);

        if (module instanceof GeoRSSModule) {
            GeoRSSModule geoRSSModule = (GeoRSSModule) module;
            AbstractGeometry geometry = geoRSSModule.getGeometry();

            if (geometry instanceof Point) {
                Position pos = ((Point) geometry).getPosition();

                Element pointElement = new Element("Point", GeoRSSModule.GML_NS);
                whereElement.addContent(pointElement);

                Element posElement = new Element("pos", GeoRSSModule.GML_NS);
                posElement.addContent(String.valueOf(pos.getLatitude()) + " " + String.valueOf(pos.getLongitude()));
                pointElement.addContent(posElement);
            }

            else if (geometry instanceof LineString) {
                PositionList posList = ((LineString) geometry).getPositionList();

                Element lineElement = new Element("LineString", GeoRSSModule.GML_NS);
                lineElement.addContent(createPosListElement(posList));
                whereElement.addContent(lineElement);
            } else if (geometry instanceof Polygon) {
                Element polygonElement = new Element("Polygon", GeoRSSModule.GML_NS);
                {
                    AbstractRing ring = ((Polygon) geometry).getExterior();
                    if (ring instanceof LinearRing) {
                        Element exteriorElement = new Element("exterior", GeoRSSModule.GML_NS);
                        polygonElement.addContent(exteriorElement);
                        Element ringElement = new Element("LinearRing", GeoRSSModule.GML_NS);
                        exteriorElement.addContent(ringElement);
                        ringElement.addContent(createPosListElement(((LinearRing) ring).getPositionList()));

                    } else {
                        logger.log(Level.WARNING, "GeoRSS GML format can't handle rings of type: " + ring.getClass().getName());
                    }
                }
                List<AbstractRing> interiorList = ((Polygon) geometry).getInterior();
                Iterator<AbstractRing> it = interiorList.iterator();
                while (it.hasNext()) {
                    AbstractRing ring = it.next();
                    if (ring instanceof LinearRing) {
                        Element interiorElement = new Element("interior", GeoRSSModule.GML_NS);
                        polygonElement.addContent(interiorElement);
                        Element ringElement = new Element("LinearRing", GeoRSSModule.GML_NS);
                        interiorElement.addContent(ringElement);
                        ringElement.addContent(createPosListElement(((LinearRing) ring).getPositionList()));

                    } else {
                        logger.log(Level.WARNING, "GeoRSS GML format can't handle rings of type: " + ring.getClass().getName());
                    }
                }
                whereElement.addContent(polygonElement);
            } else if (geometry instanceof Envelope) {
                Envelope envelope = (Envelope) geometry;
                Element envelopeElement = new Element("Envelope", GeoRSSModule.GML_NS);
                whereElement.addContent(envelopeElement);

                Element lowerElement = new Element("lowerCorner", GeoRSSModule.GML_NS);
                lowerElement.addContent(String.valueOf(envelope.getMinLatitude()) + " " + String.valueOf(envelope.getMinLongitude()));
                envelopeElement.addContent(lowerElement);

                Element upperElement = new Element("upperCorner", GeoRSSModule.GML_NS);
                upperElement.addContent(String.valueOf(envelope.getMaxLatitude()) + " " + String.valueOf(envelope.getMaxLongitude()));
                envelopeElement.addContent(upperElement);

            } else {
                logger.log(Level.WARNING, "GeoRSS GML format can't handle geometries of type: " + geometry.getClass().getName());
            }
        }
    }
}

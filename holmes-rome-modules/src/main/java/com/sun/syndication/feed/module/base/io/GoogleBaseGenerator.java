/*
 * GoogleBaseGenerator.java
 *
 * Created on November 17, 2005, 2:46 PM
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License or the Apache
 * License at your discretion.
 *
 *  Copyright (C) 2005  Robert Cooper, Temple of the Screaming Penguin
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.syndication.feed.module.base.io;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.base.GoogleBase;
import com.sun.syndication.feed.module.base.GoogleBaseImpl;
import com.sun.syndication.feed.module.base.types.CurrencyEnumeration;
import com.sun.syndication.feed.module.base.types.DateTimeRange;
import com.sun.syndication.feed.module.base.types.FloatUnit;
import com.sun.syndication.feed.module.base.types.GenderEnumeration;
import com.sun.syndication.feed.module.base.types.IntUnit;
import com.sun.syndication.feed.module.base.types.PaymentTypeEnumeration;
import com.sun.syndication.feed.module.base.types.PriceTypeEnumeration;
import com.sun.syndication.feed.module.base.types.ShippingType;
import com.sun.syndication.feed.module.base.types.ShortDate;
import com.sun.syndication.feed.module.base.types.Size;
import com.sun.syndication.feed.module.base.types.YearType;
import com.sun.syndication.io.ModuleGenerator;

/**
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper
 * @version $Revision: 1.1 $
 */
public class GoogleBaseGenerator implements ModuleGenerator {
    private static final Logger logger = Logger.getLogger(GoogleBaseGenerator.class.getName());

    private static final Namespace NS = Namespace.getNamespace("g-core", GoogleBase.URI);

    /** Creates a new instance of GoogleBaseGenerator */
    public GoogleBaseGenerator() {
        super();
    }

    @Override
    public String getNamespaceUri() {
        return GoogleBase.URI;
    }

    @Override
    public Set<Namespace> getNamespaces() {
        HashSet<Namespace> set = new HashSet<Namespace>();
        set.add(GoogleBaseGenerator.NS);

        return set;
    }

    @Override
    public void generate(Module module, Element element) {
        if (module instanceof GoogleBaseImpl) {
            GoogleBaseImpl mod = (GoogleBaseImpl) module;
            PropertyDescriptor[] pds = GoogleBaseParser.pds;

            for (int i = 0; i < pds.length; i++) {
                String tagName = GoogleBaseParser.PROPS2TAGS.getProperty(pds[i].getName());

                if (tagName == null) {
                    continue;
                }

                Object[] values = null;

                try {
                    if (pds[i].getPropertyType().isArray()) {
                        values = (Object[]) pds[i].getReadMethod().invoke(mod, (Object[]) null);
                    } else {
                        values = new Object[] { pds[i].getReadMethod().invoke(mod, (Object[]) null) };
                    }

                    for (int j = 0; values != null && j < values.length; j++) {
                        if (values[j] != null) {
                            element.addContent(this.generateTag(values[j], tagName));
                        }
                    }
                } catch (InvocationTargetException e) {
                    logger.log(Level.WARNING, e.getMessage());
                } catch (IllegalAccessException e) {
                    logger.log(Level.WARNING, e.getMessage());
                } catch (IllegalArgumentException e) {
                    logger.log(Level.WARNING, e.getMessage());
                }
            }
        }
    }

    public Element generateTag(Object o, String tagName) {
        if (o instanceof URL || o instanceof Float || o instanceof Boolean || o instanceof Integer || o instanceof String || o instanceof FloatUnit
                || o instanceof IntUnit || o instanceof GenderEnumeration || o instanceof PaymentTypeEnumeration || o instanceof PriceTypeEnumeration
                || o instanceof CurrencyEnumeration || o instanceof Size || o instanceof YearType) {
            return this.generateSimpleElement(tagName, o.toString());
        } else if (o instanceof ShortDate) {
            return this.generateSimpleElement(tagName, new SimpleDateFormat(GoogleBaseParser.SHORT_DT_FMT).format(o));
        } else if (o instanceof Date) {
            return this.generateSimpleElement(tagName, new SimpleDateFormat(GoogleBaseParser.LONG_DT_FMT).format(o));
        } else if (o instanceof ShippingType) {
            ShippingType st = (ShippingType) o;
            Element element = new Element(tagName, GoogleBaseGenerator.NS);

            element.addContent(this.generateSimpleElement("country", st.getCountry()));

            element.addContent(this.generateSimpleElement("service", st.getService().toString()));

            element.addContent(this.generateSimpleElement("price", st.getPrice().toString()));

            return element;
        } else if (o instanceof DateTimeRange) {
            DateTimeRange dtr = (DateTimeRange) o;
            Element element = new Element(tagName, GoogleBaseGenerator.NS);
            element.addContent(this.generateSimpleElement("start", new SimpleDateFormat(GoogleBaseParser.LONG_DT_FMT).format(dtr.getStart())));
            element.addContent(this.generateSimpleElement("end", new SimpleDateFormat(GoogleBaseParser.LONG_DT_FMT).format(dtr.getEnd())));

            return element;
        }

        throw new RuntimeException("Unknown class type to handle: " + o.getClass().getName());
    }

    protected Element generateSimpleElement(String name, String value) {
        Element element = new Element(name, GoogleBaseGenerator.NS);
        element.addContent(value);

        return element;
    }
}

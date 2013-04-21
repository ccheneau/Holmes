/*
 * LicenseEnumeration.java
 *
 * Created on November 20, 2005, 3:20 PM
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
package com.sun.syndication.feed.module.cc.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ToStringBean;

/**
 * The Class License.
 *
 * @version $Revision: 1.3 $
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class License implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1890706896523356764L;

    /** The Constant CC_START. */
    private static final String CC_START = "http://creativecommons.org/licenses/";

    /** The Constant LOOKUP_LICENSE. */
    private static final HashMap<String, License> LOOKUP_LICENSE = new HashMap<String, License>();

    /** The Constant NO_DERIVS. */
    public static final License NO_DERIVS = new License("http://creativecommons.org/licenses/nd/1.0/", new Behaviour[0], new Behaviour[] {
            Behaviour.DISTRIBUTION, Behaviour.REPRODUCTION });

    /** The Constant NO_DERIVS_NONCOMMERCIAL. */
    public static final License NO_DERIVS_NONCOMMERCIAL = new License("http://creativecommons.org/licenses/nd-nc/1.0/",
            new Behaviour[] { Behaviour.NONCOMMERCIAL }, new Behaviour[] { Behaviour.DISTRIBUTION, Behaviour.REPRODUCTION });

    /** The Constant NONCOMMERCIAL. */
    public static final License NONCOMMERCIAL = new License("http://creativecommons.org/licenses/nc/1.0/", new Behaviour[] { Behaviour.NONCOMMERCIAL },
            new Behaviour[] { Behaviour.DERIVATIVE, Behaviour.DISTRIBUTION, Behaviour.REPRODUCTION });

    /** The Constant SHARE_ALIKE. */
    public static final License SHARE_ALIKE = new License("http://creativecommons.org/licenses/sa/1.0/", new Behaviour[] { Behaviour.COPYLEFT },
            new Behaviour[] { Behaviour.DERIVATIVE, Behaviour.DISTRIBUTION, Behaviour.REPRODUCTION });

    /** The Constant SHARE_ALIKE_NONCOMMERCIAL. */
    public static final License SHARE_ALIKE_NONCOMMERCIAL = new License("http://creativecommons.org/licenses/nc-sa/1.0/", new Behaviour[] { Behaviour.COPYLEFT,
            Behaviour.NONCOMMERCIAL }, new Behaviour[] { Behaviour.DERIVATIVE, Behaviour.DISTRIBUTION, Behaviour.REPRODUCTION });

    /** The Constant SHARE_ALIKE_ATTRIBUTION. */
    public static final License SHARE_ALIKE_ATTRIBUTION = new License("http://creativecommons.org/licenses/by-sa/2.5/", new Behaviour[] { Behaviour.COPYLEFT,
            Behaviour.ATTRIBUTION }, new Behaviour[] { Behaviour.DERIVATIVE, Behaviour.DISTRIBUTION, Behaviour.REPRODUCTION });

    /** The Constant SHARE_ALIKE_NONCOMMERCIAL_ATTRIBUTION. */
    public static final License SHARE_ALIKE_NONCOMMERCIAL_ATTRIBUTION = new License("http://creativecommons.org/licenses/by-nc-sa/2.5/", new Behaviour[] {
            Behaviour.COPYLEFT, Behaviour.ATTRIBUTION, Behaviour.NONCOMMERCIAL }, new Behaviour[] { Behaviour.DERIVATIVE, Behaviour.DISTRIBUTION,
            Behaviour.REPRODUCTION });

    /** The Constant NONCOMMERCIAL_ATTRIBUTION. */
    public static final License NONCOMMERCIAL_ATTRIBUTION = new License("http://creativecommons.org/licenses/by-nc/2.5/", new Behaviour[] {
            Behaviour.ATTRIBUTION, Behaviour.NONCOMMERCIAL }, new Behaviour[] { Behaviour.DERIVATIVE, Behaviour.DISTRIBUTION, Behaviour.REPRODUCTION });

    /** The Constant NONCOMMERCIAL_ATTRIBUTION_NO_DERIVS. */
    public static final License NONCOMMERCIAL_ATTRIBUTION_NO_DERIVS = new License("http://creativecommons.org/licenses/by-nc-nd/2.5/", new Behaviour[] {
            Behaviour.ATTRIBUTION, Behaviour.NONCOMMERCIAL }, new Behaviour[] { Behaviour.DISTRIBUTION, Behaviour.REPRODUCTION });

    /** The Constant ATTRIBUTION_NO_DERIVS. */
    public static final License ATTRIBUTION_NO_DERIVS = new License("http://creativecommons.org/licenses/by-nd/2.5/",
            new Behaviour[] { Behaviour.ATTRIBUTION }, new Behaviour[] { Behaviour.DISTRIBUTION, Behaviour.REPRODUCTION });

    /** The Constant ATTRIBUTION. */
    public static final License ATTRIBUTION = new License("http://creativecommons.org/licenses/by/2.5/", new Behaviour[] { Behaviour.ATTRIBUTION },
            new Behaviour[] { Behaviour.DERIVATIVE, Behaviour.DISTRIBUTION, Behaviour.REPRODUCTION });

    /** The uri. */
    private String uri;

    /** The permits. */
    private Behaviour[] permits;

    /** The requires. */
    private Behaviour[] requires;

    /**
     * Creates a new instance of License.
     *
     * @param uri the uri
     * @param requires the requires
     * @param permits the permits
     */
    public License(final String uri, final Behaviour[] requires, final Behaviour[] permits) {
        this.requires = requires;
        this.permits = permits;
        this.uri = uri;
        License.LOOKUP_LICENSE.put(uri, this);

        if (this.uri.endsWith("/")) {
            //System.out.println(uri.substring(0,this.uri.lastIndexOf("/")));
            License.LOOKUP_LICENSE.put(uri.substring(0, this.uri.lastIndexOf("/")), this);
        }
    }

    /**
     * Find by value.
     *
     * @param uri the uri
     * @return license
     */
    public static License findByValue(final String uri) {
        License found = License.LOOKUP_LICENSE.get(uri);

        //No I am going to try an guess about unknown licenses
        // This is try and match known CC licenses of other versions or various URLs to
        // current licenses, then make a new one with the same permissions.
        if (found == null && uri.startsWith("http://") && uri.toLowerCase().indexOf("creativecommons.org") != -1) {
            Iterator<String> it = License.LOOKUP_LICENSE.keySet().iterator();
            while (it.hasNext() && found == null) {
                String key = it.next();
                if (key.startsWith(CC_START)) {
                    String licensePath = key.substring(CC_START.length(), key.length());
                    StringTokenizer tok = new StringTokenizer(licensePath, "/");
                    String license = tok.nextToken();
                    /*String version = */tok.nextToken();
                    if (uri.toLowerCase().indexOf("creativecommons.org/licenses/" + license) != -1) {
                        License current = LOOKUP_LICENSE.get(key);
                        found = new License(uri, current.getRequires(), current.getPermits());
                    }
                }
            }
        }
        //OK, we got here. If we haven't found a match, return a new License with unknown permissions.
        if (found == null) {
            found = new License(uri, null, null);
        }
        return found;
    }

    public Behaviour[] getPermits() {
        return this.permits;
    }

    public Behaviour[] getRequires() {
        return this.requires;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        ToStringBean tsb = new ToStringBean(License.class, this);
        return tsb.toString();
    }

    public String getValue() {
        return this.uri;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        EqualsBean eBean = new EqualsBean(License.class, this);
        return eBean.beanEquals(obj);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        EqualsBean equals = new EqualsBean(License.class, this);
        return equals.beanHashCode();
    }

    /**
     * The Class Behaviour.
     */
    public static final class Behaviour implements Serializable {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 4484932480954592749L;

        /** The Constant LOOKUP. */
        private static final HashMap<String, Behaviour> LOOKUP = new HashMap<String, Behaviour>();

        /** The Constant REPRODUCTION. */
        public static final Behaviour REPRODUCTION = new Behaviour("http://web.resource.org/cc/Reproduction");

        /** The Constant DISTRIBUTION. */
        public static final Behaviour DISTRIBUTION = new Behaviour("http://web.resource.org/cc/Distribution");

        /** The Constant DERIVATIVE. */
        public static final Behaviour DERIVATIVE = new Behaviour("http://web.resource.org/cc/DerivativeWorks");

        /** The Constant NOTICE. */
        public static final Behaviour NOTICE = new Behaviour("http://web.resource.org/cc/Notice");

        /** The Constant ATTRIBUTION. */
        public static final Behaviour ATTRIBUTION = new Behaviour("http://web.resource.org/cc/Attribution");

        /** The Constant COPYLEFT. */
        public static final Behaviour COPYLEFT = new Behaviour("http://web.resource.org/cc/Copyleft");

        /** The Constant NONCOMMERCIAL. */
        public static final Behaviour NONCOMMERCIAL = new Behaviour("http://web.resource.org/cc/Noncommercial");

        /** The uri. */
        private String uri;

        /**
         * Constructor.
         *
         * @param uri the uri
         */
        private Behaviour(final String uri) {
            this.uri = uri;
            Behaviour.LOOKUP.put(uri, this);
        }

        /**
         * Find by value.
         *
         * @param uri the uri
         * @return behaviour
         */
        public static Behaviour findByValue(final String uri) {
            return Behaviour.LOOKUP.get(uri);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return this.uri;
        }
    }
}

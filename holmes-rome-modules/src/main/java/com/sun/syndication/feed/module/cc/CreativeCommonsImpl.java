/*
 * AbstractCreativeCommons.java
 *
 * Created on November 20, 2005, 5:12 PM
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

package com.sun.syndication.feed.module.cc;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ToStringBean;
import com.sun.syndication.feed.module.cc.types.License;

/**
 * @version $Revision: 1.1 $
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class CreativeCommonsImpl implements CreativeCommons {
    private static final long serialVersionUID = -3853360076248474084L;

    public static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String RSS2_URI = "http://backend.userland.com/creativeCommonsRssModule";
    public static final String RSS1_URI = "http://web.resource.org/cc/";

    private License[] allLicenses;
    private License[] licenses;

    protected Object arrayCopy(Object[] source) {
        if (source == null) {
            return null;
        }

        Object[] array = (Object[]) Array.newInstance(source.getClass().getComponentType(), source.length);

        for (int i = 0; i < source.length; i++) {
            array[i] = source[i];
        }

        return array;
    }

    @Override
    public License[] getAllLicenses() {
        return allLicenses;
    }

    @Override
    public void setAllLicenses(License[] allLicenses) {
        this.allLicenses = allLicenses;
    }

    @Override
    public Class<?> getInterface() {
        return CreativeCommons.class;
    }

    @Override
    public String getUri() {
        return CreativeCommons.URI;
    }

    @Override
    public Object clone() {
        CreativeCommonsImpl clone = new CreativeCommonsImpl();
        clone.copyFrom(this);
        return clone;
    }

    @Override
    public void copyFrom(Object object) {
        CreativeCommons source = (CreativeCommons) object;
        this.setAllLicenses((License[]) arrayCopy(source.getAllLicenses()));
        this.setLicenses(source.getLicenses());
    }

    @Override
    public boolean equals(Object obj) {
        EqualsBean eBean = new EqualsBean(this.getClass(), this);

        return eBean.beanEquals(obj);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(allLicenses);
        result = prime * result + Arrays.hashCode(licenses);
        return result;
    }

    @Override
    public License[] getLicenses() {
        return licenses;
    }

    @Override
    public void setLicenses(License[] licenses) {
        this.licenses = licenses;
    }

    @Override
    public String toString() {

        ToStringBean tsb = new ToStringBean(CreativeCommonsImpl.class, this);
        return tsb.toString();
    }
}

/*
 * SubCategory.java
 *
 * Created on August 1, 2005, 7:24 PM
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
 *
 */
package com.sun.syndication.feed.module.itunes.types;

import java.io.Serializable;

/**
 * This class represents a Subcategor of a Category.
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.2 $
 */
public class Subcategory implements Cloneable, Serializable {
    private static final long serialVersionUID = -8563595355552684061L;

    private String name;

    /** Creates a new instance of SubCategory */
    public Subcategory() {
    }

    /** Creates a new instance of Category with a given name.
     * @param name Name of the category.
     */
    public Subcategory(String name) {
        this.setName(name);
    }

    /**
     * Returns the name of the subcategory.
     * @return Returns the name of the subcategory.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the subcategory.
     * @param name Set the name of the subcategory.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Clones the object.
     * @return Clone of the object.
     * @throws CloneNotSupportedException 
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        Subcategory sc = new Subcategory();
        sc.setName(this.getName());

        return sc;
    }

    /**
     * String representation of the object.
     * @return String representation of the object.
     */
    @Override
    public String toString() {
        return new StringBuffer(this.getName()).toString();
    }
}

/*
 * Copyright (C) 2012-2013  Cedric Cheneau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sun.syndication.feed.module.itunes.types;

import java.io.Serializable;

/**
 * This Category information. Basically a name and an optional Subcategory.
 * Categories are defined by Apple. See ITMS for a view.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 * @version $Revision: 1.2 $
 */
public final class Category implements Serializable, Cloneable {
    private static final long serialVersionUID = 5580598178604767283L;

    private String name;
    private Subcategory subcategory;

    /**
     * Creates a new instance of Category.
     */
    public Category() {
    }

    /**
     * Returns the name of the category
     *
     * @return Returns the name of the category
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the category
     *
     * @param name Sets the name of the category
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the Subcategory object for this category
     *
     * @return Returns the Subcategory object for this category
     */
    public Subcategory getSubcategory() {
        return subcategory;
    }

    /**
     * Sets the Subcategory object for this category
     *
     * @param subcategory Sets the Subcategory object for this category
     */
    public void setSubcategory(final Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    /**
     * Returns a copy of this category.
     *
     * @return Returns a copy of this category.
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        Category c = new Category();
        c.setName(this.name);
        if (this.getSubcategory() != null)
            c.setSubcategory((Subcategory) this.getSubcategory().clone());
        return c;
    }
}

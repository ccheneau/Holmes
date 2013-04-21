/*
 * Slash.java
 *
 * Created on November 19, 2005, 8:48 PM
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

package com.sun.syndication.feed.module.slash;

import java.io.Serializable;

import com.sun.syndication.feed.module.Module;

/** This interface represents the Slash RSS extension.
 * @version $Revision: 1.2 $
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public interface Slash extends Module, Serializable {

    /** The uri. */
    String URI = "http://purl.org/rss/1.0/modules/slash/";

    /**
     * Gets the section.
     *
     * @return the section
     */
    String getSection();

    /**
     * Sets the section.
     *
     * @param section the new section
     */
    void setSection(String section);

    /**
     * Gets the department.
     *
     * @return the department
     */
    String getDepartment();

    /**
     * Sets the department.
     *
     * @param department the new department
     */
    void setDepartment(String department);

    /**
     * Gets the comments.
     *
     * @return the comments
     */
    Integer getComments();

    /**
     * Sets the comments.
     *
     * @param comments the new comments
     */
    void setComments(Integer comments);

    /**
     * Gets the hit parade.
     *
     * @return the hit parade
     */
    Integer[] getHitParade();

    /**
     * Sets the hit parade.
     *
     * @param hitParade the new hit parade
     */
    void setHitParade(Integer[] hitParade);
}

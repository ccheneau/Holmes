/*
 * Unknown.java
 *
 * Created on November 18, 2005, 1:34 PM
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
package com.sun.syndication.feed.module.base;

import java.net.URL;

import com.sun.syndication.feed.module.base.types.IntUnit;

/**
 * This interface contains all the other schema elements that the document
 * doesn't associate with a particular type.
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet"
 *         Cooper</a>
 */

public interface Unknown extends GlobalInterface {

    /**
     * Sets the licenses.
     *
     * @param licenses the new licenses
     */
    void setLicenses(String[] licenses);

    /**
     * Gets the licenses.
     *
     * @return the licenses
     */
    String[] getLicenses();

    /**
     * Sets the operating systems.
     *
     * @param systems the new operating systems
     */
    void setOperatingSystems(String systems);

    /**
     * Gets the operating systems.
     *
     * @return the operating systems
     */
    String getOperatingSystems();

    /**
     * Sets the programming languages.
     *
     * @param languages the new programming languages
     */
    void setProgrammingLanguages(String[] languages);

    /**
     * Gets the programming languages.
     *
     * @return the programming languages
     */
    String[] getProgrammingLanguages();

    /**
     * Sets the related links.
     *
     * @param links the new related links
     */
    void setRelatedLinks(URL[] links);

    /**
     * Gets the related links.
     *
     * @return the related links
     */
    URL[] getRelatedLinks();

    /**
     * Sets the square footages.
     *
     * @param squareFootages the new square footages
     */
    void setSquareFootages(IntUnit[] squareFootages);

    /**
     * Gets the square footages.
     *
     * @return the square footages
     */
    IntUnit[] getSquareFootages();

    /**
     * Sets the subject areas.
     *
     * @param subjectAreas the new subject areas
     */
    void setSubjectAreas(String[] subjectAreas);

    /**
     * Gets the subject areas.
     *
     * @return the subject areas
     */
    String[] getSubjectAreas();
}

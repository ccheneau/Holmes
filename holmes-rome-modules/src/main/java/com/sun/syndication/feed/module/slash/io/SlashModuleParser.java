/*
 * SlashModuleParser.java
 *
 * Created on November 19, 2005, 9:19 PM
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

package com.sun.syndication.feed.module.slash.io;

import java.util.StringTokenizer;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.slash.Slash;
import com.sun.syndication.feed.module.slash.SlashImpl;
import com.sun.syndication.io.ModuleParser;

/** ModuleParser implementation for Slash RSS.
 * @version $Revision: 1.3 $
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class SlashModuleParser implements ModuleParser {

    private static final Namespace NS = Namespace.getNamespace(Slash.URI);

    /** Creates a new instance of SlashModuleParser */
    public SlashModuleParser() {
        super();
    }

    @Override
    public String getNamespaceUri() {
        return Slash.URI;
    }

    @Override
    public Module parse(final Element element) {
        Slash si = new SlashImpl();
        Element tag = element.getChild("hit_parade", SlashModuleParser.NS);
        if (tag != null) {
            StringTokenizer tok = new StringTokenizer(tag.getText(), ",");
            Integer[] hp = new Integer[tok.countTokens()];
            for (int i = 0; tok.hasMoreTokens(); i++) {
                hp[i] = new Integer(tok.nextToken());
            }
            si.setHitParade(hp);
        }
        tag = element.getChild("comments", SlashModuleParser.NS);
        if (tag != null) {
            si.setComments(new Integer(tag.getText()));
        }
        tag = element.getChild("department", SlashModuleParser.NS);
        if (tag != null) {
            si.setDepartment(tag.getText().trim());
        }
        tag = element.getChild("section", SlashModuleParser.NS);
        if (tag != null) {
            si.setSection(tag.getText().trim());
        }
        if (si.getHitParade().length > 0 || si.getComments() != null || si.getDepartment() != null || si.getSection() != null) {
            return si;
        }
        return null;
    }
}

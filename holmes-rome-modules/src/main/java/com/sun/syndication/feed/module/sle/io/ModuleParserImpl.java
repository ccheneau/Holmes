/*
 * ModuleParser.java
 *
 * Created on April 27, 2006, 10:37 PM
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
 */
package com.sun.syndication.feed.module.sle.io;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.sle.SimpleListExtension;
import com.sun.syndication.feed.module.sle.SimpleListExtensionImpl;
import com.sun.syndication.feed.module.sle.SleModule;
import com.sun.syndication.feed.module.sle.types.Group;
import com.sun.syndication.feed.module.sle.types.Sort;
import com.sun.syndication.io.ModuleParser;

/**
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class ModuleParserImpl implements ModuleParser {
    static final Namespace NS = Namespace.getNamespace("cf", SimpleListExtension.URI);

    /** Creates a new instance of ModuleParser */
    public ModuleParserImpl() {
        super();
    }

    /**
     * Returns the namespace URI this parser handles.
     * <p>
     *
     * @return the namespace URI.
     */
    @Override
    public String getNamespaceUri() {
        return SimpleListExtension.URI;
    }

    /**
     * Parses the XML node (JDOM element) extracting module information.
     * <p>
     *
     * @param element the XML node (JDOM element) to extract module information from.
     * @return a module instance, <b>null</b> if the element did not have module information.
     */
    @Override
    public Module parse(final Element element) {
        if (element.getChild("treatAs", NS) == null) {
            return null;
        }

        SimpleListExtension sle = new SimpleListExtensionImpl();
        sle.setTreatAs(element.getChildText("treatAs", NS));

        Element listInfo = element.getChild("listinfo", NS);
        List<?> groups = listInfo.getChildren("group", NS);
        ArrayList<Group> groupValues = new ArrayList<Group>();

        for (int i = 0; groups != null && i < groups.size(); i++) {
            Element ge = (Element) groups.get(i);
            Namespace ns = (ge.getAttribute("ns") == null) ? element.getNamespace() : Namespace.getNamespace(ge.getAttributeValue("ns"));
            String elementName = ge.getAttributeValue("element");
            String label = ge.getAttributeValue("label");
            groupValues.add(new Group(ns, elementName, label));
        }

        sle.setGroupFields(groupValues.toArray(new Group[groupValues.size()]));

        ArrayList<Sort> sortValues = new ArrayList<Sort>();

        List<?> sorts = listInfo.getChildren("sort", NS);

        for (int i = 0; sorts != null && i < sorts.size(); i++) {
            Element se = (Element) sorts.get(i);
            Namespace ns = (se.getAttribute("ns") == null) ? element.getNamespace() : Namespace.getNamespace(se.getAttributeValue("ns"));
            String elementName = se.getAttributeValue("element");
            String label = se.getAttributeValue("label");
            String dataType = se.getAttributeValue("data-type");
            boolean defaultOrder = (se.getAttribute("default") == null) ? false : Boolean.valueOf(se.getAttributeValue("default")).booleanValue();
            sortValues.add(new Sort(ns, elementName, dataType, label, defaultOrder));
        }

        sle.setSortFields(sortValues.toArray(new Sort[sortValues.size()]));
        insertValues(sle, element.getChildren());

        return sle;
    }

    /**
     * Adds the not null attribute.
     *
     * @param target the target
     * @param name the name
     * @param value the value
     */
    protected void addNotNullAttribute(final Element target, final String name, final Object value) {
        if (target == null || value == null) {
            return;
        } else {
            target.setAttribute(name, value.toString());
        }
    }

    /**
     * Insert values.
     *
     * @param sle the sle
     * @param elements the elements
     */
    public void insertValues(final SimpleListExtension sle, final List<?> elements) {
        for (int i = 0; elements != null && i < elements.size(); i++) {
            Element e = (Element) elements.get(i);
            Group[] groups = sle.getGroupFields();

            for (int g = 0; g < groups.length; g++) {
                Element value = e.getChild(groups[g].getElement(), groups[g].getNamespace());

                if (value == null) {
                    continue;
                }

                Element group = new Element("group", SleModule.URI);
                addNotNullAttribute(group, "element", groups[g].getElement());
                addNotNullAttribute(group, "label", groups[g].getLabel());
                addNotNullAttribute(group, "value", value.getText());
                addNotNullAttribute(group, "ns", groups[g].getNamespace().getURI());

                e.addContent(group);
            }

            Sort[] sorts = sle.getSortFields();

            for (int s = 0; s < sorts.length; s++) {
                Element sort = new Element("sort", SleModule.URI);
                // this is the default sort order, so I am just going to ignore 
                // the actual values and add a number type. It really shouldn't 
                // work this way. I should be checking to see if any of the elements
                // defined have a value then use that value. This will preserve the
                // sort order, however, if anyone is using the SleEntry to display
                // the value of the field, it will not give the correct value.
                // This, however, would require knowledge in the item parser that I don't 
                // have right now.
                if (sorts[s].getDefaultOrder()) {
                    sort.setAttribute("label", sorts[s].getLabel());
                    sort.setAttribute("value", Integer.toString(i));
                    sort.setAttribute("data-type", Sort.NUMBER_TYPE);
                    e.addContent(sort);

                    continue;
                }

                Element value = e.getChild(sorts[s].getElement(), sorts[s].getNamespace());

                if (value == null) {
                    continue;
                }

                addNotNullAttribute(sort, "label", sorts[s].getLabel());
                addNotNullAttribute(sort, "element", sorts[s].getElement());
                addNotNullAttribute(sort, "value", value.getText());
                addNotNullAttribute(sort, "data-type", sorts[s].getDataType());
                addNotNullAttribute(sort, "ns", sorts[s].getNamespace().getURI());
                e.addContent(sort);
            }
        }
    }
}

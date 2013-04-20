/*
 * ItemParser.java
 *
 * Created on April 29, 2006, 4:27 PM
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.sle.SleEntryImpl;
import com.sun.syndication.feed.module.sle.types.DateValue;
import com.sun.syndication.feed.module.sle.types.EntryValue;
import com.sun.syndication.feed.module.sle.types.NumberValue;
import com.sun.syndication.feed.module.sle.types.Sort;
import com.sun.syndication.feed.module.sle.types.StringValue;
import com.sun.syndication.io.impl.DateParser;

/**
 *
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class ItemParser implements com.sun.syndication.io.ModuleParser {
    /** Creates a new instance of ItemParser */
    public ItemParser() {
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
        return ModuleParserImpl.TEMP.getURI();
    }

    /**
     * Parses the XML node (JDOM element) extracting module information.
     * <p>
     *
     * @param element the XML node (JDOM element) to extract module information from.
     * @return a module instance, <b>null</b> if the element did not have module information.
     */
    @Override
    public Module parse(Element element) {
        SleEntryImpl sle = new SleEntryImpl();
        ArrayList<EntryValue> values = new ArrayList<EntryValue>();
        List<?> groups = element.getChildren("group", ModuleParserImpl.TEMP);

        for (int i = 0; groups != null && i < groups.size(); i++) {
            Element group = (Element) groups.get(i);
            StringValue value = new StringValue();
            value.setElement(group.getAttributeValue("element"));
            value.setLabel(group.getAttributeValue("label"));
            value.setValue(group.getAttributeValue("value"));
            if (group.getAttributeValue("ns") != null) value.setNamespace(Namespace.getNamespace(group.getAttributeValue("ns")));
            values.add(value);
            element.removeContent(group);
        }

        sle.setGroupValues(values.toArray(new EntryValue[values.size()]));
        values = values.size() == 0 ? values : new ArrayList<EntryValue>();

        List<?> sorts = element.getChildren("sort", ModuleParserImpl.TEMP);

        for (int i = 0; sorts != null && i < sorts.size(); i++) {
            Element sort = (Element) sorts.get(i);
            String dataType = sort.getAttributeValue("data-type");

            if (dataType == null || dataType.equals(Sort.TEXT_TYPE)) {
                StringValue value = new StringValue();
                value.setElement(sort.getAttributeValue("element"));
                value.setLabel(sort.getAttributeValue("label"));
                value.setValue(sort.getAttributeValue("value"));
                if (sort.getAttributeValue("ns") != null) value.setNamespace(Namespace.getNamespace(sort.getAttributeValue("ns")));
                else value.setNamespace(Namespace.NO_NAMESPACE);
                values.add(value);
                element.removeContent(sort);

            } else if (dataType.equals(Sort.DATE_TYPE)) {
                DateValue value = new DateValue();
                value.setElement(sort.getAttributeValue("element"));
                value.setLabel(sort.getAttributeValue("label"));
                if (sort.getAttributeValue("ns") != null) value.setNamespace(Namespace.getNamespace(sort.getAttributeValue("ns")));
                else value.setNamespace(Namespace.NO_NAMESPACE);

                Date dateValue = DateParser.parseRFC822(sort.getAttributeValue("value"));
                dateValue = dateValue == null ? DateParser.parseW3CDateTime(sort.getAttributeValue("value")) : dateValue;

                value.setValue(dateValue);
                values.add(value);
                element.removeContent(sort);
            } else if (dataType.equals(Sort.NUMBER_TYPE)) {
                NumberValue value = new NumberValue();
                value.setElement(sort.getAttributeValue("element"));
                value.setLabel(sort.getAttributeValue("label"));
                if (sort.getAttributeValue("ns") != null) value.setNamespace(Namespace.getNamespace(sort.getAttributeValue("ns")));
                else value.setNamespace(Namespace.NO_NAMESPACE);

                try {
                    value.setValue(new BigDecimal(sort.getAttributeValue("value")));
                } catch (NumberFormatException nfe) {
                    values.add(value);
                    element.removeContent(sort);
                }
            }
        }

        sle.setSortValues(values.toArray(new EntryValue[values.size()]));

        return sle;
    }
}

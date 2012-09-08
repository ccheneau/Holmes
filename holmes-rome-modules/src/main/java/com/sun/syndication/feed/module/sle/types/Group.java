/*
 * Group.java
 *
 * Created on April 27, 2006, 6:53 PM
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
package com.sun.syndication.feed.module.sle.types;

import java.io.Serializable;

import org.jdom.Namespace;

import com.sun.syndication.feed.impl.ObjectBean;

/**
 * The <code>cf:group</code> element is intended to inform the client that the&nbsp;property to which it 
 * 
 * refers is one that is “groupable” – that is, that the client should provide a user interface that 
 * 
 * allows the user to group or filter on the values of that property. Groupable&nbsp;properties should contain 
 * 
 * a small set of discrete values (e.g. book genres are perfect for groups). </p>
 * 
 * <p>The <code>cf:group</code> element contains the following attributes:</p>
 * 
 * <ul>
 *  <li><b>ns </b>- this attribute is the full namespace used in the property element. 
 *    If the attribute value is an empty string, it is assumed that the&nbsp;property 
 *    does not live in a namespace. If the ns attribute is omitted, the default 
 *    value is the empty string. In the example above, the ns attribute would contain 
 *    "http://www.example.com/book". It would <i>not</i> contain the namespace prefix. 
 *  </li>
 *  <li><b>element</b> - this attribute is the name of the property (without any 
 *    namespace). In the example above, the element attribute would contain "firstedition" 
 *    If this attribute is omitted, it is assumed that the label attribute is included 
 *    and that this <code>cf:group</code> element refers to the default sort order.</li>
 * 
 *  <li><b>label</b> - this attribute contains a human-readable name for the property 
 *    to which this <code>cf:group</code> element refers. If it is omitted, the 
 *    client should use the value of the "element" attribute as the human-readable 
 *    name. The "label" attribute is required if the "element" attribute is omitted.&nbsp;&nbsp;</li>
 * </ul>
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class Group implements Serializable, Cloneable {
    private static final long serialVersionUID = -7303429881984910769L;

    private Namespace namespace = Namespace.XML_NAMESPACE;
    private ObjectBean obj = new ObjectBean(Group.class, this);
    private String element;
    private String label;

    /**
     * Creates a new instance of Group
     * @param namespace Namespace of the element
     * @param element Name of the element
     * @param label Label for the grouping.
     */
    public Group(Namespace namespace, String element, String label) {
        this.namespace = namespace == null ? Namespace.XML_NAMESPACE : namespace;
        this.element = element;
        this.label = label;
    }

    /**
     * Returns the name of the element.
     * @return Returns the name of the element.
     */
    public String getElement() {
        return element;
    }

    /**
     * Returns the label of the element.
     * @return Returns the label of the element.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the namespace of the element.
     * @return Returns the namespace of the element.
     */
    public Namespace getNamespace() {
        return namespace;
    }

    @Override
    public Object clone() {
        return new Group(namespace, element, label);
    }

    @Override
    public boolean equals(Object o) {
        return obj.equals(o);
    }

    @Override
    public int hashCode() {
        return obj.hashCode();
    }

    @Override
    public String toString() {
        return obj.toString();
    }
}

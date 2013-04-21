/*
 * DateValue.java
 *
 * Created on April 29, 2006, 5:29 PM
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

import java.util.Date;

import org.jdom.Namespace;

import com.sun.syndication.feed.impl.ObjectBean;

/**
 * An EntryValue implementation representing a "date" data-type.
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class DateValue implements EntryValue {
    private static final long serialVersionUID = -9038958887033071251L;

    private Date value;
    private ObjectBean obj = new ObjectBean(DateValue.class, this);
    private String element;
    private String label;
    private transient Namespace namespace = Namespace.XML_NAMESPACE;

    /** Creates a new instance of DateValue */
    public DateValue() {
        super();
    }

    /**
     * Sets the element.
     *
     * @param element the new element
     */
    public void setElement(final String element) {
        this.element = element;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.module.sle.types.EntryValue#getElement()
     */
    @Override
    public String getElement() {
        return element;
    }

    /**
     * Sets the label.
     *
     * @param label the new label
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.module.sle.types.EntryValue#getLabel()
     */
    @Override
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(final Date value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.module.sle.types.EntryValue#getValue()
     */
    @Override
    public Comparable<?> getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        DateValue clone = new DateValue();
        clone.setElement(this.getElement());
        clone.setLabel(this.getLabel());
        clone.setValue(this.value);
        clone.setNamespace(this.namespace);
        return clone;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (o instanceof DateValue) return obj.equals(((DateValue) o).obj);
        else if (o instanceof ObjectBean) return obj.equals(o);
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return obj.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[Namespace: " + namespace + " Element:" + element + " Label:" + label + " Value:" + value + "]";
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.module.sle.types.EntryValue#getNamespace()
     */
    @Override
    public Namespace getNamespace() {
        return namespace;
    }

    /**
     * Sets the namespace.
     *
     * @param namespace the new namespace
     */
    public void setNamespace(final Namespace namespace) {
        this.namespace = namespace == null ? Namespace.XML_NAMESPACE : namespace;
    }
}

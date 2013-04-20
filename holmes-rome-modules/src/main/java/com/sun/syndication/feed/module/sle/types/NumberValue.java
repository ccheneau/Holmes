/*
 * NumberValue.java
 *
 * Created on April 29, 2006, 5:00 PM
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

import java.math.BigDecimal;

import org.jdom.Namespace;

import com.sun.syndication.feed.impl.ObjectBean;

/**
 * An EntryValue implementation for "number" data-type values.
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class NumberValue implements EntryValue {
    private static final long serialVersionUID = 6470462111988094892L;

    private BigDecimal value;
    private ObjectBean obj = new ObjectBean(NumberValue.class, this);
    private String element;
    private String label;
    private transient Namespace namespace = Namespace.XML_NAMESPACE;

    /** 
     * Creates a new instance of NumberValue.
     */
    public NumberValue() {
    }

    public void setElement(String element) {
        this.element = element;
    }

    @Override
    public String getElement() {
        return element;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public Comparable<?> getValue() {
        return value;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        NumberValue clone = new NumberValue();
        clone.setElement(this.getElement());
        clone.setLabel(this.getLabel());
        clone.setValue(this.value);
        clone.setNamespace(this.namespace);

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NumberValue) return obj.equals(((NumberValue) o).obj);
        else if (o instanceof ObjectBean) return obj.equals(o);
        else return false;
    }

    @Override
    public int hashCode() {
        return obj.hashCode();
    }

    @Override
    public String toString() {
        return "[Element:" + element + " Label:" + label + " Value:" + value + "]";
    }

    @Override
    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace == null ? Namespace.XML_NAMESPACE : namespace;
    }
}

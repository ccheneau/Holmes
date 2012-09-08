/*
 * SimpleListExtensionImpl.java
 *
 * Created on April 27, 2006, 10:41 PM
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
package com.sun.syndication.feed.module.sle;

import com.sun.syndication.feed.module.ModuleImpl;
import com.sun.syndication.feed.module.sle.types.Group;
import com.sun.syndication.feed.module.sle.types.Sort;

/**
 * 
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class SimpleListExtensionImpl extends ModuleImpl implements SimpleListExtension {
    private static final long serialVersionUID = 7781620211225688388L;

    private String treatAs = "list";
    private Group[] groupFields;
    private Sort[] sortFields;

    /** Creates a new instance of SimpleListExtensionImpl */
    public SimpleListExtensionImpl() {
        super(SimpleListExtensionImpl.class, SimpleListExtension.URI);
    }

    @Override
    public void setGroupFields(Group[] groupFields) {
        this.groupFields = groupFields;
    }

    @Override
    public Group[] getGroupFields() {
        return groupFields;
    }

    /**
     * Returns the interface the copyFrom works on.
     * <p>
     * This is useful when dealing with properties that may have multiple implementations.
     * For example, Module.
     * <p>
     *
     * @return the interface the copyFrom works on.
     */
    @Override
    public Class<?> getInterface() {
        return SimpleListExtension.class;
    }

    @Override
    public void setSortFields(Sort[] sortFields) {
        this.sortFields = sortFields;
    }

    @Override
    public Sort[] getSortFields() {
        return sortFields;
    }

    @Override
    public void setTreatAs(String treatAs) {
        this.treatAs = treatAs;
    }

    @Override
    public String getTreatAs() {
        return treatAs;
    }

    /**
     * Returns the URI of the module.
     * <p>
     *
     * @return URI of the module.
     */
    @Override
    public String getUri() {
        return SimpleListExtension.URI;
    }

    /**
     * Copies all the properties of the given bean into this one.
     * <p>
     * Any existing properties in this bean are lost.
     * <p>
     * This method is useful for moving from one implementation of a bean interface to another.
     * For example from the default SyndFeed bean implementation to a Hibernate ready implementation.
     * <p>
     *
     * @param obj the instance to copy properties from.
     */
    @Override
    public void copyFrom(Object obj) {
        SimpleListExtension sle = (SimpleListExtension) obj;
        this.setGroupFields(sle.getGroupFields().clone());
        this.setSortFields(sle.getSortFields().clone());
        this.setTreatAs(sle.getTreatAs());
    }
}

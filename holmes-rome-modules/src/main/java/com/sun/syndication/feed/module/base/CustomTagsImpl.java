/*
 * CustomTagsImpl.java
 *
 * Created on February 6, 2006, 12:26 AM
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
 */

package com.sun.syndication.feed.module.base;

import java.util.ArrayList;
import java.util.List;

/**
 * @version $Revision: 1.1 $
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class CustomTagsImpl implements CustomTags {
    private static final long serialVersionUID = 4964424461870045574L;

    private List<CustomTag> values;

    /** Creates a new instance of CustomTagsImpl */
    public CustomTagsImpl() {
    }

    @Override
    public List<CustomTag> getValues() {
        values = (values == null) ? new ArrayList<CustomTag>() : values;
        return values;
    }

    @Override
    public void setValues(List<CustomTag> values) {
        this.values = values;
    }

    @Override
    public void copyFrom(Object object) {
        CustomTags ct = (CustomTags) object;
        this.values = new ArrayList<CustomTag>(ct.getValues());
    }

    @Override
    public Object clone() {
        CustomTagsImpl cti = new CustomTagsImpl();
        cti.values = new ArrayList<CustomTag>(this.values);
        return cti;
    }

    @Override
    public Class<?> getInterface() {
        return CustomTags.class;
    }

    @Override
    public String getUri() {
        return CustomTags.URI;
    }

}

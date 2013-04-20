/*
 * CustomTagImpl.java
 *
 * Created on February 6, 2006, 12:06 AM
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

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

import com.sun.syndication.feed.module.base.types.DateTimeRange;
import com.sun.syndication.feed.module.base.types.FloatUnit;
import com.sun.syndication.feed.module.base.types.IntUnit;
import com.sun.syndication.feed.module.base.types.ShortDate;

/**
 * @version $Revision: 1.1 $
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class CustomTagImpl implements CustomTag, Cloneable {
    private static final long serialVersionUID = -5923754991476434312L;

    private Object value;
    private String name;

    /** Creates a new instance of CustomTagImpl */
    public CustomTagImpl(String name, String value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    public CustomTagImpl(String name, Integer value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    public CustomTagImpl(String name, Float value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    public CustomTagImpl(String name, IntUnit value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    public CustomTagImpl(String name, FloatUnit value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    public CustomTagImpl(String name, ShortDate value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    public CustomTagImpl(String name, Date value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    public CustomTagImpl(String name, DateTimeRange value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    public CustomTagImpl(String name, URL value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    public CustomTagImpl(String name, Boolean value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    public CustomTagImpl(String name, Location value) {
        if (name == null || value == null) {
            throw new NullPointerException("Name and Value cannont be null.");
        }
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CustomTag) {
            CustomTag tag = (CustomTag) o;
            if (this.name.equals(tag.getName()) && this.value.equals(tag.getValue())) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /**
      * Creates and returns a copy of this object.  The precise meaning 
      * of "copy" may depend on the class of the object. The general 
      * intent is that, for any object <tt>x</tt>, the expression:
      * <blockquote>
      * <pre>
      * x.clone() != x</pre></blockquote>
      * will be true, and that the expression:
      * <blockquote>
      * <pre>
      * x.clone().getClass() == x.getClass()</pre></blockquote>
      * will be <tt>true</tt>, but these are not absolute requirements. 
      * While it is typically the case that:
      * <blockquote>
      * <pre>
      * x.clone().equals(x)</pre></blockquote>
      * will be <tt>true</tt>, this is not an absolute requirement. 
      * <p>
      * By convention, the returned object should be obtained by calling
      * <tt>super.clone</tt>.  If a class and all of its superclasses (except
      * <tt>Object</tt>) obey this convention, it will be the case that
      * <tt>x.clone().getClass() == x.getClass()</tt>.
      * <p>
      * By convention, the object returned by this method should be independent
      * of this object (which is being cloned).  To achieve this independence,
      * it may be necessary to modify one or more fields of the object returned
      * by <tt>super.clone</tt> before returning it.  Typically, this means
      * copying any mutable objects that comprise the internal "deep structure"
      * of the object being cloned and replacing the references to these
      * objects with references to the copies.  If a class contains only
      * primitive fields or references to immutable objects, then it is usually
      * the case that no fields in the object returned by <tt>super.clone</tt>
      * need to be modified.
      * <p>
      * The method <tt>clone</tt> for class <tt>Object</tt> performs a 
      * specific cloning operation. First, if the class of this object does 
      * not implement the interface <tt>Cloneable</tt>, then a 
      * <tt>CloneNotSupportedException</tt> is thrown. Note that all arrays 
      * are considered to implement the interface <tt>Cloneable</tt>. 
      * Otherwise, this method creates a new instance of the class of this 
      * object and initializes all its fields with exactly the contents of 
      * the corresponding fields of this object, as if by assignment; the
      * contents of the fields are not themselves cloned. Thus, this method 
      * performs a "shallow copy" of this object, not a "deep copy" operation.
      * <p>
      * The class <tt>Object</tt> does not itself implement the interface 
      * <tt>Cloneable</tt>, so calling the <tt>clone</tt> method on an object 
      * whose class is <tt>Object</tt> will result in throwing an
      * exception at run time.
      * 
      * @return     a clone of this instance.
      * @exception  CloneNotSupportedException  if the object's class does not
      *               support the <code>Cloneable</code> interface. Subclasses
      *               that override the <code>clone</code> method can also
      *               throw this exception to indicate that an instance cannot
      *               be cloned.
      * @see java.lang.Cloneable
      */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        CustomTagImpl cti = new CustomTagImpl(this.name, "");
        cti.value = this.value;
        return cti;
    }

    @Override
    public String toString() {
        return "[custom name=\"" + this.name + "\" value=\"" + this.value.toString() + "\"]";
    }

    public static class Location implements Cloneable, Serializable {
        private static final long serialVersionUID = 5534836674772027424L;

        private String value;

        public Location(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            super.clone();
            return new Location(this.value);
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Location && ((Location) o).value.equals(this.value)) return true;
            else return false;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }
    }
}

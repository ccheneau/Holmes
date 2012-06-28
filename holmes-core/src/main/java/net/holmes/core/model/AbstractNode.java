/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.model;

import java.io.Serializable;

/**
 * The Class AbstractNode.
 */
public abstract class AbstractNode implements Comparable<AbstractNode>, Serializable
{
    private static final long serialVersionUID = 5909549322056486631L;

    /** The Constant TYPE_CONTAINER. */
    public final static String TYPE_CONTAINER = "container";

    /** The Constant TYPE_CONTENT. */
    public final static String TYPE_CONTENT = "content";

    /** The Constant TYPE_PODCAST. */
    public final static String TYPE_PODCAST = "podcastContainer";

    /** The Constant TYPE_PODCAST_ITEM. */
    public final static String TYPE_PODCAST_ITEM = "podcastItem";

    /** The id. */
    protected String id;

    /** The name. */
    protected String name;

    /** The path. */
    protected String path;

    /** The modifed date. */
    protected String modifedDate;

    /** The type. */
    protected String type;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets the path.
     *
     * @param path the new path
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(AbstractNode o)
    {
        if (this.getType().equals(o.getType())) return this.name.compareTo(o.name);
        else if (this.getType().equals(TYPE_CONTAINER)) return -1;
        else return 1;
    }

    /**
     * Gets the modifed date.
     *
     * @return the modifed date
     */
    public String getModifedDate()
    {
        return modifedDate;
    }

    /**
     * Sets the modifed date.
     *
     * @param modifedDate the new modifed date
     */
    public void setModifedDate(String modifedDate)
    {
        this.modifedDate = modifedDate;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Node [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", path=");
        builder.append(path);
        builder.append(", version=");
        builder.append(modifedDate);
        builder.append(", type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }

}

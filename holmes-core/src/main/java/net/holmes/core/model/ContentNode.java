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

/**
 * The Class ContentNode.
 */
public final class ContentNode extends AbstractNode
{
    private static final long serialVersionUID = 7636797693582850834L;

    /** The content type. */
    private ContentType contentType;

    /** The size. */
    private Long size;

    /**
     * Instantiates a new content node.
     */
    public ContentNode()
    {
        type = TYPE_CONTENT;
    }

    /**
     * Gets the content type.
     *
     * @return the content type
     */
    public ContentType getContentType()
    {
        return contentType;
    }

    /**
     * Sets the content type.
     *
     * @param contentType the new content type
     */
    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public Long getSize()
    {
        return size;
    }

    /**
     * Sets the size.
     *
     * @param size the new size
     */
    public void setSize(Long size)
    {
        this.size = size;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.model.AbstractNode#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ContentNode [contentType=");
        builder.append(contentType);
        builder.append(", size=");
        builder.append(size);
        builder.append(", id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", path=");
        builder.append(path);
        builder.append(", modifedDate=");
        builder.append(modifedDate);
        builder.append(", type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }

}

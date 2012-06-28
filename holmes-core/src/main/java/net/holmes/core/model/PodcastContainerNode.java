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
 * The Class PodcastContainerNode.
 */
public final class PodcastContainerNode extends AbstractNode
{
    private static final long serialVersionUID = 1970454850754589123L;

    /** The url. */
    private String url;

    /**
     * Instantiates a new podcast container node.
     */
    public PodcastContainerNode()
    {
        type = TYPE_PODCAST;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url the new url
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see net.holmes.core.model.AbstractNode#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PodcastNode [url=");
        builder.append(url);
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

package com.sun.syndication.feed.module.sle.io;

import org.jdom.Namespace;

import com.sun.syndication.feed.impl.ObjectBean;

/**
 * The Class LabelNamespaceElement.
 */
public class LabelNamespaceElement {

    /** The element. */
    private String element;

    /** The label. */
    private String label;

    /** The namespace. */
    private Namespace namespace;

    /** The obj. */
    private ObjectBean obj = new ObjectBean(LabelNamespaceElement.class, this);

    /**
     * Constructor.
     *
     * @param label the label
     * @param namespace the namespace
     * @param element the element
     */
    public LabelNamespaceElement(final String label, final Namespace namespace, final String element) {
        this.element = element;
        this.label = label;
        this.namespace = namespace;
    }

    public String getElement() {
        return element;
    }

    public void setElement(final String element) {
        this.element = element;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(final Namespace namespace) {
        this.namespace = namespace;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (o instanceof LabelNamespaceElement) return obj.equals(((LabelNamespaceElement) o).obj);
        else if (o instanceof ObjectBean) return obj.equals(o);
        else return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((element == null) ? 0 : element.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((obj == null) ? 0 : obj.hashCode());
        return result;
    }
}

package com.sun.syndication.feed.module.sle.io;

import org.jdom.Namespace;

import com.sun.syndication.feed.impl.ObjectBean;

public class LabelNamespaceElement {
    private String element;
    private String label;
    private Namespace namespace;
    private ObjectBean obj = new ObjectBean(LabelNamespaceElement.class, this);

    public LabelNamespaceElement(String label, Namespace namespace, String element) {
        this.element = element;
        this.label = label;
        this.namespace = namespace;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean equals(Object o) {
        return obj.equals(o);
    }

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
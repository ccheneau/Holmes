package com.sun.syndication.feed.module.sle.io;

import com.sun.syndication.feed.impl.ObjectBean;
import org.jdom.Namespace;


public class LabelNamespaceElement {
    private String element;
    private String label;
    private Namespace namespace;
    private ObjectBean obj = new ObjectBean(LabelNamespaceElement.class, this);
    
    public LabelNamespaceElement(String label, Namespace namespace, String element){
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
    
    public boolean equals(Object o){
        return obj.equals(o);
    }
    
}
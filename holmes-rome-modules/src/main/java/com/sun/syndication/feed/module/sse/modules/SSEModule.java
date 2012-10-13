package com.sun.syndication.feed.module.sse.modules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;

/**
 * The base module for SSE data synchronization.  Defines a namespace, uri, and basic
 * copying operations.
 */
public abstract class SSEModule implements Module {
    private static final long serialVersionUID = -5851855076942649892L;

    public static final String SSE_SCHEMA_URI = "http://www.microsoft.com/schemas/rss/sse";

    // a default prefix to use for sse tags
    public static final String PREFIX = "sx";
    public static final Namespace SSE_NS = Namespace.getNamespace(PREFIX, SSE_SCHEMA_URI);

    public static final Set<Namespace> NAMESPACES;

    static {
        Set<Namespace> nss = new HashSet<Namespace>();
        nss.add(SSEModule.SSE_NS);
        NAMESPACES = Collections.unmodifiableSet(nss);
    }

    @Override
    public String getUri() {
        return SSE_SCHEMA_URI;
    }

    @Override
    public Class<?> getInterface() {
        return getClass();
    }

    @Override
    public Object clone() {
        try {
            SSEModule clone = this.getClass().newInstance();
            clone.copyFrom(this);
            return clone;
        } catch (InstantiationException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public abstract void copyFrom(Object obj);
}

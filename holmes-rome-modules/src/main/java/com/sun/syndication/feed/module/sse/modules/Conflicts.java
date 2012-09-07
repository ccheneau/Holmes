package com.sun.syndication.feed.module.sse.modules;

/**
 * <sx:conflicts> element within <sx:sync>
 * <p>
 * The sx:conflicts element MUST contain one or more sx:conflict sub-elements.
 */
public class Conflicts extends SSEModule {
    public static final String NAME = "conflicts";

    public void copyFrom(Object obj) {
        // nothing to copy, just a place-holder
    }
}

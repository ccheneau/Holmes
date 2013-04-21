package com.sun.syndication.feed.module.sse.modules;

/**
 * <sx:conflicts> element within <sx:sync>
 * <p>
 * The sx:conflicts element MUST contain one or more sx:conflict sub-elements.
 */
public class Conflicts extends SSEModule {
    private static final long serialVersionUID = 1822070442644732830L;

    public static final String NAME = "conflicts";

    @Override
    public void copyFrom(final Object obj) {
        // nothing to copy, just a place-holder
    }
}

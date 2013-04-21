package com.sun.syndication.feed.module.sse;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.filter.Filter;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.sse.modules.Conflict;
import com.sun.syndication.feed.module.sse.modules.Conflicts;
import com.sun.syndication.feed.module.sse.modules.History;
import com.sun.syndication.feed.module.sse.modules.Related;
import com.sun.syndication.feed.module.sse.modules.SSEModule;
import com.sun.syndication.feed.module.sse.modules.Sharing;
import com.sun.syndication.feed.module.sse.modules.Sync;
import com.sun.syndication.feed.module.sse.modules.Update;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.DelegatingModuleParser;
import com.sun.syndication.io.WireFeedParser;
import com.sun.syndication.io.impl.DateParser;
import com.sun.syndication.io.impl.RSS20Parser;

/**
 * Parses embedded SSE content from RSS channel and item content.
 *
 * @author <a href="mailto:ldornin@dev.java.net">ldornin</a>
 */
public class SSE091Parser implements DelegatingModuleParser {
    // root of the sharing element
    /** The rss parser. */
    private RSS20Parser rssParser;

    /**
     * Creates a new instance of SSE091Parser.
     */
    public SSE091Parser() {
        rssParser = null;
    }

    @Override
    public void setFeedParser(final WireFeedParser feedParser) {
        if (feedParser instanceof RSS20Parser) this.rssParser = (RSS20Parser) feedParser;
    }

    @Override
    public String getNamespaceUri() {
        return SSEModule.SSE_SCHEMA_URI;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.io.ModuleParser#parse(org.jdom.Element)
     */
    @Override
    public Module parse(final Element element) {
        SSEModule sseModule = null;
        String name = element.getName();

        if (name.equals("rss")) {
            sseModule = parseSharing(element);
        } else if (name.equals("item")) {
            sseModule = parseSync(element);
        }
        return sseModule;
    }

    /**
     * Parses the sharing.
     *
     * @param element the element
     * @return sharing
     */
    private Sharing parseSharing(final Element element) {
        Element root = getRoot(element);

        Sharing sharing = null;
        Element sharingChild = root.getChild(Sharing.NAME, SSEModule.SSE_NS);
        if (sharingChild != null) {
            sharing = new Sharing();
            sharing.setOrdered(parseBooleanAttr(sharingChild, Sharing.ORDERED_ATTRIBUTE));
            sharing.setSince(parseDateAttribute(sharingChild, Sharing.SINCE_ATTRIBUTE));
            sharing.setUntil(parseDateAttribute(sharingChild, Sharing.UNTIL_ATTRIBUTE));
            sharing.setWindow(parseIntegerAttribute(sharingChild, Sharing.WINDOW_ATTRIBUTE));
            sharing.setVersion(parseStringAttribute(sharingChild, Sharing.VERSION_ATTRIBUTE));
            parseRelated(root, sharing);
        }

        return sharing;
    }

    /**
     * Parses the related.
     *
     * @param root the root
     * @param sharing the sharing
     */
    private void parseRelated(final Element root, final Sharing sharing) {
        Related related;
        Element relatedChild = root.getChild(Related.NAME, SSEModule.SSE_NS);
        if (relatedChild != null) {
            related = new Related();
            related.setLink(parseStringAttribute(relatedChild, Related.LINK_ATTRIBUTE));
            related.setSince(parseDateAttribute(relatedChild, Related.SINCE_ATTRIBUTE));
            related.setTitle(parseStringAttribute(relatedChild, Related.TITLE_ATTRIBUTE));
            related.setType(parseIntegerAttribute(relatedChild, Related.TYPE_ATTRIBUTE));
            related.setUntil(parseDateAttribute(relatedChild, Related.UNTIL_ATTRIBUTE));
            sharing.setRelated(related);
        }
    }

    /**
     * Parses the sync.
     *
     * @param element the element
     * @return sync
     */
    private Sync parseSync(final Element element) {
        // Now I am going to get the item specific tags
        Element syncChild = element.getChild(Sync.NAME, SSEModule.SSE_NS);
        Sync sync = null;

        if (syncChild != null) {
            sync = new Sync();
            sync.setId(parseStringAttribute(syncChild, Sync.ID_ATTRIBUTE));
            sync.setVersion(parseIntegerAttribute(syncChild, Sync.VERSION_ATTRIBUTE));
            sync.setDeleted(parseBooleanAttr(syncChild, Sync.DELETED_ATTRIBUTE));
            sync.setConflict(parseBooleanAttr(syncChild, Sync.CONFLICT_ATTRIBUTE));
            sync.setHistory(parseHistory(syncChild));
            sync.setConflicts(parseConflicts(syncChild));
        }
        return sync;
    }

    /**
     * Parses the conflicts.
     *
     * @param syncElement the sync element
     * @return list
     */
    private List<Conflict> parseConflicts(final Element syncElement) {
        List<Conflict> conflicts = null;

        List<?> conflictsContent = syncElement.getContent(new ContentFilter(Conflicts.NAME));
        for (Iterator<?> conflictsIter = conflictsContent.iterator(); conflictsIter.hasNext();) {
            Element conflictsElement = (Element) conflictsIter.next();

            List<?> conflictContent = conflictsElement.getContent(new ContentFilter(Conflict.NAME));
            for (Iterator<?> conflictIter = conflictContent.iterator(); conflictIter.hasNext();) {
                Element conflictElement = (Element) conflictIter.next();

                Conflict conflict = new Conflict();
                conflict.setBy(parseStringAttribute(conflictElement, Conflict.BY_ATTRIBUTE));
                conflict.setWhen(parseDateAttribute(conflictElement, Conflict.WHEN_ATTRIBUTE));
                conflict.setVersion(parseIntegerAttribute(conflictElement, Conflict.VERSION_ATTRIBUTE));

                List<?> conflictItemContent = conflictElement.getContent(new ContentFilter("item"));
                for (Iterator<?> conflictItemIter = conflictItemContent.iterator(); conflictItemIter.hasNext();) {
                    Element conflictItemElement = (Element) conflictItemIter.next();
                    Element root = getRoot(conflictItemElement);
                    Item conflictItem = rssParser.parseItem(root, conflictItemElement);
                    conflict.setItem(conflictItem);

                    if (conflicts == null) {
                        conflicts = new ArrayList<Conflict>();
                    }
                    conflicts.add(conflict);
                }
            }
        }
        return conflicts;
    }

    /**
     * Gets the root.
     *
     * @param start the start
     * @return the root
     */
    private Element getRoot(final Element start) {
        // reach up to grab the sharing element out of the root
        Element root = start;

        while (root.getParent() != null && root.getParent() instanceof Element) {
            root = (Element) root.getParent();
        }
        return root;
    }

    /**
     * Parses the history.
     *
     * @param historyElement the history element
     * @return history
     */
    private History parseHistory(final Element historyElement) {
        Element historyContent = getFirstContent(historyElement, History.NAME);

        History history = null;
        if (historyContent != null) {
            history = new History();
            history.setBy(parseStringAttribute(historyContent, History.BY_ATTRIBUTE));
            history.setWhen(parseDateAttribute(historyContent, History.WHEN_ATTRIBUTE));
            parseUpdates(historyContent, history);
        }
        return history;
    }

    /**
     * Gets the first content.
     *
     * @param element the element
     * @param name the name
     * @return the first content
     */
    private Element getFirstContent(final Element element, final String name) {
        List<?> filterList = element.getContent(new ContentFilter(name));
        Element firstContent = null;
        if (filterList != null && filterList.size() > 0) {
            firstContent = (Element) filterList.get(0);
        }
        return firstContent;
    }

    /**
     * Parses the updates.
     *
     * @param historyChild the history child
     * @param history the history
     */
    private void parseUpdates(final Element historyChild, final History history) {
        List<?> updatedChildren = historyChild.getContent(new ContentFilter(Update.NAME));
        for (Iterator<?> childIter = updatedChildren.iterator(); childIter.hasNext();) {
            Element updateChild = (Element) childIter.next();
            Update update = new Update();
            update.setBy(parseStringAttribute(updateChild, Update.BY_ATTRIBUTE));
            update.setWhen(parseDateAttribute(updateChild, Update.WHEN_ATTRIBUTE));
            history.addUpdate(update);
        }
    }

    /**
     * Parses the string attribute.
     *
     * @param syncChild the sync child
     * @param attrName the attr name
     * @return string
     */
    private String parseStringAttribute(final Element syncChild, final String attrName) {
        Attribute idAttribute = syncChild.getAttribute(attrName);
        return idAttribute != null ? idAttribute.getValue().trim() : null;
    }

    /**
     * Parses the integer attribute.
     *
     * @param sharingChild the sharing child
     * @param attrName the attr name
     * @return integer
     */
    private Integer parseIntegerAttribute(final Element sharingChild, final String attrName) {
        Attribute integerAttribute = sharingChild.getAttribute(attrName);
        Integer integerAttr = null;
        if (integerAttribute != null) {
            try {
                integerAttr = Integer.valueOf(integerAttribute.getIntValue());
            } catch (DataConversionException e) {
                // dont use the data
                integerAttr = null;
            }
        }
        return integerAttr;
    }

    /**
     * Parses the boolean attr.
     *
     * @param sharingChild the sharing child
     * @param attrName the attr name
     * @return boolean
     */
    private Boolean parseBooleanAttr(final Element sharingChild, final String attrName) {
        Attribute attribute = sharingChild.getAttribute(attrName);
        Boolean attrValue = null;
        if (attribute != null) {
            try {
                attrValue = Boolean.valueOf(attribute.getBooleanValue());
            } catch (DataConversionException e) {
                // dont use the data
                attrValue = null;
            }
        }
        return attrValue;
    }

    /**
     * Parses the date attribute.
     *
     * @param childElement the child element
     * @param attrName the attr name
     * @return date
     */
    private Date parseDateAttribute(final Element childElement, final String attrName) {
        Attribute dateAttribute = childElement.getAttribute(attrName);
        Date date = null;
        if (dateAttribute != null) {
            // SSE spec requires the timezone to be 'GMT'
            // admittedly, this is a bit heavy-handed
            String dateAttr = dateAttribute.getValue().trim();
            return DateParser.parseRFC822(dateAttr);
        }
        return date;
    }

    /**
     * The Class ContentFilter.
     */
    private static final class ContentFilter implements Filter {
        private static final long serialVersionUID = -6834384294039340975L;

        private String name;

        /**
         * Constructor.
         *
         * @param name the name
         */
        private ContentFilter(final String name) {
            this.name = name;
        }

        /* (non-Javadoc)
         * @see org.jdom.filter.Filter#matches(java.lang.Object)
         */
        @Override
        public boolean matches(final Object object) {
            return object instanceof Element && name.equals(((Element) object).getName());
        }
    }
}

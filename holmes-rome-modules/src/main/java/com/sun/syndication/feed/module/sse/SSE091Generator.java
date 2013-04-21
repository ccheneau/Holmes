package com.sun.syndication.feed.module.sse;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;

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
import com.sun.syndication.io.DelegatingModuleGenerator;
import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.impl.DateParser;
import com.sun.syndication.io.impl.RSS20Generator;

/**
 * Generator for the SSE Module.
 * <p>
 * @author <a href="mailto:ldornin@gmail.com">Laird Dornin</a>
 */
public class SSE091Generator implements DelegatingModuleGenerator {
    private RSS20Generator parentGenerator;

    /**
     * Constructor.
     */
    public SSE091Generator() {
        parentGenerator = null;
    }

    @Override
    public void setFeedGenerator(final WireFeedGenerator feedGenerator) {
        if (feedGenerator instanceof RSS20Generator) parentGenerator = (RSS20Generator) feedGenerator;
    }

    @Override
    public String getNamespaceUri() {
        return SSEModule.SSE_SCHEMA_URI;
    }

    /**
     * Returns a set with all the URIs (JDOM Namespace elements) this module generator uses.
     * <p/>
     * It is used by the the feed generators to add their namespace definition in
     * the root element of the generated document (forward-missing of Java 5.0 Generics).
     * <p/>
     * @return a set with all the URIs (JDOM Namespace elements) this module generator uses.
     */
    @Override
    public Set<Namespace> getNamespaces() {
        return SSEModule.NAMESPACES;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.io.ModuleGenerator#generate(com.sun.syndication.feed.module.Module, org.jdom.Element)
     */
    @Override
    public void generate(final Module module, final Element element) {
        if (!(module instanceof SSEModule)) {
            return;
        }

        SSEModule sseModule = (SSEModule) module;

        if (sseModule instanceof Sharing) {
            Sharing sharing = (Sharing) sseModule;
            // add sse namespace
            Element root = element;
            while (root.getParent() != null && root.getParent() instanceof Element) {
                root = (Element) root.getParent();
            }
            root.addNamespaceDeclaration(SSEModule.SSE_NS);

            generateSharing(sharing, root);
        } else if (sseModule instanceof Sync) {
            generateSync((Sync) sseModule, element);
        }
    }

    /**
     * Generate sharing.
     *
     * @param sharing the sharing
     * @param parent the parent
     */
    private void generateSharing(final Sharing sharing, final Element parent) {
        // inject sse sharingModule element
        Element sharingElement = new Element(Sharing.NAME, SSEModule.SSE_NS);
        generateAttribute(sharingElement, Sharing.UNTIL_ATTRIBUTE, sharing.getUntil());
        generateAttribute(sharingElement, Sharing.SINCE_ATTRIBUTE, sharing.getSince());
        generateAttribute(sharingElement, Sharing.ORDERED_ATTRIBUTE, sharing.getOrdered());
        generateAttribute(sharingElement, Sharing.WINDOW_ATTRIBUTE, sharing.getWindow());
        generateAttribute(sharingElement, Sharing.VERSION_ATTRIBUTE, sharing.getVersion());

        // add sharing as the first element of the rss root
        parent.addContent(0, sharingElement);

        Related related = sharing.getRelated();
        if (related != null) {
            generateRelated(related);
        }
    }

    /**
     * Generate related.
     *
     * @param related the related
     */
    private void generateRelated(final Related related) {
        Element relatedElement = new Element(Related.NAME, SSEModule.SSE_NS);
        generateAttribute(relatedElement, Related.SINCE_ATTRIBUTE, related.getSince());
        generateAttribute(relatedElement, Related.UNTIL_ATTRIBUTE, related.getUntil());
        generateAttribute(relatedElement, Related.LINK_ATTRIBUTE, related.getLink());
        generateAttribute(relatedElement, Related.TITLE_ATTRIBUTE, related.getTitle());
        generateAttribute(relatedElement, Related.TYPE_ATTRIBUTE, related.getType());
    }

    /**
     * Generate sync.
     *
     * @param sync the sync
     * @param parent the parent
     */
    protected void generateSync(final Sync sync, final Element parent) {
        Element syncElement = new Element(Sync.NAME, SSEModule.SSE_NS);
        generateAttribute(syncElement, Sync.DELETED_ATTRIBUTE, sync.isDeleted());
        generateAttribute(syncElement, Sync.VERSION_ATTRIBUTE, sync.getVersion());
        generateAttribute(syncElement, Sync.ID_ATTRIBUTE, sync.getId());
        generateAttribute(syncElement, Sync.CONFLICT_ATTRIBUTE, sync.isConflict());
        generateHistory(syncElement, sync.getHistory());
        generateConflicts(syncElement, sync.getConflicts());
        parent.addContent(syncElement);
    }

    /**
     * Generate conflicts.
     *
     * @param syncElement the sync element
     * @param conflicts the conflicts
     */
    private void generateConflicts(final Element syncElement, final List<Conflict> conflicts) {
        if (conflicts != null) {
            Element conflictsElement = new Element(Conflicts.NAME, SSEModule.SSE_NS);
            for (Iterator<Conflict> confictIter = conflicts.iterator(); confictIter.hasNext();) {
                Element conflictElement = new Element(Conflict.NAME, SSEModule.SSE_NS);
                Conflict conflict = confictIter.next();
                generateAttribute(conflictElement, Conflict.BY_ATTRIBUTE, conflict.getBy());
                generateAttribute(conflictElement, Conflict.VERSION_ATTRIBUTE, conflict.getVersion());
                generateAttribute(conflictElement, Conflict.WHEN_ATTRIBUTE, conflict.getWhen());
                generateItem(conflictElement, conflict.getItem());
                conflictsElement.addContent(conflictElement);
            }
            syncElement.addContent(conflictsElement);
        }
    }

    /**
     * Generate item.
     *
     * @param conflictElement the conflict element
     * @param item the item
     */
    private void generateItem(final Element conflictElement, final Item item) {
        if (item != null) {
            Element itemElement = new Element("item");
            parentGenerator.populateItem(item, itemElement, 0);
            parentGenerator.generateItemModules(item.getModules(), itemElement);
            conflictElement.addContent(itemElement);
        }
    }

    /**
     * Generate history.
     *
     * @param syncElement the sync element
     * @param history the history
     */
    private void generateHistory(final Element syncElement, final History history) {
        if (history != null) {
            Element historyElement = new Element(History.NAME, SSEModule.SSE_NS);
            generateAttribute(historyElement, History.BY_ATTRIBUTE, history.getBy());
            generateAttribute(historyElement, History.WHEN_ATTRIBUTE, history.getWhen());
            generateUpdates(historyElement, history.getUpdates());
            syncElement.addContent(historyElement);
        }
    }

    /**
     * Generate updates.
     *
     * @param historyElement the history element
     * @param updates the updates
     */
    private void generateUpdates(final Element historyElement, final List<Update> updates) {
        if (updates != null) {
            for (Iterator<Update> updateIter = updates.iterator(); updateIter.hasNext();) {
                Element updateElement = new Element(Update.NAME, SSEModule.SSE_NS);
                Update update = updateIter.next();
                generateAttribute(updateElement, Update.BY_ATTRIBUTE, update.getBy());
                generateAttribute(updateElement, Update.WHEN_ATTRIBUTE, update.getWhen());
                historyElement.addContent(updateElement);
            }
        }
    }

    /**
     * Generate attribute.
     *
     * @param syncElement the sync element
     * @param attrName the attr name
     * @param attribute the attribute
     */
    private void generateAttribute(final Element syncElement, final String attrName, final Object attribute) {
        if (attribute != null) {
            syncElement.setAttribute(attrName, toString(attribute));
        }
    }

    /**
     * To string.
     *
     * @param o the o
     * @return string
     */
    private String toString(final Object o) {
        if (o != null) {
            if (o instanceof Date) {
                return DateParser.formatRFC822((Date) o);
            } else {
                return o.toString();
            }
        }
        return "";
    }
}

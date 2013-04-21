/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.sun.syndication.feed.module.opensearch.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;

import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
import com.sun.syndication.feed.module.opensearch.entity.OSQuery;
import com.sun.syndication.io.ModuleParser;

/**
 * @author Michael W. Nassif (enrouteinc@gmail.com)
 * OpenSearch implementation of the ModuleParser class
 */
public class OpenSearchModuleParser implements ModuleParser {
    private static final Logger LOGGER = Logger.getLogger(OpenSearchModuleParser.class.getName());
    private static final Namespace OS_NS = Namespace.getNamespace("opensearch", OpenSearchModule.URI);

    @Override
    public String getNamespaceUri() {
        return OpenSearchModule.URI;
    }

    @Override
    public Module parse(final Element dcRoot) {

        URL baseURI = findBaseURI(dcRoot);

        boolean foundSomething = false;
        OpenSearchModule osm = new OpenSearchModuleImpl();

        Element e = dcRoot.getChild("totalResults", OS_NS);

        if (e != null) {
            foundSomething = true;
            try {
                osm.setTotalResults(Integer.parseInt(e.getText()));
            } catch (NumberFormatException ex) {
                // Ignore setting the field and post a warning
                LOGGER.log(Level.WARNING, "The element totalResults must be an integer value: " + ex.getMessage());
            }
        }

        e = dcRoot.getChild("itemsPerPage", OS_NS);
        if (e != null) {
            try {
                osm.setItemsPerPage(Integer.parseInt(e.getText()));
            } catch (NumberFormatException ex) {
                // Ignore setting the field and post a warning
                LOGGER.log(Level.WARNING, "The element itemsPerPage must be an integer value: " + ex.getMessage());
            }
        }

        e = dcRoot.getChild("startIndex", OS_NS);
        if (e != null) {
            try {
                osm.setStartIndex(Integer.parseInt(e.getText()));
            } catch (NumberFormatException ex) {
                // Ignore setting the field and post a warning
                LOGGER.log(Level.WARNING, "The element startIndex must be an integer value: " + ex.getMessage());
            }
        }

        List<?> queries = dcRoot.getChildren("Query", OS_NS);

        if (queries != null && queries.size() > 0) {

            // Create the OSQuery list 
            List<OSQuery> osqList = new LinkedList<OSQuery>();

            for (Iterator<?> iter = queries.iterator(); iter.hasNext();) {
                e = (Element) iter.next();
                osqList.add(parseQuery(e));
            }

            osm.setQueries(osqList);
        }

        e = dcRoot.getChild("link", OS_NS);

        if (e != null) osm.setLink(parseLink(e, baseURI));

        return foundSomething ? osm : null;
    }

    /**
     * Parses the query.
     *
     * @param element the element
     * @return oS query
     */
    private static OSQuery parseQuery(final Element element) {
        OSQuery query = new OSQuery();

        String att = element.getAttributeValue("role");
        query.setRole(att);

        att = element.getAttributeValue("osd");
        query.setOsd(att);

        att = element.getAttributeValue("searchTerms");
        query.setSearchTerms(att);

        att = element.getAttributeValue("title");
        query.setTitle(att);

        try {
            // someones mistake should not cause the parser to fail, since these are only optional attributes
            att = element.getAttributeValue("totalResults");
            if (att != null) {
                query.setTotalResults(Integer.parseInt(att));
            }

            att = element.getAttributeValue("startPage");
            if (att != null) {
                query.setStartPage(Integer.parseInt(att));
            }

        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Exception caught while trying to parse a non-numeric Query attribute: " + ex.getMessage());
        }

        return query;
    }

    /**
     * Parses the link.
     *
     * @param e the e
     * @param baseURI the base uri
     * @return link
     */
    private static Link parseLink(final Element e, final URL baseURI) {
        Link link = new Link();

        String att = e.getAttributeValue("rel");
        if (att != null) link.setRel(att);

        att = e.getAttributeValue("type");
        if (att != null) link.setType(att);

        att = e.getAttributeValue("href");
        if (att != null) {
            if (isRelativeURI(att)) link.setHref(resolveURI(baseURI, e, ""));
            else link.setHref(att);
        }

        att = e.getAttributeValue("hreflang");
        if (att != null) {
            link.setHreflang(att);
        }
        att = e.getAttributeValue("length");
        if (att != null) {
            link.setLength(Integer.valueOf(att));
        }

        return link;
    }

    /**
     * Checks if is relative uri.
     *
     * @param uri the uri
     * @return true, if is relative uri
     */
    private static boolean isRelativeURI(final String uri) {
        if (uri.startsWith("http://") || uri.startsWith("https://") || uri.startsWith("/")) {
            return false;
        }
        return true;
    }

    /**
     * Use xml:base attributes at feed and entry level to resolve relative links.
     *
     * @param baseURI the base uri
     * @param parent the parent
     * @param url the url
     * @return string
     */
    private static String resolveURI(final URL baseURI, final Parent parent, final String url) {
        String sUrl = url.equals(".") || url.equals("./") ? "" : url;
        if (isRelativeURI(sUrl) && parent != null && parent instanceof Element) {
            Attribute baseAtt = ((Element) parent).getAttribute("base", Namespace.XML_NAMESPACE);
            String xmlBase = (baseAtt == null) ? "" : baseAtt.getValue();
            if (!isRelativeURI(xmlBase) && !xmlBase.endsWith("/")) {
                xmlBase = xmlBase.substring(0, xmlBase.lastIndexOf("/") + 1);
            }
            return resolveURI(baseURI, parent.getParent(), xmlBase + sUrl);
        } else if (isRelativeURI(sUrl) && parent == null) {
            return baseURI + sUrl;
        } else if (baseURI != null && sUrl.startsWith("/")) {
            String hostURI = baseURI.getProtocol() + "://" + baseURI.getHost();
            if (baseURI.getPort() != baseURI.getDefaultPort()) {
                hostURI = hostURI + ":" + baseURI.getPort();
            }
            return hostURI + sUrl;
        }
        return sUrl;
    }

    /**
     * Use feed links and/or xml:base attribute to determine baseURI of feed.
     *
     * @param root the root
     * @return url
     */
    private static URL findBaseURI(final Element root) {
        URL baseURI = null;
        List<?> linksList = root.getChildren("link", OS_NS);
        if (linksList != null) {
            for (Iterator<?> links = linksList.iterator(); links.hasNext();) {
                Element link = (Element) links.next();
                if (!root.equals(link.getParent())) break;
                String href = link.getAttribute("href").getValue();
                if (link.getAttribute("rel", OS_NS) == null || link.getAttribute("rel", OS_NS).getValue().equals("alternate")) {
                    href = resolveURI(null, link, href);
                    try {
                        baseURI = new URL(href);
                        break;
                    } catch (MalformedURLException e) {
                        LOGGER.log(Level.WARNING, "Base URI is malformed: " + href);
                    }
                }
            }
        }
        return baseURI;
    }
}

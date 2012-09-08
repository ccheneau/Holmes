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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.module.ModuleImpl;
import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
import com.sun.syndication.feed.module.opensearch.entity.OSQuery;

/**
 * @author Michael W. Nassif (enrouteinc@gmail.com)
 * OpenSearch Module implementation
 */
public class OpenSearchModuleImpl extends ModuleImpl implements OpenSearchModule, Serializable {
    private static final long serialVersionUID = 1L;

    private int totalResults = -1;
    private int startIndex = 1;
    private int itemsPerPage = -1;
    private Link link;
    private List<OSQuery> queries;

    public OpenSearchModuleImpl() {
        super(OpenSearchModuleImpl.class, OpenSearchModuleImpl.URI);
    }

    /**
     * @return Returns the itemsPerPage.
     */
    @Override
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    /**
     * @param itemsPerPage The itemsPerPage to set.
     */
    @Override
    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    /**
     * @return Returns the link.
     */
    @Override
    public Link getLink() {
        return link;
    }

    /**
     * @param link The link to set.
     */
    @Override
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     * @return Returns the queries.
     */
    @Override
    public List<OSQuery> getQueries() {
        this.queries = (queries == null) ? new LinkedList<OSQuery>() : queries;

        return this.queries;
    }

    /**
     * @param queries The queries to set.
     */
    @Override
    public void setQueries(List<OSQuery> queries) {
        this.queries = queries;
    }

    @Override
    public void addQuery(OSQuery query) {
        if (queries != null) {
            queries.add(query);
        }
        else {
            queries = new LinkedList<OSQuery>();
            queries.add(query);
        }
    }

    /**
     * @return Returns the startIndex.
     */
    @Override
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * @param startIndex The startIndex to set.
     */
    @Override
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * @return Returns the totalResults.
     */
    @Override
    public int getTotalResults() {
        return totalResults;
    }

    /**
     * @param totalResults The totalResults to set.
     */
    @Override
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.CopyFrom#copyFrom(java.lang.Object)
     */
    @Override
    public void copyFrom(Object obj) {
        OpenSearchModule osm = (OpenSearchModuleImpl) obj;

        setTotalResults(osm.getTotalResults());
        setItemsPerPage(osm.getItemsPerPage());
        setStartIndex(osm.getStartIndex());
        setLink(osm.getLink());

        // setQueries(osm.getQueries());
    }

    /* (non-Javadoc)
     * @see com.sun.syndication.feed.CopyFrom#getInterface()
     */
    @Override
    public Class<?> getInterface() {
        return OpenSearchModule.class;
    }
}

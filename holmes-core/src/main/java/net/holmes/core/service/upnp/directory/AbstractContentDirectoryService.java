/*
 * Copyright (C) 2012-2015  Cedric Cheneau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.holmes.core.service.upnp.directory;

import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.csv.CSV;
import org.fourthline.cling.model.types.csv.CSVString;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;

import java.util.List;

/**
 * Simple ContentDirectory service skeleton.
 */

@UpnpService(
        serviceId = @UpnpServiceId("ContentDirectory"),
        serviceType = @UpnpServiceType(value = "ContentDirectory", version = 1))

@UpnpStateVariables({
        @UpnpStateVariable(name = "A_ARG_TYPE_ObjectID", sendEvents = false, datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_Result", sendEvents = false, datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_BrowseFlag", sendEvents = false, datatype = "string", allowedValuesEnum = BrowseFlag.class),
        @UpnpStateVariable(name = "A_ARG_TYPE_Filter", sendEvents = false, datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_SortCriteria", sendEvents = false, datatype = "string"),
        @UpnpStateVariable(name = "A_ARG_TYPE_Index", sendEvents = false, datatype = "ui4"),
        @UpnpStateVariable(name = "A_ARG_TYPE_Count", sendEvents = false, datatype = "ui4"),
        @UpnpStateVariable(name = "A_ARG_TYPE_UpdateID", sendEvents = false, datatype = "ui4"),
        @UpnpStateVariable(name = "A_ARG_TYPE_URI", sendEvents = false, datatype = "uri"),
        @UpnpStateVariable(name = "A_ARG_TYPE_SearchCriteria", sendEvents = false, datatype = "string")})
public abstract class AbstractContentDirectoryService {

    @UpnpStateVariable(sendEvents = false)
    private final CSV<String> searchCapabilities;
    @UpnpStateVariable(sendEvents = false)
    private final CSV<String> sortCapabilities;
    @UpnpStateVariable(sendEvents = true, defaultValue = "0", eventMaximumRateMilliseconds = 200)
    private final UnsignedIntegerFourBytes systemUpdateID = new UnsignedIntegerFourBytes(0);

    protected AbstractContentDirectoryService(final List<String> searchCapabilities, final List<String> sortCapabilities) {
        this.searchCapabilities = new CSVString();
        this.searchCapabilities.addAll(searchCapabilities);
        this.sortCapabilities = new CSVString();
        this.sortCapabilities.addAll(sortCapabilities);
    }

    /**
     * Get search capabilities.
     *
     * @return search capabilities
     */
    @UpnpAction(out = @UpnpOutputArgument(name = "SearchCaps"))
    public CSV<String> getSearchCapabilities() {
        return searchCapabilities;
    }

    /**
     * Get sor capabilities.
     *
     * @return sort capabilities
     */
    @UpnpAction(out = @UpnpOutputArgument(name = "SortCaps"))
    public CSV<String> getSortCapabilities() {
        return sortCapabilities;
    }

    /**
     * Get system update id.
     *
     * @return system update id
     */
    @UpnpAction(out = @UpnpOutputArgument(name = "Id"))
    public synchronized UnsignedIntegerFourBytes getSystemUpdateID() {
        return systemUpdateID;
    }

    /**
     * Browse for content.
     *
     * @param objectId         object id
     * @param browseFlag       browse flag
     * @param filter           filter
     * @param firstResult      first result
     * @param maxResults       max results
     * @param orderBy          order by
     * @param remoteClientInfo remote client info
     * @return browse result
     * @throws ContentDirectoryException
     */
    @SuppressWarnings("UnusedParameters")
    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Result", stateVariable = "A_ARG_TYPE_Result", getterName = "getResult"),
            @UpnpOutputArgument(name = "NumberReturned", stateVariable = "A_ARG_TYPE_Count", getterName = "getCount"),
            @UpnpOutputArgument(name = "TotalMatches", stateVariable = "A_ARG_TYPE_Count", getterName = "getTotalMatches"),
            @UpnpOutputArgument(name = "UpdateID", stateVariable = "A_ARG_TYPE_UpdateID", getterName = "getContainerUpdateID")})
    public BrowseResult browse(
            @UpnpInputArgument(name = "ObjectID", aliases = "ContainerID") String objectId,
            @UpnpInputArgument(name = "BrowseFlag") String browseFlag,
            @UpnpInputArgument(name = "Filter") String filter,
            @UpnpInputArgument(name = "StartingIndex", stateVariable = "A_ARG_TYPE_Index") UnsignedIntegerFourBytes firstResult,
            @UpnpInputArgument(name = "RequestedCount", stateVariable = "A_ARG_TYPE_Count") UnsignedIntegerFourBytes maxResults,
            @UpnpInputArgument(name = "SortCriteria") String orderBy,
            RemoteClientInfo remoteClientInfo) throws ContentDirectoryException {

        return browse(objectId, BrowseFlag.valueOrNullOf(browseFlag), firstResult.getValue(), maxResults.getValue(), remoteClientInfo);
    }

    /**
     * Implement this method to implement browsing of your content.
     * <p>
     * This is a required action defined by <em>ContentDirectory:1</em>.
     * </p>
     * <p>
     * You should wrap any exception into a {@link ContentDirectoryException}, so a property
     * error message can be returned to control points.
     * </p>
     *
     * @param objectID         object id
     * @param browseFlag       browse flag
     * @param firstResult      first results
     * @param maxResults       max result
     * @param remoteClientInfo remote client info
     * @return browse result
     * @throws ContentDirectoryException
     */
    protected abstract BrowseResult browse(String objectID, BrowseFlag browseFlag, long firstResult, long maxResults,
                                           RemoteClientInfo remoteClientInfo) throws ContentDirectoryException;
}

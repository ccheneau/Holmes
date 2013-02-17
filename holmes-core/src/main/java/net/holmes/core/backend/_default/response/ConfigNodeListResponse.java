/**
* Copyright (C) 2012-2013  Cedric Cheneau
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
package net.holmes.core.backend._default.response;

import java.util.Collection;

public class ConfigNodeListResponse {
    private final int page;
    private final int total;
    private final int records;
    private final Collection<ConfigNode> rows;

    public ConfigNodeListResponse(int page, int total, int records, Collection<ConfigNode> rows) {
        this.page = page;
        this.total = total;
        this.records = records;
        this.rows = rows;
    }

    public int getPage() {
        return page;
    }

    public int getTotal() {
        return total;
    }

    public int getRecords() {
        return records;
    }

    public Collection<ConfigNode> getRows() {
        return rows;
    }
}

/**
* Copyright (c) 2012 Cedric Cheneau
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package net.holmes.core.backend.model;

import java.io.Serializable;
import java.util.Collection;

/**
 * The Class ListGridRowsResponse.
 */
public class ListGridRowsResponse implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 936714380923230317L;

    /** The page. */
    private int page;

    /** The total. */
    private int total;

    /** The records. */
    private int records;

    /** The rows. */
    private Collection<GridRow> rows;

    /**
     * Gets the page.
     *
     * @return the page
     */
    public int getPage()
    {
        return page;
    }

    /**
     * Sets the page.
     *
     * @param page the new page
     */
    public void setPage(int page)
    {
        this.page = page;
    }

    /**
     * Gets the total.
     *
     * @return the total
     */
    public int getTotal()
    {
        return total;
    }

    /**
     * Sets the total.
     *
     * @param total the new total
     */
    public void setTotal(int total)
    {
        this.total = total;
    }

    /**
     * Gets the records.
     *
     * @return the records
     */
    public int getRecords()
    {
        return records;
    }

    /**
     * Sets the records.
     *
     * @param records the new records
     */
    public void setRecords(int records)
    {
        this.records = records;
    }

    /**
     * Gets the rows.
     *
     * @return the rows
     */
    public Collection<GridRow> getRows()
    {
        return rows;
    }

    /**
     * Sets the rows.
     *
     * @param rows the new rows
     */
    public void setRows(Collection<GridRow> rows)
    {
        this.rows = rows;
    }

}

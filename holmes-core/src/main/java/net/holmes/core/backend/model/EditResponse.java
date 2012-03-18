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

import net.holmes.core.backend.ErrorCode;

/**
 * The Class EditResponse.
 */
public class EditResponse implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4829835251156005404L;

    /** The status. */
    private boolean status;

    /** The message. */
    private String message;

    /** The id. */
    private String id;

    /** The operation. */
    private String operation;

    /** The error code. */
    private int errorCode;

    /**
     * Gets the status.
     *
     * @return the status
     */
    public boolean getStatus()
    {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(boolean status)
    {
        this.status = status;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message the new message
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Gets the operation.
     *
     * @return the operation
     */
    public String getOperation()
    {
        return operation;
    }

    /**
     * Sets the operation.
     *
     * @param operation the new operation
     */
    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public int getErrorCode()
    {
        return errorCode;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode the new error code
     */
    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode the new error code
     */
    public void setErrorCode(ErrorCode errorCode)
    {
        this.errorCode = errorCode.code();
        this.message = errorCode.message();
    }

}

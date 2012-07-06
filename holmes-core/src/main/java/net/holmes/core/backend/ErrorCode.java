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
package net.holmes.core.backend;

public enum ErrorCode {
    NO_ERROR(0, "OK"), //
    FOLDER_ALREADY_EXISTS(1, "This folder already exists"), //
    UNKNOWN_FOLDER(2, "Unkonwn folder"), //
    UNKNOWN_OPERATION(3, "Unkonwn operation"), //
    PATH_NOT_EXIST(4, "Path does not exist"), //
    PATH_NOT_DIRECTORY(5, "Path must be a dictory"), // ;
    PATH_NOT_READABLE(6, "Path is not readable"), //
    MALFORMATTED_URL(7, "Malformatted URL"), //
    DUPLICATED_FOLDER(8, "Duplicated folder"), //
    EMPTY_SERVER_NAME(9, "Server name is not defined"), //
    EMPTY_HTTP_SERVER_PORT(10, "Http server port is not defined");

    private final int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}

/**
* Copyright (C) 2012  Cedric Cheneau
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
package net.holmes.core.backend;

public enum ErrorCode {
    NO_ERROR(0), //
    FOLDER_ALREADY_EXISTS(1), //
    UNKNOWN_FOLDER(2), //
    UNKNOWN_OPERATION(3), //
    PATH_NOT_EXIST(4), //
    PATH_NOT_DIRECTORY(5), //
    PATH_NOT_READABLE(6), //
    MALFORMATTED_URL(7), //
    DUPLICATED_FOLDER(8), //
    EMPTY_SERVER_NAME(9), //
    EMPTY_HTTP_SERVER_PORT(10);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}

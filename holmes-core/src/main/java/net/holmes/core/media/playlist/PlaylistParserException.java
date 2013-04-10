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

package net.holmes.core.media.playlist;

public class PlaylistParserException extends Exception {
    private static final long serialVersionUID = -5068408691447981899L;

    public PlaylistParserException() {
        super();
    }

    public PlaylistParserException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public PlaylistParserException(String arg0) {
        super(arg0);
    }

    public PlaylistParserException(Throwable arg0) {
        super(arg0);
    }
}

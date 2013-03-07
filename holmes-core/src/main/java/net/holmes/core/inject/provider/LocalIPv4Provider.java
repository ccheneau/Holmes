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

package net.holmes.core.inject.provider;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.inject.Provider;

/**
 * Get local IPv4 address (InetAddress.getLocalHost().getHostAddress() does not work on Linux)
 */
public final class LocalIPv4Provider implements Provider<String> {

    @Override
    public String get() {
        try {
            for (Enumeration<NetworkInterface> intfaces = NetworkInterface.getNetworkInterfaces(); intfaces.hasMoreElements();) {
                NetworkInterface intf = intfaces.nextElement();
                for (Enumeration<InetAddress> inetAddresses = intf.getInetAddresses(); inetAddresses.hasMoreElements();) {
                    InetAddress inetAddr = inetAddresses.nextElement();
                    if (inetAddr instanceof Inet4Address && !inetAddr.isLoopbackAddress() && inetAddr.isSiteLocalAddress()) {
                        return inetAddr.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}

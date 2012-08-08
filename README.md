Holmes
============================

Holmes stands for Home Light Media Server.

A Java application that supports DLNA/UPnP protocol for playing videos, music, pictures and podcasts (RSS) to compatible devices.

This application runs on Windows 7 and Linux Ubuntu with [VLC](http://www.videolan.org/vlc/) 2.x, Freebox V6 (a french set top box) and LG SmartTV devices

## DESCRIPTION

Holmes uses the following frameworks:

* [Cling](http://teleal.org/projects/cling/) as UPnP server
* [Netty](http://www.jboss.org/netty/) as HTTP server (streaming and HTML pages)
* [Guice](http://code.google.com/p/google-guice/) for dependency injection
* [Jersey](http://jersey.java.net/) and [Jackson](http://jackson.codehaus.org/) for back-end REST api
* [Rome](http://java.net/projects/rome/) for RSS parsing
* [EHCache](http://ehcache.org/) for caching
* [JQueryUI](http://jqueryui.com/) for user interface

 
## REQUIREMENTS

Java 6 and [Apache Maven](http://maven.apache.org/) are required to build this project.

## INSTALL

Download the zip from GitHub or compile sources using Maven (the archive is generated in holmes-package/target directory).

Run the startup script in bin directory.

## USAGE

After starting the application, use a web browser to access the user interface at http://ip_of_your_server:8085/ or the system tray icon

## LICENSE

Copyright (c) 2012 Cedric Cheneau

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

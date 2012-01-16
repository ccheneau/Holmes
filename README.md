Holmes
============================

Holmes stands for Home Light Media Server

A Java application that supports UPnp protocol for playing videos, music, pictures and podcasts to compatible devices.

Support for AirPlay is planned for next release

## DESCRIPTION

Holmes uses the following frameworks:
* [Cling](http://teleal.org/projects/cling/) for UPnp
* [Netty](http://www.jboss.org/netty/) as HTTP server (streaming and HTML pages)
* [Guice](http://code.google.com/p/google-guice/) for dependency injection
* [Jersey](http://jersey.java.net/) and [Jackson](http://jackson.codehaus.org/) for back-end management
* [EHCache](http://ehcache.org/) for caching
* [JQueryUI](http://jqueryui.com/) for user interface (any help from web designer would be appreciated :-) )

This application has been tested on Windows with [VLC](http://www.videolan.org/vlc/) 1.2 beta and Freebox V6 (a french set top box)
 
## REQUIREMENTS

Java 6 and [Apache Maven](http://maven.apache.org/) are required to build this project.

## INSTALL

Compile using Maven then unzip the archive generated in holmes-package module.
Run the startup.bat script

## Usage

After starting the application, use a web browser to access the user interface at http://ip_of_your_server:8085/
(nor localhost or 127.0.0.1 are accepted for the moment)


## LICENSE

Copyright (c) 2012 Cedric Cheneau

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
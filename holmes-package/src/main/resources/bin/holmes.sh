#!/bin/sh

# Copyright (c) 2012 Cedric Cheneau

# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:

# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.

holmes_start () {
  # get script path
  SCRIPT_PATH="$0"
  while [ -h "$SCRIPT_PATH" ] ; do
    ls=`ls -ld "$SCRIPT_PATH"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      SCRIPT_PATH="$link"
    else
      SCRIPT_PATH="`dirname "$SCRIPT_PATH"`/$link"
    fi
  done

  # set holmes home
  currentDir=`pwd`
  HOLMES_HOME=`dirname "$SCRIPT_PATH"`/..
  HOLMES_HOME=`cd "$HOLMES_HOME" && pwd`
  cd "$currentDir"

  # locate java
  if [ -n "$JAVA_HOME"  ] ; then
    JAVA="$JAVA_HOME/bin/java"
  else
    JAVA="`which java`"
  fi
  if [ ! -x "$JAVA" ] ; then
    echo "[ERROR]: JAVA_HOME is not defined correctly or java is not in path."
    exit 1
  fi
  
  # run holmes
  JAVA_ARGS="-Dnet.holmes.home=\"$HOLMES_HOME\" -Dfile.encoding=UTF-8"
  $JAVA -Xmx30m $JAVA_ARGS -jar $HOLMES_HOME/lib/holmes-core--${project.version}.jar 1>$HOLMES_HOME/log/systemOut.log 2>$HOLMES_HOME/log/systemErr.log &
  echo "[INFO] Holmes server started"
}

holmes_stop() {
  echo "TODO stop"
}

holmes_force_stop() {
  echo "TODO force-stop"
}

holmes_status() {
  echo "TODO status"
}

case "$1" in
  start)
    holmes_start
    ;;

  stop)
    holmes_stop
    ;;
    
  force-stop)
    holmes_force_stop
    ;;

  status)
    holmes_status
    ;;

  *)
    echo "Usage: holmes.sh {start|stop|force-stop|status}"
    exit 1
    ;;
esac
exit 0
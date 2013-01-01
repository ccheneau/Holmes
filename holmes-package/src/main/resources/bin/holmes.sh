#!/bin/bash

# Copyright (C) 2012-2013  Cedric Cheneau
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
  current_dir=`pwd`
  HOLMES_HOME=`dirname "$SCRIPT_PATH"`/..
  HOLMES_HOME=`cd "$HOLMES_HOME" && pwd`
  cd "$current_dir"

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
 
  # check log dir 
  if [ ! -x "$HOME/.holmes/log" ] ; then
    mkdir -p "$HOME/.holmes/log"
  fi
  
  # run holmes
  echo "Starting Holmes"
  JAVA_ARGS="-Dnet.holmes.home=$HOLMES_HOME -Dfile.encoding=UTF-8"
  $JAVA -Xmx30m $JAVA_ARGS -jar $HOLMES_HOME/lib/holmes-core-${project.version}.jar 1>$HOME/.holmes/log/systemOut.log 2>$HOME/.holmes/log/systemErr.log &
  sleep 3
  holmes_status
}

holmes_stop() {
  holmes_pid=`ps -eaf |grep "net.holmes.home" | grep -v "grep" | head -1 | awk '{ print $2 }'`
  if [ ! -z "$holmes_pid" ] ; then
    echo "Stopping Holmes (pid $holmes_pid)"
    kill -s 15 $holmes_pid
    sleep 3
  fi
  holmes_status
}

holmes_force_stop() {
  holmes_pid=`ps -eaf |grep "net.holmes.home" | grep -v "grep" | head -1 | awk '{ print $2 }'`
  if [ ! -z "$holmes_pid" ] ; then
    echo "Killing Holmes (pid $holmes_pid). Professor Moriarty finally wins :-("
    kill -9 $holmes_pid
  fi
  holmes_status
}

holmes_status() {
  holmes_pid=`ps -eaf |grep "net.holmes.home" | grep -v "grep" | head -1 | awk '{ print $2 }'`
  if [ -z "$holmes_pid" ] ; then
    echo "Holmes is not running"
  else
    echo "Holmes is running (pid $holmes_pid)"
  fi
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
    echo "Usage: holmes.sh (start|stop|force-stop|status)"
    exit 1
    ;;
esac
exit 0

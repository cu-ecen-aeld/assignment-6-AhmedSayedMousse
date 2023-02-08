#!/bin/bash
# Sample init script for the misc drivers module <rubini@linux.it>
if [ $# -eq 2 ]
then
	module=$2
else
	module=hello
fi
case "$1" in
  start)
     echo -n "Loading module hello"
     /usr/bin/module_load $module
	 /usr/bin/module_load faulty
     echo "."
     ;;
  stop)
     echo -n "Unloading module hello"
     /usr/bin/module_unload
     echo "."
     ;;
  force-reload|restart)
     echo -n "Reloading module hello"
     /usr/bin/module_unload
     /usr/bin/module_load $module
     /usr/bin/module_load faulty
     echo "."
     ;;
  *)
     echo "Usage: $0 {start|stop|restart|force-reload}"
     exit 1
esac

exit 0

#!/bin/bash
# Sample init script for the misc drivers module <rubini@linux.it>

case "$1" in
  start)
     echo -n "Loading module hello\n"
	/usr/bin/module_load faulty
	modprobe hello
     echo "."
     ;;
  stop)
     echo -n "Unloading module hello\n"
     /usr/bin/module_unload
     echo "."
     ;;
  force-reload|restart)
     echo -n "Reloading module hello\n"
     /usr/bin/module_unload
	/usr/bin/module_load faulty
	modprobe hello
     echo "."
     ;;
  *)
     echo "Usage: $0 {start|stop|restart|force-reload}"
     exit 1
esac

exit 0

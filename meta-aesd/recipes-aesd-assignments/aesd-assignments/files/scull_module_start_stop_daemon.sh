#!/bin/bash
# Sample init script for the scull driver module <rubini@linux.it>

case "$1" in
  start)
     echo -n "Loading scull module"
     /usr/bin/scull_load
     echo "."
     ;;
  stop)
     echo -n "Unloading scull module"
     /usr/bin/scull_unload
     echo "."
     ;;
  force-reload|restart)
     echo -n "Reloading scull module"
  /usr/bin/scull_unload
     /usr/bin/scull_load
     echo "."
     ;;
  *)
     echo "Usage: $0 {start|stop|restart|force-reload}"
     exit 1
esac

exit 0

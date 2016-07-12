# chkconfig: 35 90 12
# description: miniSipServer
#
# Get function from functions library
. /etc/init.d/functions
# Start the service MiniSipServer
start() {
        initlog -c "echo -n Starting MiniSipServer: "
       /opt/sipserver/msscli&
        ### Create the lock file ###
        touch /var/lock/subsys/msscli
        success $"MiniSipServer server startup"
        echo
}
# Restart the service FOO
stop() {
        initlog -c "echo -n Stopping FOO server: "
        killproc msscli
        ### Now, delete the lock file ###
        rm -f /var/lock/subsys/msscli
        echo
}
### main logic ###
case "$1" in
  start)
        start
        ;;
  stop)
        stop
        ;;
  status)
        status FOO
        ;;
  restart|reload|condrestart)
        stop
        start
        ;;
  *)
        echo $"Usage: $0 {start|stop|restart|reload|status}"
        exit 1
esac
exit 0
#!/usr/bin/env bash

#1 paramter image path
#2 patamter ssh status
#3 patametr minisip status

option=$1
imagePath=$2
sshStatus=$3
miniSip=$4
vncStatus=$5

function mount_dir {
 
     # /system/xbin/busybox mount -t proc proc $1/proc
     # /system/xbin/busybox mount -o bind dev $1/dev
     # /system/xbin/busybox mount -t sysfs sysfs $1/sys
     # /system/xbin/busybox mount -t devpts devpts $1/dev/pts

	echo "Mount /dev /dev/pts /proc /sys"

	for f in /dev /dev/pts /proc /sys ; do mount -o bind /$f $1/$f ; done
}

function umount_dir {

	echo "Kill process"

	#kill ssh
	chroot $1 /bin/bash -x <<'EOF'
ps -ef | grep sshd | grep -v grep | awk '{print $2}'
ps -ef | grep sshd | grep -v grep | awk '{print $2}' | xargs kill -9

EOF

 echo "Stop VNC Server"
                 chroot $1 /bin/bash -x <<'EOF'
                  service vncservice stop
EOF


		echo "Stop VNC Server - DONE"


	#kill minisipserver
	chroot $1 /bin/bash -x <<'EOF'
ps -ef | grep msscli | grep -v grep | awk '{print $2}' |
ps -ef | grep msscli | grep -v grep | awk '{print $2}' | xargs kill -9
EOF

	echo "Unmount partitions: "

	for i in '.*' '*'
		do
			parts=$(cat /proc/mounts | awk '{print $2}' | grep "^$1/$i$" | sort -r)

			for p in $parts
				do
					pp=$(echo $p | sed  "s|$1/*|/|g")
					echo "$pp ..."
					echo "umount $p"
					umount $p
					#[ $? -eq 0 ] && echo "done" || echo "fail"
			done
	done

}
  
function start_services {
 
 
 #	  echo "Start SSH server? [y/N]"
  #        read  startssh
  	echo "Starting services"
   	startssh=$sshStatus
   	if [[ $startssh == "y" || $startssh == "Y" || $startssh == "yes" || $startssh == "Yes" ]]
 		then
		 
		 echo "Start SSH Server"
                 chroot $1 /bin/bash -x <<'EOF'
                  service ssh start
EOF
          fi

		echo "Start SSH Server - DONE"
	
	#  echo "Start VNC server? [y/N]"
	#  read  startvnc
	startvnc=$vncStatus
	  if [[ $startvnc == "y" || $startvnc == "Y" || $startvnc == "yes" || $startvnc == "Yes" ]]
 	then

 		echo "Start VNC server"
		chroot $1 /bin/bash -x <<'EOF'
		 service vncserver start

EOF
		echo "Start VNC server DONE"
		sleep 2

	else
		echo "Nie startuje VNC"
	fi
	echo "Start VNC Server - DONE"

	startsip=$miniSip
	if [[ $startsip == "y" || $startsip == "Y" || $startsip == "yes" || $startsip == "Yes" ]]
		then
		#start miniSipServer
		echo "Start miniSipserver"
		chroot $1 /bin/bash -x <<'EOF'
	service minisipserver start
EOF

		echo "Start miniSipserver - DONE"
	fi
	  
	  

}

function check_first_time {

#check if system start first time
             if [ ! -f  "$1/first" ]; then
                   #first start 
                   touch $1/first
                   echo `date` > $1/first
             	   
		echo "nameserver 8.8.8.8" > $1/etc/resolv.conf
		echo "nameserver 8.8.4.4" >> $1/etc/resolv.conf
		echo "search local" >> $1/etc/resolv.conf

		 chroot $1 /bin/bash -x <<'EOF'
                 /debootstrap/debootstrap --second-stage
EOF
		cat <<EOT > $1/etc/apt/sources.list
deb http://ftp.pl.debian.org/debian stable main
deb-src http://ftp.pl.debian.org/debian stable main

deb http://ftp.debian.org/debian/ wheezy-updates main
deb-src http://ftp.debian.org/debian/ wheezy-updates main

deb http://security.debian.org/ wheezy/updates main
deb-src http://security.debian.org/ wheezy/updates main
EOT
	     fi
		

}

echo "Parametr 1: $1"
echo "Paramter 2: $2"
echo "Paramter 3: $3"
echo "Parametr 4: $4"

export MOUNTDIR=/mnt/linux
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$PATH

if [ $option == "start" ]; then

	echo "Start"

	/system/xbin/busybox mount -o remount,rw,exec,dev,suid /
	# echo "Default configuration [y/N]?"
	# read response
	# response=yes

	#create folder for mount

	mkdir -p $MOUNTDIR

	if [ -d "$MOUNTDIR" ]; then
		echo "Mount folder on $MOUNTDIR"
	else
		echo "Folder create error"
	exit
	fi

	#mount image
	#/system/xbin/busybox mount $1 $MOUNTDIR

	echo "/system/xbin/busybox mount $imagePath $MOUNTDIR"
	/system/xbin/busybox mount $imagePath /mnt/linux

	#mount important dir
	#mount_dir $MOUNTDIR
	/system/xbin/busybox mount -t proc /proc /mnt/linux/proc
	echo "proc zamontowany"
	/system/xbin/busybox mount -o bind /dev /mnt/linux/dev
	echo "dev zamontowany"
	/system/xbin/busybox mount -t sysfs sysfs /mnt/linux/proc/sys
	echo "sysfs zamontowany"
	/system/xbin/busybox mount -t devpts devpts /mnt/linux/dev/pts
	echo "dev/pts zamontowany"
	#check if system start first time
	#check_first_time $MOUNTDIR

	#chroot $MOUNTDIR /bin/bash -c "export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$PATH"
	#chroot $MOUNTDIR /bin/bash -c "ifconfig"

	start_services $MOUNTDIR
	echo "DONE"
	exit
	#chroot $MOUNTDIR /bin/bash &
fi

if [ $option == "stop" ]; then

	  echo "Stop"
	  umount_dir $MOUNTDIR
	  echo "DONE"
fi

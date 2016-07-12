
#1 paramter image path
#2 patamter ssh status
#3 patametr minisip status

imagePath=$1
sshStatus=$2
miniSip=$3

function mount_dir {
 
#      /system/xbin/busybox mount -t proc proc $1/proc
#      /system/xbin/busybox mount -o bind /dev $1/dev
#      /system/xbin/busybox mount -t sysfs sysfs $1/sys
#      /system/xbin/busybox mount -t devpts devpts $1/dev/pts

	for f in dev dev/pts proc sys ; do mount -o bind /$f $1/$f ; done
}

function umount_dir {

	echo "Unmount partitions: "

	for i in '.*' '*'
		do
			parts=$(cat /proc/mounts | awk '{print $2}' | grep "^$1/$i$" | sort -r)
			for p in $parts
				do
					pp=$(echo $p | sed  "s|$1/*|/|g")
					echo "$pp ..."
					umount $p
					[ $? -eq 0 ] && echo "done" || echo "fail"
			done
	done

}
  
function start_services {
 
 
 #	  echo "Start SSH server? [y/N]"
  #        read  startssh
   	startssh=$sshStatus
   	if [[ $startssh == "y" || $startssh == "Y" || $startssh == "yes" || $startssh == "Yes" ]]
 		then
		 
		 echo "start ssh server" 
                 chroot $1 /bin/bash -x <<'EOF'
                  service ssh start
EOF
          fi  
	
	#  echo "Start VNC server? [y/N]"
	#  read  startvnc
	#  if [[ $startvnc == "y" || $startvnc == "Y" || $startvnc == "yes" || $startvnc == "Yes" ]]
 	#then
#		echo "Enter resolution " 
#		read $res 
#		chroot $1 /bin/bash -x <<'EOF'
#		vncserver :0 -geometry $res
#EOF
#	fi

#	  echo "Start miniSipServer? [y/N]"
#	  read  startsip
	startsip=$miniSip
	if [[ $startsip == "y" || $startsip == "Y" || $startsip == "yes" || $startsip == "Yes" ]]
		then
		#start miniSipServer
		chroot $1 /bin/bash -x <<'EOF'
		/opt/sipserver/msscli&
EOF
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

  echo "Start"
  export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$PATH

  /system/xbin/busybox mount -o remount,rw,exec,dev,suid /
 # echo "Default configuration [y/N]?"
 # read response
  response=yes
  case $response in
      [yY][eE][sS]|[yY]) 
        
          #create folder for mount
          export MOUNTDIR=/mnt/linux
          mkdir -p $MOUNTDIR
          
          if [ -d "$MOUNTDIR" ]; then
                  echo "Mount folder on $MOUNTDIR"
          else 
                  echo "Folder create error"
		  exit
          fi
          
          #mount image
          /system/xbin/busybox mount $imagePath $MOUNTDIR
  
          #mount important dir
          mount_dir $MOUNTDIR
          
          #check if system start first time
	  check_first_time $MOUNTDIR	
 
	  start_services $MOUNTDIR

	  chroot $MOUNTDIR /bin/bash
	
	  umount_dir $MOUNTDIR
 
       ;;
    *)
          #create folder for mount
	  echo "Enter path for mount folder"
	  read  mountfolder
	  mkdir -p $mountfolder

	  if [ -d "$mountfolder" ]; then
		  export MOUNTDIR=$mountfolder
	  	  echo "Mount folder on $MOUNTDIR"
          else
		  echo "Folder create error"
		  exit 
	  fi
	
	echo "Enter image path"
	  read imagepath
	  while [ ! -f "$imagepath" ] 
	  do
		  echo "File not found"
		  read -r "Enter image path" imagepath	
       	  done
	  
	  #mount image 
	  /system/xbin/busybox mount $imagepath $MOUNTDIR

	  #mount important dir
	  mount_dir $MOUNTDIR

          #check if system start first time
	  check_first_time $MOUNTDIR

	  start_services $MOUNTDIR
	  chroot $MOUNTDIR /bin/bash

	  umount_dir $MOUNTDIR
       ;;
 esac

 


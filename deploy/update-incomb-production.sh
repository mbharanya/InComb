#!/bin/bash
WEBAPPDIR=/var/lib/tomcat7/webapps
WEBAPPNAME=ROOT
BACKUPDIR=/root/backup/$(date "+%d.%m.%y_%H:%M:%S")
MYSQL_USER=root
MYSQL_PW=
MYSQL_TABLE=incomb
# Make sure only root can run our script
if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

if [ -z "$1" ]
  then
    echo "No war file supplied; First argument must be the .war file"
    exit 1
fi

if [ -z "$2" ]
  then
    echo "No incomb_config.json file supplied; Second argument must be the conf file"
    exit 1
fi

mkdir -p $BACKUPDIR

#Stop tomcat
echo "stopping tomcat service..."
service tomcat7 stop
echo "done!"

#Dump db
echo "dumping db..."
mysqldump -u$MYSQL_USER -p$MYSQL_PW incomb  > $BACKUPDIR/complete.sql
echo "done!"

#Backup old version
echo "Backing up old version ..."
tar -czf $BACKUPDIR/root.tar.gz -C $WEBAPPDIR/$WEBAPPNAME .
mv $WEBAPPDIR/$WEBAPPNAME/WEB-INF/indexes $BACKUPDIR/indexes
rm -rf $WEBAPPDIR/$WEBAPPNAME
echo "done!"

# war file
echo "unzipping war file..."
unzip -q $1 -d $WEBAPPDIR/ROOT
echo "done!"

echo "loading server incomb_config.json"
cp $2 $WEBAPPDIR/$WEBAPPNAME/WEB-INF/conf/
echo "done!"

echo "reloading indexes..."
mv  $BACKUPDIR/indexes $WEBAPPDIR/$WEBAPPNAME/WEB-INF/indexes
echo "done!"

echo "setting owner and permissions"
chown tomcat7:tomcat7 -R $WEBAPPDIR/$WEBAPPNAME
chmod 770 -R $WEBAPPDIR/$WEBAPPNAME
echo "done!"

echo "restarting tomcat"
service tomcat7 start
echo "done!"

echo -e "\e[7mRemember to update incomb_config.json as it is not included in the war file\e[0m"
echo -e "\e[7mRemember to also set the log level to warn\e[0m"

#!/bin/bash

WEBAPPDIR=/var/lib/tomcat7/webapps
WEBAPPNAME=ROOT
BACKUPDIR=/root/backup/$(date "+%d.%m.%y_%H:%M:%S")
MYSQL_USER=root
MYSQL_TABLE=incomb

mkdir -p $BACKUPDIR

#Dump db
echo "dumping db..."
mysqldump -u$MYSQL_USER -p$MYSQL_PW incomb  > $BACKUPDIR/complete.sql
echo "done!"

#Backup old version
echo "Backing up files ..."
tar -czf $BACKUPDIR/root.tar.gz -C $WEBAPPDIR/$WEBAPPNAME .

zip -r $BACKUPDIR.zip $BACKUPDIR
rm -r $BACKUPDIR
echo "done! Download with scp root@incomb.com:$BACKUPDIR.zip ."


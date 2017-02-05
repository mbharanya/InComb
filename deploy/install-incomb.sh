#!/bin/bash
WEBAPPDIR=/var/lib/tomcat7/webapps
WEBAPPNAME=ROOT
MYSQL_USER=root
MYSQL_PW=
MYSQL_TABLE=incomb

#Stop tomcat
echo "stopping tomcat service..."
service tomcat7 stop
echo "done!"

# war file
echo "unzipping war file..."
unzip -q $1 -d $WEBAPPDIR/ROOT
echo "done!"

echo "loading schema sql..."
cat $WEBAPPDIR/$WEBAPPNAME/WEB-INF/sql/schema.sql | mysql -u$MYSQL_USER -p$MYSQL_PW $MYSQL_TABLE
echo "done!"

echo "loading data sql..."
cat $WEBAPPDIR/$WEBAPPNAME/WEB-INF/sql/data.sql | mysql -u$MYSQL_USER -p$MYSQL_PW $MYSQL_TABLE
echo "done!"

echo "setting owner and permissions"
chown tomcat7:tomcat7 -R $WEBAPPDIR/$WEBAPPNAME
chmod 770 -R $WEBAPPDIR/$WEBAPPNAME
echo "done!"

echo "restarting tomcat"
service tomcat7 start
echo "done!"

#!/bin/sh

echo "The application will start soon -- Spring-Boot ..." 
exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar -Dspring.profiles.active=prod "testpro-0.0.1-SNAPSHOT.war"

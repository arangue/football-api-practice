#!/bin/sh

CLASSPATH="/app/target/dependency/*:/app/target/classes"

java -cp $CLASSPATH \
-Dactivejdbc.log \
-Denv.connections.file=/app/src/main/resources/docker-database.properties \
-DACTIVE_ENV=development \
com.football.api.App
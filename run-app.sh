#!/usr/bin/env bash


echo "Running as user: $(whoami)"

#export JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
export JAVA_APP_JAR="/home/runner/quarkus-app/quarkus-run.jar"
export QUARKUS_HTTP_PORT=${PORT:-8080}

echo "Java executable path : $(command -v java)"
ls -l "$(command -v java)"

export COMMAND_TO_RUN="java -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Dquarkus.http.port=${QUARKUS_HTTP_PORT} -jar ${JAVA_APP_JAR}"
echo "Running: ${COMMAND_TO_RUN}"
$COMMAND_TO_RUN
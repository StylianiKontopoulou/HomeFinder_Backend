# Use the official Maven image to build the application
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM quay.io/wildfly/wildfly:latest

ARG WILDFLY_USER
ARG WILDFLY_PASS
ARG DB_NAME
ARG DB_USER
ARG DB_PASS
ARG DB_URI

# Appserver
ENV WILDFLY_USER ${WILDFLY_USER}
ENV WILDFLY_PASS ${WILDFLY_PASS}

# Database
ENV DB_NAME ${DB_NAME}
ENV DB_USER ${DB_USER}
ENV DB_PASS ${DB_PASS}
ENV DB_URI ${DB_URI}

ENV MYSQL_VERSION 8.0.30
ENV JBOSS_CLI /opt/jboss/wildfly/bin/jboss-cli.sh
ENV DEPLOYMENT_DIR /opt/jboss/wildfly/standalone/deployments/
#ENV JAVA_OPTS

# Setup WildFly Admin Console
RUN echo "=> Adding WildFly administrator"
RUN $JBOSS_HOME/bin/add-user.sh -u $WILDFLY_USER -p $WILDFLY_PASS --silent

# Configure WildFly server
RUN echo "=> Starting WildFly server" && \
    bash -c '$JBOSS_HOME/bin/standalone.sh -c standalone-microprofile.xml &' && \
    echo "=> Waiting for the server to boot" && \
    bash -c 'until `$JBOSS_CLI -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do echo `$JBOSS_CLI -c ":read-attribute(name=server-state)" 2> /dev/null`; sleep 1; done' && \
    echo "=> Downloading MySQL driver" && \
    curl --location --insecure --output /tmp/mysql-connector-java-${MYSQL_VERSION}.jar --url http://search.maven.org/remotecontent?filepath=mysql/mysql-connector-java/${MYSQL_VERSION}/mysql-connector-java-${MYSQL_VERSION}.jar && \
    echo "=> Adding MySQL module" && \
    $JBOSS_CLI --connect --command="module add --name=com.mysql --resources=/tmp/mysql-connector-java-${MYSQL_VERSION}.jar --dependencies=javax.api,javax.transaction.api" && \
    echo "=> Adding MySQL driver" && \
    $JBOSS_CLI --connect --command="/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql)" && \
    echo "=> Creating a new datasource" && \
    $JBOSS_CLI --connect --command="data-source add \
    --name=${DB_NAME}DS \
    --jndi-name=java:/${DB_NAME} \
    --user-name=${DB_USER} \
    --password=${DB_PASS} \
    --driver-name=mysql \
    --connection-url=jdbc:mysql://${DB_URI}/${DB_NAME} \
    --use-ccm=false \
    --max-pool-size=25 \
    --blocking-timeout-wait-millis=5000 \
    --enabled=true" && \
    echo "=> Shutting down WildFly and Cleaning up" && \
    $JBOSS_CLI --connect --command=":shutdown" && \
    rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ $JBOSS_HOME/standalone/log/* && \
    rm -f /tmp/*.jar

# Copy the built WAR file from the Maven build stage
COPY --from=build /app/target/*.war $DEPLOYMENT_DIR

# Expose http and admin ports
EXPOSE 8080 9990

# Boot WildFly in the standalone mode and bind to all interfaces
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-microprofile.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

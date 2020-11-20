ARG CODENARC_VERSION=2.0.0
ARG JAVA_VERSION=8
ARG GROOVY_VERSION=2.5.12
ARG SLF4J_VERSION=1.7.25
ARG GMETRICS_VERSION=1.1

FROM buildpack-deps:stable-curl AS downloads

ARG GROOVY_VERSION
ARG CODENARC_VERSION
ARG SLF4J_VERSION
ARG GMETRICS_VERSION

RUN mkdir -p /downloads && cd /downloads \
 && curl -fsSLO https://search.maven.org/remotecontent?filepath=org/codenarc/CodeNarc/$CODENARC_VERSION/CodeNarc-$CODENARC_VERSION.jar \
 && curl -fsSLO https://search.maven.org/remotecontent?filepath=org/codehaus/groovy/groovy/$GROOVY_VERSION/groovy-$GROOVY_VERSION.jar \
 && curl -fsSLO https://search.maven.org/remotecontent?filepath=org/codehaus/groovy/groovy-xml/$GROOVY_VERSION/groovy-xml-$GROOVY_VERSION.jar \
 && curl -fsSLO https://search.maven.org/remotecontent?filepath=org/codehaus/groovy/groovy-json/$GROOVY_VERSION/groovy-json-$GROOVY_VERSION.jar \
 && curl -fsSLO https://search.maven.org/remotecontent?filepath=org/codehaus/groovy/groovy-ant/$GROOVY_VERSION/groovy-ant-$GROOVY_VERSION.jar \
 && curl -fsSLO https://search.maven.org/remotecontent?filepath=org/codehaus/groovy/groovy-templates/$GROOVY_VERSION/groovy-templates-$GROOVY_VERSION.jar \
 && curl -fsSLO https://search.maven.org/remotecontent?filepath=org/slf4j/slf4j-api/$SLF4J_VERSION/slf4j-api-$SLF4J_VERSION.jar \
 && curl -fsSLO https://search.maven.org/remotecontent?filepath=org/slf4j/slf4j-simple/$SLF4J_VERSION/slf4j-simple-$SLF4J_VERSION.jar \
 && curl -fsSLO https://search.maven.org/remotecontent?filepath=org/gmetrics/GMetrics/$GMETRICS_VERSION/GMetrics-$GMETRICS_VERSION.jar

FROM openjdk:$JAVA_VERSION

ARG GROOVY_VERSION
ARG CODENARC_VERSION
ARG SLF4J_VERSION
ARG GMETRICS_VERSION

ENV GROOVY_VERSION=$GROOVY_VERSION
ENV CODENARC_VERSION=$CODENARC_VERSION
ENV SLF4J_VERSION=$SLF4J_VERSION
ENV GMETRICS_VERSION=$GMETRICS_VERSION

COPY --from=downloads /downloads/*.jar /opt/

RUN echo '#!/bin/bash' > /usr/local/bin/codenarc \
 && echo "$JAVA_HOME/bin/java -cp '/opt/CodeNarc-$CODENARC_VERSION.jar:/opt/groovy-$GROOVY_VERSION.jar:/opt/groovy-xml-$GROOVY_VERSION.jar:/opt/groovy-json-$GROOVY_VERSION.jar:/opt/groovy-ant-$GROOVY_VERSION.jar:/opt/groovy-templates-$GROOVY_VERSION.jar:/opt/slf4j-api-$SLF4J_VERSION.jar:/opt/slf4j-simple-$SLF4J_VERSION.jar:/opt/GMetrics-$GMETRICS_VERSION.jar'"' org.codenarc.CodeNarc "$@"' >> /usr/local/bin/codenarc \
 && chmod 755 /usr/local/bin/codenarc

WORKDIR /code

CMD /usr/local/bin/codenarc
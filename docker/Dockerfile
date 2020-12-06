FROM openjdk:8-jre-slim AS gradle

ARG CODENARC_VERSION=2.0.0
ARG GROOVY_VERSION=2.5.12
ARG GRADLE_VERSION=6.5.1

RUN apt-get update && apt-get install -y unzip curl
WORKDIR /gradle
RUN curl -L https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip -o gradle-$GRADLE_VERSION-bin.zip
RUN unzip gradle-$GRADLE_VERSION-bin.zip
ENV GRADLE_HOME=/gradle/gradle-$GRADLE_VERSION
ENV PATH=$PATH:$GRADLE_HOME/bin

COPY . /gradle
RUN sed -e "s/\${codenarc.version}/$CODENARC_VERSION/" -e "s/\${groovy.version}/$GROOVY_VERSION/" build.gradle.template > build.gradle

RUN gradle --quiet --no-daemon shadowJar

FROM openjdk:8-jre-slim

WORKDIR /lib
COPY --from=gradle /gradle/build/libs/codenarc-all.jar /lib/codenarc-all.jar
RUN chmod 755 /lib/codenarc-all.jar

WORKDIR /ws

ENTRYPOINT ["java", "-jar", "/lib/codenarc-all.jar"]


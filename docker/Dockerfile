FROM openjdk:8-jdk

ARG CODENARC_VERSION=2.0.0
ARG GROOVY_VERSION=2.5.12
ARG GRADLE_VERSION=6.5.1

RUN apt-get update && apt-get install -y unzip
WORKDIR /gradle
RUN curl -L https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip -o gradle-$GRADLE_VERSION-bin.zip
RUN unzip gradle-$GRADLE_VERSION-bin.zip
ENV GRADLE_HOME=/gradle/gradle-$GRADLE_VERSION
ENV PATH=$PATH:$GRADLE_HOME/bin

COPY . /gradle
RUN sed -e "s/\${codenarc.version}/$CODENARC_VERSION/" -e "s/\${groovy.version}/$GROOVY_VERSION/" build.gradle.template > build.gradle

RUN gradle --quiet --no-daemon shadowJar

WORKDIR /work
COPY . /work

ENTRYPOINT ["java", "-jar", "/gradle/build/libs/codenarc-all.jar"]


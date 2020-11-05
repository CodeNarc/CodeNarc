FROM openjdk:8-jdk-slim AS build

WORKDIR /code

COPY . /code

RUN ./gradlew --quiet --no-daemon shadowJar

RUN echo '#!/bin/bash' > codenarc \
 && echo '/usr/local/openjdk-8/bin/java -jar /opt/codenarc.jar "$@"' >> codenarc \
 && chmod 755 codenarc

FROM openjdk:8-jre-slim

COPY --from=build /code/build/libs/CodeNarc-*-all.jar /opt/codenarc.jar
COPY --from=build /code/codenarc /usr/local/bin/codenarc

WORKDIR /code

CMD /usr/local/bin/codenarc
# CodeNarc  (http://codenarc.org/)

[![Maven Central](https://img.shields.io/maven-central/v/org.codenarc/CodeNarc.svg)]()

**CodeNarc** is a static analysis tool for Groovy source code, enabling monitoring and enforcement of many coding standards and best practices. CodeNarc applies a set of Rules (predefined and/or custom) that are applied to each Groovy file, and generates an HTML or XML report of the results, including a list of rules violated for each source file, and a count of the number of violations per package and for the whole project.

CodeNarc is similar to popular static analysis tools such as PMD or Checkstyle. Unlike those tools which analyze Java code, CodeNarc analyzes Groovy code.

DEPENDENCIES

CodeNarc requires
 - Groovy version 2.1 or later
 - The [SLF4J](https://www.slf4j.org/) api/implementation jars, accessible on the CLASSPATH.
 - The [GMetrics](http://gmetrics.org) jar, version 1.0 or later -- optional if using some of the size/complexity rules.

AVAILABLE FROM MAVEN CENTRAL REPOSITORY

For projects built using Gradle or Maven, **CodeNarc** is available from the Maven Central Repository.
  - groupId = org.codenarc
  - artifactId = CodeNarc

## Development

When contributing to CodeNarc, run the following command to test on your local machine.

```bash
./gradlew check
```

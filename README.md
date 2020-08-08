<!-- markdownlint-disable MD041 -->
[![CodeNarc Logo](https://codenarc.github.io/CodeNarc/images/codenarc-logo.png)](https://codenarc.org/)

[![Maven Central](https://img.shields.io/maven-central/v/org.codenarc/CodeNarc.svg)](https://mvnrepository.com/artifact/org.codenarc/CodeNarc)
[![Build Status](https://travis-ci.org/CodeNarc/CodeNarc.svg?branch=master)](https://travis-ci.org/CodeNarc/CodeNarc)
[![GitHub contributors](https://img.shields.io/github/contributors/CodeNarc/CodeNarc.svg)](https://gitHub.com/CodeNarc/CodeNarc/graphs/contributors/)
[![GitHub stars](https://img.shields.io/github/stars/CodeNarc/CodeNarc?label=Star&maxAge=2592000)](https://GitHub.com/CodeNarc/CodeNarc/stargazers/)
[![License](https://img.shields.io/github/license/CodeNarc/CodeNarc.svg)](https://github.com/CodeNarc/CodeNarc/blob/master/LICENSE.txt)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

**CodeNarc** is a static analysis tool for **Groovy** source code, enabling monitoring and enforcement of many coding standards and best practices. CodeNarc applies a set of Rules (predefined and/or custom) that are applied to each Groovy file, and generates an HTML or XML report of the results, including a list of rules violated for each source file, and a count of the number of violations per package and for the whole project.

CodeNarc is similar to popular static analysis tools such as PMD or Checkstyle. Unlike those tools which analyze Java code, CodeNarc analyzes Groovy code.

## DOCUMENTATION

All documentation is available on [CodeNarc website](https://codenarc.github.io/CodeNarc/)

List of tools and IDEs integrating CodeNarc out of the box is available [here](https://codenarc.github.io/CodeNarc/codenarc-other-tools-frameworks.html)

## DEPENDENCIES

CodeNarc requires

- Groovy version 2.4 or later
- The [SLF4J](https://www.slf4j.org/) api/implementation jars, accessible on the CLASSPATH.
- The [GMetrics](http://gmetrics.org) jar, version 1.0 or later -- optional if using some of the size/complexity rules.

## AVAILABLE FROM MAVEN CENTRAL REPOSITORY

For projects built using Gradle or Maven, **CodeNarc** is available from the [Maven Central Repository]((https://mvnrepository.com/artifact/org.codenarc/CodeNarc)).

- groupId = org.codenarc
- artifactId = CodeNarc

## DEVELOPMENT

When contributing to CodeNarc, run the following command to test on your local machine.

```bash
./gradlew check
```

It is recommended to use Java 8-11.

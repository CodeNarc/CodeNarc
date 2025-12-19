---
layout: default
title: CodeNarc - Integration with Other Tools / Frameworks
---

# CodeNarc - Integration with Other Tools / Frameworks

## IDEs

  * [IntelliJ IDEA](http://www.jetbrains.com/idea/) - See the [IDEA CodeNarc Plugin](http://plugins.jetbrains.com/plugin/?idea&id=5925).

  * [Visual Studio Code](https://code.visualstudio.com/) - See the [VsCode Groovy Lint extension](https://marketplace.visualstudio.com/items?itemName=NicolasVuillamy.vscode-groovy-lint).

## Docker

See [CodeNarc - Docker](./codenarc-docker.html).

## Application Frameworks

  * [Grails](https://grails.apache.org/) - See the [Grails CodeNarc Plugin](https://github.com/chrismair/GrailsCodeNarcPlugin) for Grails 2.x.
    Use the [Gradle CodeNarc Plugin](http://gradle.org/docs/current/userguide/codenarc_plugin.html) for Grails 3.x and later.
    Also see [Static code analysis in a Grails app with CodeNarc](http://guides.grails.org/grails-codenarc/guide/index.html).

## Build and Code Quality Tools

  * [Gradle](http://www.gradle.org/) build system - See the [Gradle CodeNarc Plugin](http://gradle.org/docs/current/userguide/codenarc_plugin.html).
      * See [#630](https://github.com/CodeNarc/CodeNarc/issues/630) for info on controlling the version of Groovy that CodeNarc uses for the Gradle plugin.
        e.g. 
        ```groovy
            dependencies {
                codenarc('org.codenarc:CodeNarc:3.2.0')
                codenarc('org.codehaus.groovy:groovy-all:3.0.9')
            }
        ```

  * [Maven](http://maven.apache.org/) - See the [Maven CodeNarc Plugin](https://github.com/gleclaire/codenarc-maven-plugin)

  * [Jenkins](https://jenkins.io/) - The
    [Jenkins Warnings NG Plugin](https://github.com/jenkinsci/warnings-ng-plugin#jenkins-warnings-next-generation-plugin)
    includes reporting and trending of CodeNarc violations.

  * [SonarQube](https://www.sonarqube.org/) - The [Sonar Groovy Plugin](https://redirect.sonarsource.com/plugins/groovy.html)
    uses CodeNarc for its static analysis of Groovy source code.

  * [NPM Groovy Lint](https://github.com/nvuillam/npm-groovy-lint) - NPM CodeNarc wrapper to easily lint, format and fix Groovy files and Jenkinsfile via command line. Uses CodeNarc for its static analysis of Groovy source code.

  * [GitLab Code Quality](https://docs.gitlab.com/ee/ci/testing/code_quality.html) -
    Using the "gitlab" report formatter, you can export the linting results to the
    running GitLab installation, to support in code reviews and merge requests.


## Customizing ClassLoader for Loading Rule Scripts

  **Grails** (and possibly other tools integrating with **CodeNarc**) can benefit from using the current thread context
  `ClassLoader` for loading rule script files, rather than the default base `ClassLoader`. Setting the
  *"codenarc.useCurrentThreadContextClassLoader"* system property to "true" uses the current thread context
  `ClassLoader` for loading rule script files. That enables **Grails** to load rule script files from within
  the **Grails** project, and allows those rule scripts to reference local classes. The
  [Grails CodeNarc Plugin](https://github.com/chrismair/GrailsCodeNarcPlugin)  automatically sets that system property.

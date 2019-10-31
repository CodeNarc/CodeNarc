---
layout: default
title: CodeNarc - Run as a Test
---
# CodeNarc - Run as an Automated Test

**CodeNarc** can be run as part of an automated test suite, for instance using **JUnit** or **TestNG**.
This approach uses the `org.codenarc.ant.CodeNarcTask` class and Groovy's built-in support for **Apache Ant**.

**NOTE:** This approach may not play well with having **CodeNarc** also configured to run as a 
separate task aspart of your "regular" build, especially if you have a classpath defined for 
your build which is different that your runtime classpath for running your tests (which is likely).

## Automated Test That Runs CodeNarc

This is an example **JUnit** (4.x) test that runs **CodeNarc**.

```groovy
    private static final GROOVY_FILES = '**/*.groovy'
    private static final RULESET_FILES = [
            'rulesets/basic.xml',
            'rulesets/imports.xml'].join(',')

    @Test
    void testRunCodeNarc() {
        def ant = new AntBuilder()

        ant.taskdef(name:'codenarc', classname:'org.codenarc.ant.CodeNarcTask')

        ant.codenarc(ruleSetFiles:RULESET_FILES,
           maxPriority1Violations:0, maxPriority2Violations:0) {

           fileset(dir:'src/main/groovy') {
               include(name:GROOVY_FILES)
           }
           fileset(dir:'src/test/groovy') {
               include(name:GROOVY_FILES)
           }

           report(type:'ide')
        }
    }
```

Things to note:

  * This approach uses the `AntBuilder` class provided with Groovy.

  * The **CodeNarc** jar must be on the classpath. Any external resources that you reference must also
    be on the classpath, including any custom <RuleSet> files.

  * The `<maxPriority1Violations>` and `<maxPriority2Violations>` properties are configured to fail the test
    if any priority 1 or 2 violations are found.

  * An `<ide>` report is configured. This will to write (only) to *standard out*, and will include hyperlinks
    to the lines within the source code that contained the violations . The report shows the total number
    of violations (at each priority) and also list the violations by file.


See the [CodeNarc Ant Task](./codenarc-ant-task.html) for more information on configuring the Ant task.


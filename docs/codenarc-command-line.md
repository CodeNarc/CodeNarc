---
layout: default
title: CodeNarc Command Line
---

# CodeNarc - Run From Command-Line

**CodeNarc** can be run from the command-line as a Java application. The `org.codenarc.CodeNarc`
class provides the application entry-point.

## CodeNarc Command-Line Parameters

Usage: `java org.codenarc.CodeNarc [OPTIONS]`
where OPTIONS are zero or more command-line parameters of the form "`-NAME[=VALUE]`"

All command-line parameters are optional. If no parameters are supplied, **CodeNarc** runs from the
current directory with reasonable defaults, as described below.

| Parameter                   | Description                  | Example                          |
|---------------------------------|----------------------------------------------------------------------+--------------------------------------+
| *-basedir=DIR*                  | The base (root) directory for the source code to be analyzed. Defaults to the current directory (".").       | -basedir=src/main/groovy |
| *-includes=PATTERNS*            | The comma-separated list of Ant-style file patterns specifying files that must be included. Defaults to "**/*.groovy". | -includes=**/*.gr |
| *-excludes=PATTERNS*            | The comma-separated list of Ant-style file patterns specifying files that must be excluded. No files are excluded when omitted.     |  -excludes=**/templates/**,**/*Test.* |
| *-rulesetfiles=FILENAMES*       | The path to the Groovy or XML RuleSet definition files. This can be a single file path, or multiple paths separated by commas. By default, the paths specified are relative to the classpath. But these paths may be optionally prefixed by any of the valid java.net.URL prefixes, such as "file:" (to load from a relative or absolute path on the filesystem), or "http:". If it is a URL, its path may be optionally URL-encoded. That can be useful if the path contains any problematic characters, such as comma (',') or hash ('#'). For instance: "file:src/test/resources/RuleSet-,#.txt" can be encoded as: "file:src%2Ftest%2Fresources%2FRuleSet-%2C%23.txt" See `URLEncoder#encode(java.lang.String, java.lang.String).` Defaults to "rulesets/basic.xml". | -rulesetfiles=rulesets/imports.xml,rulesets/naming.xml |
| *-report=REPORT-TYPE[:FILENAME]*| The definition of the report to produce. The option value is of the form `TYPE[:FILENAME]`, where **<TYPE**> is one of the predefined type names: "html", "xml", "text", "console" or else the fully-qualified class name of a class (accessible on the classpath) that implements the `org.codenarc.report.ReportWriter` interface. And `FILENAME` is the filename (with optional path) of the output report filename. If the report filename is omitted, the default filename for the report type is used ("CodeNarcReport.html" for "html" and "CodeNarcXmlReport.xml" for "xml"). If no report option is specified, default to a single "html" report with the default filename. | -report=html <br/>-report=html:MyProject.html <br/>-report=xml <br/>-report=xml:MyXmlReport.xml <br/>-report=org.codenarc.report.HtmlReportWriter |
| *-maxPriority1Violations=MAX*   | The maximum number of priority 1 violations allowed (int).           | -maxPriority1Violations=0            |
| *-maxPriority2Violations=MAX*   | The maximum number of priority 2 violations allowed (int).           | -maxPriority2Violations=0            |
| *-maxPriority3Violations=MAX*   | The maximum number of priority 3 violations allowed (int).           | -maxPriority3Violations=0            |
| *-title=REPORT TITLE*           | The title for this analysis; used in the output report(s), if supported by the report type(s). Optional. | -title="My Project" |
| *-help*                         | Display the command-line help. If present, this must be the only command-line parameter. | -help                                |

## Executing CodeNarc from the Command-Line

Make sure that the following are included your CLASSPATH:

  1. The Groovy jar

  2. The CodeNarc jar

  3. The **SLF4J** api/implementation jars

  4. The directories containing (or relative to) **CodeNarc** config files such as "codenarc.properties"
        or ruleset files.

The CodeNarc command-line application sets an exit status of zero (0) if the command successfully executes, and
an exit status of one (1) if an error occurs executing CodeNarc, or if an invalid command-line option is specified.

Here is an example BAT file for running **CodeNarc** on Windows.

```
    @set GROOVY_JAR="%GROOVY_HOME%/embeddable/groovy-all-1.5.6.jar"
    @java -classpath %GROOVY_JAR%;lib/CodeNarc-0.5.jar;lib/slf4j-api-1.7.25.jar;lib org.codenarc.CodeNarc %*
```

Here is the equivalent Linux command.

```
    export GROOVY_JAR="$GROOVY_HOME/embeddable/groovy-all-1.5.6.jar"
    java -classpath $GROOVY_JAR:lib/CodeNarc-0.5.jar:lib/slf4j-api-1.7.25.jar:lib org.codenarc.CodeNarc $*
```
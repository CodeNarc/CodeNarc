---
layout: default
title: CodeNarc Ant Task
---
# CodeNarc - Ant Task

## Description

The **CodeNarc** Ant Task is implemented by the `org.codenarc.ant.CodeNarcTask` class.

## Parameters

| Attribute                | Description                                                    | Required               |
|--------------------------|----------------------------------------------------------------|------------------------|
| *ruleSetFiles*           | The paths to the Groovy or XML RuleSet definition files. This can be a single file path, or  multiple paths separated by commas. By default, the paths specified are relative to the classpath. But each path may be optionally prefixed by any of the valid java.net.URL prefixes, such as "file:" (to load from a relative or absolute filesystem path), or "http:". If it is a URL, its path may be optionally URL-encoded. That can be useful if the path contains any problematic characters, such as comma (',') or hash ('#'). For instance: "file:src/test/resources/RuleSet-,#.txt" can be encoded as: "file:src%2Ftest%2Fresources%2FRuleSet-%2C%23.txt" See `URLEncoder#encode(java.lang.String, java.lang.String).`   | Yes |
| *maxPriority1Violations* | The maximum number of priority 1 violations allowed before failing the build (throwing a `BuildException`).               | No |
| *maxPriority2Violations* | The maximum number of priority 2 violations allowed before failing the build (throwing a `BuildException`).               | No |
| *maxPriority3Violations* | The maximum number of priority 3 violations allowed before failing the build (throwing a `BuildException`).               | No |
| *excludeBaseline*        | The path to a *baseline violations* report (report type "baseline") If set, then all violations specified within that report are excluded (filtered) from the current **CodeNarc** run. If null/empty, then do nothing. See [Baseline Report](./codenarc-BaselineXmlReportWriter.html).    | No |
| *classpathRef*           | The reference to a path element which is to be used as classpath when compiling analysed sources (useful with [Enhanced Classpath Rules](./codenarc-enhanced-classpath-rules.html)). | No |
| *failOnError*            | Boolean that indicates whether to terminate and fail the task (throwing a `BuildException`) if any errors occur parsing source files (<true>), or just log  the errors (<false>). It defaults to <false>.                                        | No |


## Report Nested Element

The **report** nested element defines the format and output file for the analysis report.

| Attribute            | Description                                                    | Required               |
|----------------------|----------------------------------------------------------------|------------------------|
| *type*               | The type of the output report. Must be either one of the predefined type names: "html", "xml", "text", "console", "ide" or else the fully-qualified class name of a class (accessible on the classpath) that implements the `org.codenarc.report.ReportWriter` interface.  | Yes |

Notes:

  * The "ide" report *type* creates an `IdeTextReportWriter` and sets its *writeToStandardOut* property to `true`.
    The generated report includes IDE-compatible (Eclipse, Idea) hyperlinks to source code for violations.

  * The "console" report *type* creates a `TextReportWriter` and sets its *writeToStandardOut* property to `true`.


### Option Nested Element

The `<option>` element is a child of the `<report>` element and defines a
report-specific option for a report. You specify the option *name* and *value* as attributes
within the `<option>` element. See the *Example* below.


## Fileset Nested Element

At least one `<fileset>` nested element is required, and is used to specify the source files that
**CodeNarc** should analyze. This is the standard Ant `<FileSet>`, and is quite powerful and flexible.
See the [Apache Ant Manual](http://ant.apache.org/manual/index.html) for more information on `<FileSets>`.


## Classpath Nested Element

An optional **classpath** nested element can be utilized to specify contents of the classpath to be used when
**CodeNarc** compiles classes it analyses. Having control over that classpath is essential when using
[Enhanced Classpath Rules](./codenarc-enhanced-classpath-rules.html). It is a standard
[Ant Path element](https://ant.apache.org/manual/using.html#path).


## Example

Here is an example Ant XML build file.

```
    <taskdef name="codenarc" classname="org.codenarc.ant.CodeNarcTask"/>
    <target name="runCodeNarc">
        <codenarc
                ruleSetFiles="rulesets/basic.xml,rulesets/exceptions.xml,rulesets/imports.xml"
                maxPriority1Violations="0">
    
            <report type="html">
                <option name="outputFile" value="reports/CodeNarcAntReport.html" />
                <option name="title" value="My Sample Code" />
            </report>
    
            <fileset dir="src">
                <include name="**/*.groovy"/>
            </fileset>
        </codenarc>
    </target>
```

Things to note:

  * Three `<RuleSet>` files are specified (`<basic>`, `<exceptions>` and `<imports>`).

  * The `<fileset>` specifies that all ".groovy" files are analyzed.

  * Remember that you need the **SLF4J** api/implementation jars on the classpath.


## Logging and Troubleshooting

Be sure to have a **SLF4J** api/implementation jars on the classpath so that any errors are logged.


---
layout: default
title: CodeNarc TextReportWriter and IdeTextReportWriter
---

# TextReportWriter and IdeTextReportWriter

## Description

The `org.codenarc.report.TextReportWriter` class (type="text") produces a simple text report of the
**CodeNarc** results. See a [Sample Report](./SampleCodeNarcTextReport.txt).

The `org.codenarc.report.IdeTextReportWriter` class (type="ide") also produces a text report of the
**CodeNarc** results, and includes IDE-compatible (Eclipse, Idea) hyperlinks to source code for violations.


## Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option
for a report.

The `TextReportWriter` and `IdeTextReportWriter` classes
supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| maxPriority             | The maximum priority level for violations in the report. For instance, setting *maxPriority* to 2 will result in the report containing only priority 1 and 2 violations (and omitting violations with priority 3). | 3 |
| outputFile              | The path and filename for the output report file.              | "CodeNarcReport.txt"  |
| title                   | The title for the output report.                               |                        |
| writeToStandardOut      | Set to `true` to write out the report to *stdout* (`System.out`) instead of writing to a file. |  `false` |


## Examples

Here is an example Ant XML build file illustrating configuration of
`org.codenarc.report.TextReportWriter`. Note that the report **type** is specified as **"text"**.

```
    <taskdef name="codenarc" classname="org.codenarc.ant.CodeNarcTask"/>
    <target name="runCodeNarc">
        <codenarc
                ruleSetFiles="rulesets/basic.xml,rulesets/exceptions.xml,rulesets/imports.xml"
                maxPriority1Violations="0">
    
            <report type="text">
                <option name="outputFile" value="reports/CodeNarcReport.txt" />
                <option name="title" value="My Sample Code" />
            </report>
    
            <fileset dir="src">
                <include name="**/*.groovy"/>
            </fileset>
        </codenarc>
    </target>
```

  Here is an example Ant XML build file illustrating configuration of
  `org.codenarc.report.IdeTextReportWriter`. Note that the report **type** is specified as **"ide"**.
  The **"ide"** report type will automatically write the report to *stdout*.

```
    <taskdef name="codenarc" classname="org.codenarc.ant.CodeNarcTask"/>
    <target name="runCodeNarc">
        <codenarc
                ruleSetFiles="rulesets/basic.xml,rulesets/exceptions.xml,rulesets/imports.xml"
                maxPriority1Violations="0">
    
            <report type="ide">
                <option name="title" value="My Sample Code" />
            </report>
    
            <fileset dir="src">
                <include name="**/*.groovy"/>
            </fileset>
        </codenarc>
    </target>
```


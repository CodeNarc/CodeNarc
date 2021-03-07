---
layout: default
title: CodeNarc Text / IDE / Compact ReportWriters
---

# Text / IDE / Compact ReportWriters

## TextReportWriter 

### Description

The `org.codenarc.report.TextReportWriter` class (type="text") produces a simple text report of the
**CodeNarc** results. See a [Sample Report](./SampleCodeNarcTextReport.txt).

### Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option
for a report.

The `TextReportWriter` class supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| maxPriority             | The maximum priority level for violations in the report. For instance, setting *maxPriority* to 2 will result in the report containing only priority 1 and 2 violations (and omitting violations with priority 3). | 3 |
| outputFile              | The path and filename for the output report file.              | "CodeNarcReport.txt"  |
| title                   | The title for the output report.                               |                        |
| writeToStandardOut      | Set to `true` to write out the report to *stdout* (`System.out`) instead of writing to a file. |  `false` |


### Example Configuration

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


## IdeTextReportWriter 

### Description

The `org.codenarc.report.IdeTextReportWriter` class (type="ide") produces a text report of the
**CodeNarc** results, and includes IDE-compatible (Eclipse, Idea) hyperlinks to source code for violations.

### Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option
for a report. Note that this ReportWriter defaults the *writeToStandardOut* property to `true`.

The `IdeTextReportWriter` class supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| maxPriority             | The maximum priority level for violations in the report. For instance, setting *maxPriority* to 2 will result in the report containing only priority 1 and 2 violations (and omitting violations with priority 3). | 3 |
| outputFile              | The path and filename for the output report file.              | "CodeNarcReport.txt"  |
| title                   | The title for the output report.                               |                        |
| writeToStandardOut      | Set to `true` to write out the report to *stdout* (`System.out`) instead of writing to a file. |  `true` |


### Example Configuration

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

## CompactTextReportWriter 

### Description

The `org.codenarc.report.CompactTextReportWriter` class (type="compact") produces a simple text report of the
**CodeNarc** results, with one line per violation. This can be useful for reading/parsing by other tools.

### Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option
for a report. Note that this ReportWriter defaults the *writeToStandardOut* property to `true`.

The `IdeTextReportWriter` class supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| maxPriority             | The maximum priority level for violations in the report. For instance, setting *maxPriority* to 2 will result in the report containing only priority 1 and 2 violations (and omitting violations with priority 3). | 3 |
| outputFile              | The path and filename for the output report file.                                              | "CodeNarcReport.txt"  |
| writeToStandardOut      | Set to `true` to write out the report to *stdout* (`System.out`) instead of writing to a file. |  `true` |


### Example Output

  Here is an example output from `CompactTextReportWriter`:

```
    src/main/MyAction.groovy:11:Rule1 null
    src/main/MyAction.groovy:11:Rule1 null
    src/main/MyAction.groovy:2:AnotherRule bad stuff: !@#\$%^&*()_+<>
    src/main/MyAction.groovy:333:BadStuff Other info
    src/main/MyAction.groovy:333:BadStuff Other info
    src/main/dao/MyDao.groovy:333:BadStuff Other info
    src/main/dao/MyOtherDao.groovy:11:Rule1 null
    src/main/dao/MyOtherDao.groovy:2:AnotherRule bad stuff: !@#\$%^&*()_+<>
```




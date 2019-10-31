---
layout: default
title: CodeNarc SortableHtmlReportWriter
---

# SortableHtmlReportWriter

## Description

The `org.codenarc.report.SortableHtmlReportWriter` class (type="sortable") produces an HTML report
containing a summary results table and a single table for all violations, which can be sorted in several ways:
  * Sort by rule name
  * Sort by rule priority
  * Sort by rule, in order of the rules that have the greatest number of violations
  * Sort by file, in order of the files that have the greatest number of violations

It also includes an optional table of the rules applied, along with their descriptions.

See a [Sample Report](./SampleCodeNarcSortableHtmlReport.html).


## Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option for a report.

`org.codenarc.report.SortableHtmlReportWriter` supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| maxPriority             | The maximum priority level for violations in the report. For instance, setting *maxPriority* to 2 will result in the report containing only priority 1 and 2 violations (and omitting violations with priority 3).                                   | 3                       |
| outputFile              | The path and filename for the output report file.              | "CodeNarcReport.html"  |
| title                   | The title for the output report.                               |                        |
| writeToStandardOut      | Set to `true` to write out the report to *stdout* (`System.out`) instead of writing to a file. |  `false` |
| includeRuleDescriptions | Set to `false` to exclude the rule descriptions section of the report. It defaults to `true`.                      | `true` |


## Example

Here is an example Ant XML build file illustrating configuration of
`org.codenarc.report.SortableHtmlReportWriter`. Note that the report **type** is specified as **"sortable"**.

```
    <taskdef name="codenarc" classname="org.codenarc.ant.CodeNarcTask"/>
    <target name="runCodeNarc">
        <codenarc
                ruleSetFiles="rulesets/basic.xml,rulesets/exceptions.xml,rulesets/imports.xml"
                maxPriority1Violations="0">
    
            <report type="sortable">
                <option name="outputFile" value="reports/CodeNarcSortableReport.html" />
                <option name="title" value="My Sample Code" />
            </report>
    
            <fileset dir="src">
                <include name="**/*.groovy"/>
            </fileset>
        </codenarc>
    </target>
```

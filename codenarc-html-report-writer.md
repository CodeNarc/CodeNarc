---
layout: default
title: CodeNarc HtmlReportWriter
---

# HtmlReportWriter

## Description

The `org.codenarc.report.HtmlReportWriter` class (type="html") produces an HTML report
containing a table with summary results by package and a separate section for
each package containing violations, and then a table of the rules applied, along
with their descriptions.

See a [Sample Report](./SampleCodeNarcHtmlReport.html).

Alternatively, see the [mrhaki](http://mrhaki.blogspot.com/) blog post about
[creating custom CodeNarc HTML reports using XSLT](http://mrhaki.blogspot.com/2011/01/groovy-goodness-create-codenarc-reports.html?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+mrhaki+%28Messages+from+mrhaki%29).


## Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option
for a report.

`org.codenarc.report.HtmlReportWriter` supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| maxPriority             | The maximum priority level for violations in the report. For instance, setting *maxPriority* to 2 will result in the report containing only priority 1 and 2 violations (and omitting violations with priority 3).                                   | 3                       |
| outputFile              | The path and filename for the output report file.              | "CodeNarcReport.html"  |
| title                   | The title for the output report.                               |                        |
| writeToStandardOut      | Set to `true` to write out the report to *stdout* (`System.out`) instead of writing to a file. |  `false` |
| includeSummaryByPackage | Set to `false` to exclude the violation summary for each package within the "Summary" section of the report. It defaults to `true`.                                        | `true` |
| includeRuleDescriptions | Set to `false` to exclude the rule descriptions section of the report. It defaults to `true`.                      | `true` |

## Example

Here is an example Ant XML build file illustrating configuration of
`org.codenarc.report.HtmlReportWriter`. Note that the report **type** is specified as **"html"**.

```
    <taskdef name="codenarc" classname="org.codenarc.ant.CodeNarcTask"/>
    <target name="runCodeNarc">
        <codenarc
                ruleSetFiles="rulesets/basic.xml,rulesets/exceptions.xml,rulesets/imports.xml>
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


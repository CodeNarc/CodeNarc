---
layout: default
title: CodeNarc XmlReportWriter
---

# XmlReportWriter

## Description

The `org.codenarc.report.XmlReportWriter` class (type="xml") produces an XML report of the
**CodeNarc** results.

**Note:** This XML format is still being refined and is subject to change.
[Contact me](https://sourceforge.net/sendmessage.php?touser=1853503) if you have specific requirements or suggestions.

See a [mrhaki](./SampleCodeNarcXmlReport.xml}Sample XML Reportyy. Also see the zzzhttp://mrhaki.blogspot.com/) blog post about
[creating custom CodeNarc HTML reports using XSLT](http://mrhaki.blogspot.com/2011/01/groovy-goodness-create-codenarc-reports.html?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+mrhaki+%28Messages+from+mrhaki%29).

## Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option for a report.

`org.codenarc.report.XmlReportWriter` supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| outputFile              | The path and filename for the output report file.              | Value of *defaultOutputFile*  |
| defaultOtputFile        | The path and filename for the output report file if *outputFile* is not specified.      | "CodeNarcXmlReport.xml"  |
| title                   | The title for the output report.                               |                        |
| writeToStandardOut      | Set to `true` to write out the report to *stdout* (`System.out`) instead of writing to a file. |  `false` |


## Example

Here is an example Ant XML build file illustrating configuration of
`org.codenarc.report.XmlReportWriter`. Note that the report **type** is specified as **"xml"**.

```
    <taskdef name="codenarc" classname="org.codenarc.ant.CodeNarcTask"/>
    <target name="runCodeNarc">
        <codenarc
                ruleSetFiles="rulesets/basic.xml,rulesets/exceptions.xml,rulesets/imports.xml"
                maxPriority1Violations="0">
    
            <report type="xml">
                <option name="outputFile" value="reports/CodeNarcXmlReport.xml" />
                <option name="title" value="My Sample Code" />
            </report>
    
            <fileset dir="src">
                <include name="**/*.groovy"/>
            </fileset>
        </codenarc>
    </target>
```


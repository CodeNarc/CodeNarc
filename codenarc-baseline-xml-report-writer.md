---
layout: default
title: CodeNarc BaselineXmlReportWriter
---

# BaselineXmlReportWriter [Beta]

*This feature is still experimental, and may change in the future...*

## Description

The `org.codenarc.report.BaselineXmlReportWriter` class (type="baseline") produces an XML "baseline" report
of the **CodeNarc** results. This is used in conjunction with the *"excludeBaseline"* property of the **CodeNarc**
Ant Task to filter out (exclude) a set of *baseline violations* from subsequent **CodeNarc** executions.

Excluding *baseline violations* can be useful when running **CodeNarc** against an existing (legacy) codebase,
when it is not practical to address a large number of existing rule violations. Establish a *baseline violations*
file, and then use that so that **CodeNarc** results show only *new* violations.

The steps to initialize and then exclude the *baseline violations* are:

  1. Generate the *baseline violations* file. See
     [Example 1: How to Generate the Baseline Violations File](#example1).

     By default, this will create a "CodeNarcBaselineViolations.xml" file within the current directory, but you can
     configure that file name/path using the *outputFile* option of this *ReportWriter*.

     Note: Be sure to remove the *baseline* report `(\*report type="baseline"\*)` after you have generated
     the *baseline* report file. Otherwise you will overwrite that file the next time you run **CodeNarc**!

  2. Update your **CodeNarc** Ant Task configuration to specify the *"excludeBaseline"* property.
     See [Example 2: How to Use the Baseline Violations File](#example2).


## Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option for a report.

`org.codenarc.report.BaselineXmlReportWriter` (type="baseline") supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| outputFile              | The path and filename for the output report file.              | Value of *defaultOutputFile*  |
| defaultOtputFile        | The path and filename for the output report file if *outputFile* is not specified.      | "CodeNarcBaselineViolations.xml" |
| title                   | The title for the output report.                               |                        |
| writeToStandardOut      | Set to `true` to write out the report to *stdout* (`System.out`) instead of writing to a file. |  `false` |


## <a name="example1"/> Example 1: How to Generate the Baseline Violations File

Here is an example Ant XML build file illustrating configuration of
`org.codenarc.report.BaselineXmlReportWriter` to generate a *baseline violations* file.
Note that the report **type** is specified as **"baseline"**.

```
    <taskdef name="codenarc" classname="org.codenarc.ant.CodeNarcTask"/>
    <target name="runCodeNarc">
        <codenarc ruleSetFiles="rulesets/basic.xml">
    
            <report type="baseline">
                <option name="title" value="My Sample Code" />
            </report>
    
            <fileset dir="src">
                <include name="**/*.groovy"/>
            </fileset>
        </codenarc>
    </target>
```


## <a name="example2"/> Example 2: How to Use the Baseline Violations File

Here is an example Ant XML build file illustrating configuration of the *"excludeBaseline"* property.
to use the *baseline violations* file. This will filter out (exclude) a set of *baseline violations*
from subsequent **CodeNarc** executions. Note that the *type="baseline"* report was removed.

```
    <taskdef name="codenarc" classname="org.codenarc.ant.CodeNarcTask"/>
    <target name="runCodeNarc">
        <codenarc ruleSetFiles="rulesets/basic.xml" excludeBaseline="CodeNarcBaselineViolations.xml">
    
            <report type="ide">
                <option name="title" value="My Sample Code" />
            </report>
    
            <fileset dir="src">
                <include name="**/*.groovy"/>
            </fileset>
        </codenarc>
    </target>
```


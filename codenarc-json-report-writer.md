---
layout: default
title: CodeNarc JsonReportWriter
---

# JsonReportWriter

## Description

The `org.codenarc.report.JsonReportWriter` class (type="json") produces an JSON report of the
**CodeNarc** results.

## Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option for a report.

`org.codenarc.report.JsonReportWriter` supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| outputFile              | The path and filename for the output report file.              | Value of *defaultOutputFile*  |
| defaultOutputFile       | The path and filename for the output report file if *outputFile* is not specified.      | "CodeNarcJsonReport.json"  |
| title                   | The title for the output report.                               |                        |
| writeToStandardOut      | Set to `true` to write out the report to *stdout* (`System.out`) instead of writing to a file. |  `false` |
| writeAsSingleLine       | Set to `true` to provide output json in a single line, for easier parsing  | `false` |

## Example

Here is an example Ant XML build file illustrating configuration of
`org.codenarc.report.JsonReportWriter`. Note that the report **type** is specified as **"json"**.

```
    <taskdef name="codenarc" classname="org.codenarc.ant.CodeNarcTask"/>
    <target name="runCodeNarc">
        <codenarc
                ruleSetFiles="rulesets/basic.xml,rulesets/exceptions.xml,rulesets/imports.xml"
                maxPriority1Violations="0">
    
            <report type="json">
                <option name="outputFile" value="reports/CodeNarcJsonReport.json" />
                <option name="title" value="My Sample Code" />
            </report>
    
            <fileset dir="src">
                <include name="**/*.groovy"/>
            </fileset>
        </codenarc>
    </target>
```

## Example output

```json
{
    "codeNarc": {
        "url": "https://www.codenarc.org",
        "version": "1.5"
    },
    "report": {
        "timestamp": "Jan 1, 2010 4:51:12 PM"
    },
    "project": {
        "title": "My Cool Project",
        "sourceDirectories": [
            "c:/MyProject/src/main/groovy",
            "c:/MyProject/src/test/groovy"
        ]
    },
    "summary": {
        "totalFiles": 6,
        "filesWithViolations": 3,
        "priority1": 2,
        "priority2": 2,
        "priority3": 3
    },
    "packages": [
        {
            "path": "src/main",
            "totalFiles": 3,
            "filesWithViolations": 3,
            "priority1": 2,
            "priority2": 2,
            "priority3": 3,
            "files": [
                {
                    "name": "MyAction.groovy",
                    "violations": [
                        {
                            "ruleName": "RULE1",
                            "priority": 1,
                            "lineNumber": 111,
                            "sourceLine": "if (count < 23 && index <= 99 && name.contains('\u0000')) {"
                        },
                        {
                            "ruleName": "RULE3",
                            "priority": 3,
                            "lineNumber": 333,
                            "sourceLine": "throw new Exception(\"cdata=<![CDATA[whatever]]>\") // Some very long message 1234567890123456789012345678901234567890",
                            "message": "Other info"
                        },
                        {
                            "ruleName": "RULE3",
                            "priority": 3,
                            "lineNumber": 333,
                            "sourceLine": "throw new Exception(\"cdata=<![CDATA[whatever]]>\") // Some very long message 1234567890123456789012345678901234567890",
                            "message": "Other info"
                        },
                        {
                            "ruleName": "RULE1",
                            "priority": 1,
                            "lineNumber": 111,
                            "sourceLine": "if (count < 23 && index <= 99 && name.contains('\u0000')) {"
                        },
                        {
                            "ruleName": "RULE2",
                            "priority": 2,
                            "lineNumber": 222,
                            "message": "bad stuff: !@#$%^&*()_+<>"
                        }
                    ]
                }
            ]
        },
        {
            "path": "src/main/dao",
            "totalFiles": 2,
            "filesWithViolations": 2,
            "priority1": 0,
            "priority2": 1,
            "priority3": 1,
            "files": [
                {
                    "name": "MyDao.groovy",
                    "violations": [
                        {
                            "ruleName": "RULE3",
                            "priority": 3,
                            "lineNumber": 333,
                            "sourceLine": "throw new Exception(\"cdata=<![CDATA[whatever]]>\") // Some very long message 1234567890123456789012345678901234567890",
                            "message": "Other info"
                        }
                    ]
                },
                {
                    "name": "MyOtherDao.groovy",
                    "violations": [
                        {
                            "ruleName": "RULE2",
                            "priority": 2,
                            "lineNumber": 222,
                            "message": "bad stuff: !@#$%^&*()_+<>"
                        }
                    ]
                }
            ]
        },
        {
            "path": "src/test",
            "totalFiles": 3,
            "filesWithViolations": 0,
            "priority1": 0,
            "priority2": 0,
            "priority3": 0,
            "files": [
                
            ]
        }
    ],
    "rules": [
        {
            "name": "DuplicateImport",
            "description": "Custom: Duplicate imports"
        },
        {
            "name": "UnnecessaryBooleanInstantiation",
            "description": "Use Boolean.valueOf() for variable values or Boolean.TRUE and Boolean.FALSE for constant values instead of calling the Boolean() constructor directly or calling Boolean.valueOf(true) or Boolean.valueOf(false)."
        }
    ]
}
```
---
layout: default
title: CodeNarc SarifReportWriter
---

# SarifReportWriter

## Description

The `org.codenarc.report.SarifReportWriter` class (type="sarif") produces a JSON report of the **CodeNarc** results in the [Static Analysis Results Interchange Format (SARIF)](https://sarifweb.azurewebsites.net/). This is a standardized format that can be consumed by a wide variety of tools.

## Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option for a report.

`org.codenarc.report.SarifReportWriter` supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| outputFile              | The path and filename for the output report file.                                              | Value of *defaultOutputFile*     |
| defaultOutputFile       | The path and filename for the output report file if *outputFile* is not specified.             | "CodeNarcSarifReport.sarif.json" |
| writeToStandardOut      | Set to `true` to write out the report to *stdout* (`System.out`) instead of writing to a file. | `false`                          |
| writeAsSingleLine       | Set to `true` to provide output JSON in a single line, for easier parsing.                     | `false`                          |

## Example

Here is an example command line call illustrating configuration of
`org.codenarc.report.SarifReportWriter`. Note that the report **type** is specified as **"sarif"**.

```
java -classpath $GROOVY_JAR:$CODENARC_JAR:$SLF4J_JAR org.codenarc.CodeNarc -report=sarif:reports/codenarc-report.sarif.json
```

## Example output

```json
{
    "$schema": "https://json.schemastore.org/sarif-2.1.0.json",
    "version": "2.1.0",
    "runs": [
        {
            "tool": {
                "driver": {
                    "name": "CodeNarc",
                    "version": "3.5.0",
                    "informationUri": "https://codenarc.org",
                    "rules": [
                        {
                            "id": "EmptyClass",
                            "name": "EmptyClassRule",
                            "shortDescription": { "text": "Reports classes without methods, fields or properties. Why would you need a class like this?" },
                            "helpUri": "https://codenarc.org/codenarc-rules-basic.html#emptyclass-rule",
                            "properties": { "priority": 3 }
                        }
                    ]
                }
            },
            "results": [
                {
                    "ruleId": "EmptyClass",
                    "level": "note",
                    "message": { "text": "Class 'SampleFile' is empty (has no methods, fields or properties). Why would you need a class like this?" },
                    "locations": [ { "physicalLocation": { "artifactLocation": { "uri": "src/test/resources/SampleFile.groovy" }, "region": { "startLine": 1 } } } ]
                }
            ]
        }
    ]
}
```

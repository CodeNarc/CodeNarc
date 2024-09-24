---
layout: default
title: CodeNarc GitlabCodeQualityReportWriter
---

# GitlabCodeQualityReportWriter

## Description

The `org.codenarc.report.GitlabCodeQualityReportWriter` class (type="gitlab")
produces a JSON report of the **CodeNarc** results, suitable for consumption
by GitLab.

## Option Nested Elements

The **option** element is a child of the **report** element and defines a report-specific option for a report.

`org.codenarc.report.GitlabCodeQualityReportWriter` supports the following options:

| Attribute               | Description            | Default             |
|-------------------------|------------------------|---------------------|
| outputFile              | The path and filename for the output report file.              | Value of *defaultOutputFile*  |
| defaultOutputFile       | The path and filename for the output report file if *outputFile* is not specified.      | "CodeNarcGitlabCodeQualityReport.json"  |
| writeAsSingleLine       | Set to `true` to provide output JSON in a single line, for easier parsing  | `false` |

## Example

Here is an extract from a GitLab CI/CD pipeline definition. It uses the
generated JAR file to run CodeNarc and exports the result as artifact to
GitLab.
Notes:

- The report **type** is specified as **"gitlab"**.
- Since this report type is currently (2024-09-24) still unreleased, the
  preferred way of using a Docker container is not available.

```yaml
codenarc-linter:
  artifacts:
    name: $CI_JOB_NAME artifacts from $CI_PROJECT_NAME on $CI_COMMIT_REF_SLUG
    reports:
      codequality:
        - report.json
    when: always
  image: openjdk:11-jre-slim
  script:
    - java -jar CodeNarc-3.5.0-all.jar -report=gitlab:./report.json
```

## Example output

```json
[
    {
        "description": "Class 'SampleFile' is empty (has no methods, fields or properties). Why would you need a class like this?",
        "check_name": "EmptyClass",
        "fingerprint": "37e50585a6afe0d042752030ed92b20febe9e622",
        "severity": "major",
        "location": {
            "path": "src/test/resources/SampleFile.groovy",
            "lines": {
                "begin": 1
            }
        }
    },
    {
        "description": "Class 'SourceFile1' is empty (has no methods, fields or properties). Why would you need a class like this?",
        "check_name": "EmptyClass",
        "fingerprint": "c3985bbc5fa389cae1cb8a5bdcda4904906e7cc1",
        "severity": "major",
        "location": {
            "path": "src/test/resources/source/SourceFile1.groovy",
            "lines": {
                "begin": 23
            }
        }
    }
]
```

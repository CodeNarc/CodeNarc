---
layout: default
title: CodeNarc - Jenkins Rules
---  

# Jenkins Rules  ("*rulesets/jenkins.xml*")

## ClosureInGString Rule

*Since CodeNarc 3.5.0*

Closures are CPS transformed by Jenkins and will cause an error at runtime when used in GStrings.
Typically they can be replaced by variable interpolation.
Closures that are used as argument to method calls on the other hand are fine.
See also the Jenkins docs about [Closures inside GString](https://www.jenkins.io/doc/book/pipeline/cps-method-mismatches/#PipelineCPSmethodmismatches-ClosuresinsideGString).

Examples:

```
def s1 = "some string ${-> x}" // Violation
def s2 = "some string ${x}" // OK
def s3 = "some string ${ [1,2,3].collect { i -> i * i }.toString() }" // OK
```

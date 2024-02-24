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

## ClassNotSerializable Rule

*Since CodeNarc 3.5.0*

Classes in Jenkins libraries should generally implement the Serializable interface because every expression/variable used in a CPS transformed method can potentially be serialized.
Generally all user defined classes (not from external libraries) in pipeline libraries or Jenkinsfiles are already implicitly Serializable in Jenkins but it makes static analysis easier later on if all classes are marked Serializable explicitly.

Example of violation:

```
class SomeClass {}
```

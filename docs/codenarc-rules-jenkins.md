---
layout: default
title: CodeNarc - Jenkins Rules
---  

# Jenkins Rules  ("*rulesets/jenkins.xml*")

## ClassNotSerializable Rule

*Since CodeNarc 3.5.0*

Classes in Jenkins libraries should generally implement the Serializable interface because every expression/variable used in a CPS transformed method can potentially be serialized.
Generally all user defined classes (not from external libraries) in pipeline libraries or Jenkinsfiles are already implicitly Serializable in Jenkins but it makes static analysis easier later on if all classes are marked Serializable explicitly.

Example of violation:

```
class SomeClass {}
```

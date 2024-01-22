---
layout: default
title: CodeNarc - Jenkins Rules
---  

# Jenkins Rules  ("*rulesets/jenkins.xml*")

## ParameterOrReturnTypeNotSerializable Rule

*Since CodeNarc 3.5.0*

Every parameter and return type has to implement the Serializable interface in Jenkins CPS tranformed Code.

Examples:

```
class SomeClass {}
class SerializableClass implements Serializable {}

void method(int i, SomeClass s) {}                                // Violation
SomeClass returnMethod() { return null }                          // Violation
SerializableClass safeMethod(SerializableClass s) { return null } // OK
```

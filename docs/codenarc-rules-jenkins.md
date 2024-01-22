---
layout: default
title: CodeNarc - Jenkins Rules
---  

# Jenkins Rules  ("*rulesets/jenkins.xml*")

## OverridesNotNonCps Rule

*Since CodeNarc 3.5.0*

Overridden methods of the standard library (e.g. from java.lang.Object) are often called from there and therefore must not be CPS transformed in Jenkins.
See also the Jenkins docs on [Overrides of non-CPS-transformed methods](https://www.jenkins.io/doc/book/pipeline/cps-method-mismatches/#overrides-of-non-cps-transformed-methods).

Examples:

```
class SomeClass {

    @Override // Violation
    String toString() {
        return ''
    }

    @NonCPS
    @Override // OK
    boolean equals(Object other) {
        return false
    }
}
```
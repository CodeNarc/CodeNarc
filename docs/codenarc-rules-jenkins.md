---
layout: default
title: CodeNarc - Jenkins Rules
---  

# Jenkins Rules  ("*rulesets/jenkins.xml*")

## ForbiddenCallInCpsMethod Rule

*Since CodeNarc 3.5.0*

Some methods from the standard library cannot be CPS transformed and therefore must not be called with CPS transformed closure arguments.
See also the Jenkins docs on [Calling non-CPS-transformed methods with CPS-transformed arguments](https://www.jenkins.io/doc/book/pipeline/cps-method-mismatches/#calling-non-cps-transformed-methods-with-cps-transformed-arguments)

Examples:

```
void cpsMethod() {
    List l = [4,1,3]
    l.sort { a, b -> a > b } // Violation
    l.toSorted { a, b -> a > b } // Violation
    "hello".eachLine { l, n -> println(l) } // Violation
    "hello".eachLine(2) { l, n -> println(l) } // Violation
}
```

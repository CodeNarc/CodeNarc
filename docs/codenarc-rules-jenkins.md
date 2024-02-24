---
layout: default
title: CodeNarc - Jenkins Rules
---  

# Jenkins Rules  ("*rulesets/jenkins.xml*")

## CpsCallFromNonCpsMethod Rule

*Since CodeNarc 3.5.0*

CPS transformed methods may not be called from non CPS transformed methods in Jenkins.
Every analyzed Method is assumed to be CPS transformed, except it is annotated with `com.cloudbees.groovy.cps.NonCPS`, is a constructor or a library method that is not configured in `cpsPackages`.
Pipeline steps are assumed to be CPS transformed except for 'echo', 'properties' and 'getContext'.
See also the Jenkins docs on [Pipeline CPS Method Mismatches](https://www.jenkins.io/doc/book/pipeline/cps-method-mismatches/).

| Property              | Description                                       | Default Value |
|-----------------------|---------------------------------------------------|---------------|
| cpsScriptVariableName | Jenkins pipeline script variable name             | 'script'      |
| cpsPackages           | Package names of used Jenkins pipeline libraries  | []            |

Known limitations:

* Methods are only checked for their name, parameters are ignored. Therefore false positives are possible if two overloaded methods are both CPS and non CPS transformed respectively.
* Because of their dynamic nature, pipeline steps can only be recognized if they are called on a predefined script variable (default is `script`).
* Method calls on dynamic types (Object) can't be resolved.

Examples:

```
class SomeClass() {
    public int value = cpsMethod() // Violation
    SomeClass() {}
}

@NonCPS
void nonCpsMethod() {}

void cpsMethod() {}

@NonCPS
void someMethod() {
    nonCpsMethod() // OK
    new SomeClass() // OK
    cpsMethod() // Violation
    script.sh('echo hello') // Violation
}
```

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

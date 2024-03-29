---
layout: default
title: CodeNarc - Unnecessary Rules
---  

# Unused Rules  ("*rulesets/unused.xml*")

## UnusedArray Rule

Checks for array allocations that are not assigned or used, unless it is the last
statement within a block (because it may be the intentional return value). Examples
include:

```
    int myMethod() {
        new String[3]               // unused
        return -1
    }

    String[] myMethod() {
        new String[3]               // OK (last statement in block)
    }

    def closure = {
        doStuff()
        new Date[3]                 // unused
        doOtherStuff()
    }

    def closure = { new Date[3] }   // OK (last statement in block)
```


## UnusedMethodParameter Rule

*Since CodeNarc 0.16*

This rule finds instances of method (or constructor) parameters not being used. It does not analyze private methods (that is done by
the UnusedPrivateMethodParameter rule) or methods marked @Override.

  * This rule ignores `main()` methods. In Groovy, the `main()** method can either specify a `void`
    return type or else omit a return type (be dynamically typed). The `main()** method must have exactly one
    parameter. That parameter can either be typed as `String[]` or else the type can be omitted
    (be dynamically typed). And the `main()** method must be `static`.

  * You can specify an ignore list of parameter names using the 'ignoreRegex' property. By default, a parameter
    named 'ignore' or 'ignored'  does not trigger a violation (the regex value is 'ignore|ignored').
    You can add your own ignore list using this property.

  * You can specify a class name pattern to ignore using the 'ignoreClassRegex' property. By default classes named
    '*.Category' are ignored because they are category classes and have unused parameters in static methods.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreRegex                 | Regex that specifies the parameter names to ignore.                | 'ignore\|ignored' |
| ignoreClassRegex            | Regex that specifies the names of the classes to skip checking.    | '.*Category' |

Example of violations:

```
    class MyClass {
        def method(def param) {
            // param is unused
        }
    }
```

Example of code that does not cause violations:

```
    class MyClass {
        @Override
        def otherMethod(def param) {
            // this is OK because it overrides a super class
        }
    }

    class MyCategory {
        // Category classes are ignored by default
        void myMethod1(String string, int value) { }
        void myMethod1(String string, int value, name) { }
    }

    class MainClass1 {
        // main() methods are ignored
        public static void main(String[] args) { }
    }
    class MainClass2 {
        // This is also a valid Groovy main() method
        static main(args) { }
    }
```


## UnusedObject Rule

Checks for object allocations that are not assigned or used, unless it is the last
statement within a block (because it may be the intentional return value). Examples
include:

By default, this rule does not analyze test files. This rule sets the default value of the
*doNotApplyToFilesMatching* property to ignore file names ending in 'Spec.groovy, ''Test.groovy', 'Tests.groovy'
or 'TestCase.groovy'. Invoking constructors without using the result is a common pattern in tests.

```
    int myMethod() {
        new BigDecimal("23.45")     // unused
        return -1
    }

    BigDecimal myMethod() {
        new BigDecimal("23.45")     // OK (last statement in block)
    }

    def closure = {
        doStuff()
        new Date()                  // unused
        doOtherStuff()
    }

    def closure = { new Date() }    // OK (last statement in block)
```


## UnusedPrivateField Rule

Checks for private fields that are not referenced within the same class. Note that the `private`
modifier is not currently "respected" by Groovy code (i.e., Groovy can access `private`
members within other classes).

By default, fields named `serialVersionUID`, and fields annotated with `groovy.lang.Delegate` are ignored.
The rule has a property named *ignoreFieldNames*, which can be set to ignore other field names as well.
For instance, to also ignore fields named 'fieldx', set the property to the 'fieldx, serialVersionUID'

| Property                         | Description                                                                                                                                                                                                 | Default Value    |
|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------|
| ignoreFieldNames                 | Specifies one or more (comma-separated) field names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).                                | `serialVersionUID` |
| ignoreClassesAnnotatedWithNames  | Specifies one or more (comma-separated) annotation names; any classes annotated with those should be ignored (i.e., should not cause a rule violation). The names may optionally contain wildcards (*,?).   | `Entity` |
| allowConstructorOnlyUsages       | Should be set to `false` if violations are to be raised for fields which are used only within constructors.                                                                                                 | `true` |

Known limitations:
  * Does not recognize field access when field name is a GString (e.g. `this."${fieldName}"`)
  * Does not recognize access of private field of another instance (i.e. other than `this`)


## UnusedPrivateMethod Rule

Checks for private methods that are not referenced within the same class. Note that the `private`
modifier is not currently "respected" by Groovy code (i.e., Groovy can access `private`
members within other classes).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreMethodsWithAnnotationNames | Specifies one or more (comma-separated) annotation names that mark private methods that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?). | '' |

Known limitations:
  * Does not recognize method reference through property access (e.g. `getName()` accessed as `x.name`)
  * Does not recognize method invocations when method name is a GString (e.g. `this."${methodName}"()`)
  * Does not recognize invoking private method of another instance (i.e. other than `this`)
  * Does not recognize when a private method is used as @MethodSource
  * Does not differentiate between multiple private methods with the same name but different parameters (i.e., overloaded)
  * Does not check for unused constructors


## UnusedPrivateMethodParameter Rule

*Since CodeNarc 0.12*

Checks for parameters to private methods (or constructors) that are not referenced within the method body. Note that the
`private` modifier is not currently "respected" by Groovy code (i.e., Groovy can access `private`
members within other classes).

Known limitations:
  * Does not recognize parameter references within an inner class. See
    [CodeNarc bug #3155974](https://sourceforge.net/tracker/index.php?func=detail&aid=3155974&group_id=250145&atid=1126573).
  * Does not recognize parameter references when parameter name is a GString (e.g. `println "${parameterName}"`)
  * You can specify an ignore list using the 'ignoreRegex' property. By default, a parameter named 'ignore' or 'ignored'
    does not trigger a violation (the regex value is 'ignore|ignored'). You can add your own ignore list using this property.


## UnusedVariable Rule

Checks for variables that are never referenced. An assignment to the variable is not considered a reference.

The rule has a property named ignoreVariableNames, which can be set to ignore some variable names.
For instance, to ignore fields named 'unused', set the property to 'unused'.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreVariableNames         | Specifies one or more (comma-separated) variable names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |

Known limitations:
  * Incorrectly considers a variable referenced if another variable with the same name is referenced
    elsewhere (in another scope/block).


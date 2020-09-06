---
layout: default
title: CodeNarc - Enhanced Classpath Rules
---  

# Enhanced Classpath Rules ("*rulesets/enhanced.xml*")

These rules use a later compilation phase for parsing of the Groovy source code, allowing **CodeNarc**
to use a richer and more complete Abstract Syntax Tree (AST). The downside is that the later
compiler phase requires **CodeNarc** to have the application classes being analyzed, as well as
any referenced classes, on the classpath.

Note that these rules may be reorganized and moved to a other rulesets or packages. If possible, include
them individually within your ruleset -- refer to them using the rule name, e.g. "CloneWithoutCloneable",
rather that pulling in the entire "*rulesets/enhanced.xml*" ruleset, to protect against future reorganization.

Note that if your ruleset includes an *enhanced* rule, it can significantly increase execution times for CodeNarc.

## CloneWithoutCloneable Rule

*Since CodeNarc 0.19*

The method clone() should only be declared if the class implements the Cloneable interface.

NOTE: This is a [CodeNarc Enhanced Classpath Rule](./codenarc-enhanced-classpath-rules.html).
It requires **CodeNarc** to have the application classes being analyzed, as well as any referenced classes, on the classpath.

Example of violations:

```
    class ValueClass {
        ValueClass clone() {
        }
    }
```


## JUnitAssertEqualsConstantActualValue Rule

*Since CodeNarc 0.19*

Reports usages of `org.junit.Assert.assertEquals([message,] expected, actual)` where the `actual` parameter
is a constant or a literal. Most likely it was intended to be the `expected` value.

NOTE: This is a [CodeNarc Enhanced Classpath Rule](./codenarc-enhanced-classpath-rules.html).
It requires **CodeNarc** to have the application classes being analyzed, as well as any referenced classes, on the classpath.

Example of violations:

```
    assertEquals(result, 2)
    assertEquals("Message", result, 2)
    assertEquals(result, 2.3d, 0.5d)
    assertEquals("Message", result, 2.3d, 0.5d)
```


## UnsafeImplementationAsMap Rule

*Since CodeNarc 0.19*

Reports incomplete interface implementations created by map-to-interface coercions.

By default, this rule does not apply to test files.

NOTE: This is a [CodeNarc Enhanced Classpath Rule](./codenarc-enhanced-classpath-rules.html).
It requires **CodeNarc** to have the application classes being analyzed, as well as any referenced classes, on the classpath.

Example of violations:

```
    [mouseClicked: { ... }] as MouseListener
    //not all MouseListener methods are implemented which can lead to UnsupportedOperationException-s
```


## MissingOverrideAnnotation Rule

*Since CodeNarc 1.1*

Checks for methods that override a method in a superclass or implement a method in an interface but are not annotated
with `@Override`.

Consistent use of `@Override` annotation helps in spotting situations when the intent was to override a method but
because of a mistake in method signature that is not the case. Additionally, applying `@Override` annotation to
all overridden methods helps in spotting unnecessary methods which no longer override any methods after removing them
from superclasses or implemented interfaces because such annotated methods will cause compilation errors.

Example of violations:

```
    class ClassOverridingToString {
        String toString() {
          "ClassOverridingToString"
        }
    }
```
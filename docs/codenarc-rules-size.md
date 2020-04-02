---
layout: default
title: CodeNarc - Size and Complexity Rules
---  

# Size and Complexity Rules  ("*rulesets/size.xml*")


## AbcMetric Rule

Calculates the *ABC* size metric for methods/classes and checks against configured threshold values.

The **maxMethodAbcScore** property holds the threshold value for the ABC score for each method.
If this value is non-zero, a method with an ABC score greater than this value is considered a violation.
The value does not have to be an integer (e.g., 1.7 is allowed).

The **maxClassAverageMethodAbcScore** property holds the threshold value for the average ABC
score for each class. If this value is non-zero, a class with an average ABC score value greater
than this value is considered a violation. The value does not have to be an integer.

The **maxClassAbcScore** property holds the threshold value for the total ABC
score value for each class. If this value is non-zero, a class with a total ABC score
greater than this value is considered a violation. The value does not have to be an integer.

This rule treats "closure fields" as methods. If a class field is initialized to a Closure (ClosureExpression),
then that Closure is analyzed and checked just like a method.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| maxMethodAbcScore             | The maximum *ABC* score allowed for a single method (or "closure field"). If zero or *null*, then do not check method-level scores. | 60 |
| maxClassAverageMethodAbcScore | The maximum average *ABC* score allowed for a class, calculated as the average score of its methods or "closure fields". If zero or *null*, then do not check class-level average scores. | 60 |
| maxClassAbcScore              | The maximum *ABC* score allowed for a class, calculated as the total ABC score of its methods or "closure fields". If zero or *null*, then do not check class-level scores. | 0 |
| ignoreMethodNames             | Specifies one or more (comma-separated) method names that that should not cause a rule violation. The names may optionally contain wildcards (*,?). Note that the ignored methods still contribute to the class complexity value. | `null` |

### ABC Size Metric Calculation Rules

The *ABC* score is calculated as follows:
The *ABC* metric measures size by counting the number of Assignments (A), Branches (B) and
Conditions (C) and assigns a single numerical score calculated as:

` |ABC| = sqrt((A*A)+(B*B)+(C*C)) `

The *ABC Metric* calculation rules for Groovy:
  * Add one to the *assignment* count for each occurrence of an assignment operator, excluding constant
    declarations: = *= /= %= += **= **= &= |= ^= `=
  * Add one to the *assignment* count for each occurrence of an increment or decrement operator
    (prefix or postfix): ++ --
  * Add one to the *branch* count for each function call or class method call.
  * Add one to the *branch* count for each occurrence of the new operator.
  * Add one to the *condition* count for each use of a conditional operator: == != *= *= * * *=* =~ ==~
  * Add one to the *condition* count for each use of the following keywords: else case default try catch ?
  * Add one to the *condition* count for each unary conditional expression.

### Notes

  * See the [ABC Metric specification](http://www.softwarerenovation.com/ABCMetric.pdf)
  * See the [Blog post](http://jakescruggs.blogspot.com/2008/08/whats-good-flog-score.html) describing guidelines for interpreting an ABC score
  * This [(Spanish) blog post](https://servicios.excentia.es/confluence/display/QAX/SONAR+ABC+Metric+Plugin) about the
    eXcentia Sonar ABC Metric Plugin (for Java) includes a table of risk classifications for ABC scores for both methods and classes.
  * See the [GMetrics ABC metric](http://gmetrics.sourceforge.net/gmetrics-AbcMetric.html).
    This includes a discussion of guidelines for interpreting *ABC* scores.
  * This rule requires Groovy 1.6 (or later).
  * This rule requires the GMetrics jar on the classpath. See [GMetrics](http://gmetrics.sourceforge.net/).


## ClassSize Rule

Checks if the size of a class exceeds the number of lines specified by the **maxLines** property.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| maxLines                    | The maximum number of lines allowed in a class definition.     | 1000 |


## CrapMetric Rule

Calculates the [C.R.A.P.](http://www.artima.com/weblogs/viewpost.jsp?thread=210575) (Change Risk Anti-Patterns)
metric score for methods/classes and checks against configured threshold values.

The *CRAP* metric score is based on the *cyclomatic complexity* and test coverage for individual methods.
A method with a *CRAP* value greater than the **maxMethodCrapScore** property causes a violation. Likewise,
a class that has an (average method) *CRAP* value greater than the **maxClassAverageMethodCrapScore**
property causes a violation.

**NOTE:** This rule requires the **GMetrics**[3] jar, version 0.5 (or later), on the classpath, as well as
a **Cobertura**[4]-[6] XML coverage file. If either of these prerequisites is not available, this rule
logs a warning messages and exits (i.e., does nothing).

The **maxMethodCrapScore** property holds the threshold value for the CRAP value for each method. If this
value is non-zero, a method with a cyclomatic complexity value greater than this value is considered a violation.

The **maxClassAverageMethodCrapScore** property holds the threshold value for the average CRAP value
for each class. If this value is non-zero, a class with an average cyclomatic complexity
value greater than this value is considered a violation.

NOTE: This rule does NOT treat *closure fields* as methods (unlike some of the other size/complexity rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| coberturaXmlFile               | The path to the Cobertura XML coverage file for the Groovy code By default, the path is relative to the classpath. But the path may be optionally prefixed by any of the valid java.net.URL prefixes, such as "file:" (to load from a relative or absolute path on the filesystem), or "http:". This property is REQUIRED. | `null` |
| maxMethodCrapScore             | The maximum *CRAP* metric value allowed for a single method. If zero or *null*, then do not check method-level complexity.  | 30 |
| maxClassAverageMethodCrapScore | The maximum *CRAP* average metric value allowed for a class, calculated as the average CRAP value of its methods. If zero or *null*, then do not check the average class-level CRAP value.  | 30 |
| maxClassCrapScore              | The maximum total *CRAP* metric value allowed for a class, calculated as the total CRAP value of its methods. If zero or *null*, then do not check class-level CRAP value.      | 0 |
| ignoreMethodNames              | Specifies one or more (comma-separated) method names that that should not cause a rule violation. The names may optionally contain wildcards (*,?). Note that the ignored methods still contribute to the class complexity value. | `null` |


## CRAP Formula

Given a Groovy method m, C.R.A.P. for m is calculated as follows:

```
  C.R.A.P.(m) = comp(m)^2 * (1 - cov(m)/100)^3 + comp(m)
```

Where **comp(m)** is the *cyclomatic complexity* of method m, and **cov(m)** is the test code coverage provided
by automated tests.


## References

  * **[1]** The original 2007 [blog post](http://www.artima.com/weblogs/viewpost.jsp?thread=210575) that defined the **CRAP** metric.

  * **[2]** A 2011 [blog post](http://googletesting.blogspot.com/2011/02/this-code-is-crap.html) from Alberto Savoia
   (the co-creator of the **CRAP** metric with Bob Evans), describing the formula, the motivation, and the **CRAP4J**
   tool for calculating **CRAP** score for Java code.

  * **[3]** The [GMetrics CRAP Metric](http://gmetrics.sourceforge.net/gmetrics-CrapMetric.html).

  * **[4]** [Cobertura](http://cobertura.sourceforge.net/) -- *Cobertura is a free Java tool that calculates the percentage
 of code accessed by tests. It can be used to identify which parts of your Java program are lacking test coverage.*

  * **[5]** [Cobertura Ant Task Reference](http://cobertura.sourceforge.net/anttaskreference.html)

  * **[6]** [Cobertura Maven Plugin](http://mojo.codehaus.org/cobertura-maven-plugin/index.html)


## CyclomaticComplexity Rule

Calculates the *Cyclomatic Complexity* for methods/classes and checks against configured threshold values.

The **maxMethodComplexity** property holds the threshold value for the cyclomatic complexity
value for each method. If this value is non-zero, a method with a cyclomatic complexity value greater than
this value is considered a violation.

The **maxClassAverageMethodComplexity** property holds the threshold value for the average cyclomatic
complexity value for each class. If this value is non-zero, a class with an average cyclomatic complexity
value greater than this value is considered a violation.

This rule treats "closure fields" as methods. If a class field is initialized to a Closure (ClosureExpression),
then that Closure is analyzed and checked just like a method.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| maxMethodComplexity             | The maximum *cyclomatic complexity* value allowed for a single method (or "closure field"). If zero or *null*, then do not check method-level complexity. | 20 |
| maxClassAverageMethodComplexity | The maximum average *cyclomatic complexity* value allowed for a class, calculated as the average complexity of its methods or "closure fields". If zero or *null*, then do not check average class-level complexity. | 20 |
| maxClassComplexity              | The maximum total *cyclomatic complexity* value allowed for a class, calculated as the total complexity of its methods or "closure fields". If zero or *null*, then do not check total class-level complexity. | 0 |
| ignoreMethodNames               | Specifies one or more (comma-separated) method names that that should not cause a rule violation. The names may optionally contain wildcards (*,?). Note that the ignored methods still contribute to the class complexity value.        | `null` |

### Cyclomatic Complexity Metric Calculation Rules

The *cyclomatic complexity* value is calculated as follows:

*Start with a initial (default) value of one (1).
Add one (1) for each occurrence of each of the following:*

     * `if` statement

     * `while` statement

     * `for` statement

     * `case` statement

     * `catch` statement

     * `&&` and `||` boolean operations

     * `?:` ternary operator and `?:` *Elvis* operator.

     * `?.` null-check operator

### Notes

  * See the [Cyclomatic Complexity Wikipedia entry](http://en.wikipedia.org/wiki/Cyclomatic_complexity)
  * See the [original paper describing Cyclomatic Complexity](http://www.literateprogramming.com/mccabe.pdf)
  * See the [GMetrics Cyclomatic Complexity metric](http://gmetrics.sourceforge.net/gmetrics-CyclomaticComplexityMetric.html).
    This includes a discussion of guidelines for interpreting *cyclomatic complexity* values.
  * This rule requires Groovy 1.6 (or later).
  * This rule requires the GMetrics jar on the classpath. See [GMetrics](http://gmetrics.sourceforge.net/).


## MethodCount Rule

*Since CodeNarc 0.11*

Checks if the number of methods within a class exceeds the number of lines specified by the **maxMethod** property.

A class with too many methods is probably a good suspect for refactoring, in order to reduce its
complexity and find a way to have more fine grained objects.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| maxMethods                  | The maximum number of methods allowed in a class definition.   | 30                     |


## MethodSize Rule

Checks if the size of a method exceeds the number of lines specified by the **maxLines** property.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| maxLines                    | The maximum number of lines allowed in a method definition.    | 100                    |
| ignoreMethodNames           | Specifies one or more (comma-separated) method names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |

Known Limitations:

  * Annotations on a method are included in the size (line count) for that method.


## NestedBlockDepth Rule

Checks for blocks or closures nested more deeply than a configured maximum number.
Blocks include `if`, `for`, `while`, `switch`, `try`, `catch`,
`finally` and `synchronized` blocks/statements, as well as closures.

Methods calls, constructor calls, and property access through Builder objects are ignore. For instance, this code
does not cause a violation:

```
    myBuilder.root {
        foo {
            bar {
                baz {
                    quix {
                        qux {
                            quaxz {
                            }
                        }
                    }
                }
            }
        }
    }
```

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| maxNestedBlockDepth         | The maximum number of nesting levels. A block or closure nested deeper than that number of levels is considered a violation.  | 5 |
| ignoreRegex                 | Determines what is a builder call. For instance, closures nested on a method named createBuilder, a property named myBuilder, or a constructor call to object MyBuilder() do not produce violations. |  .*(b\|B)uilder  |


## ParameterCount Rule

*Since CodeNarc 0.23*

Checks if the number of parameters in method/constructor exceeds the number of parameters specified by the maxParameters property.

Example of violations:

```
    void someMethod(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) { // violation
    }

    @Override
    void someMethod(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) { // no violation if ignoreOverriddenMethods == true
    }

    class SampleClass {
        SampleClass(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) { // violation
        }
    }
```

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| maxParameters               | The maximum number of parameters in method/constructor | 5 |
| ignoreOverriddenMethods     | Ignore number of parameters for methods with @Override annotation | `true` |

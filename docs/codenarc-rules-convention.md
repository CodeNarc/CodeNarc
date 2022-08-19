---
layout: default
title: CodeNarc - Convention Rules
---  

# Convention Rules  ("*rulesets/convention.xml*")


## CompileStatic Rule

*Since CodeNarc 1.4*

Enforces classes are annotated either with one of the @CompileStatic, @GrailsCompileStatic or @CompileDynamic annotations.


## ConfusingTernary Rule

*Since CodeNarc 0.12*

In a ternary expression avoid negation in the test. For example, rephrase:
`(x != y) ? diff : same` as: `(x == y) ? same : diff`. Consistent use of this rule makes the code easier to read.
Also, this resolves trivial ordering problems, such as "does the error case go first?" or "does the common case go first?".

Example:

```
    (x != y) ? diff : same      // triggers violation
    (!x) ? diff : same          // triggers violation

    (x == y) ? same : diff      // OK
    (x) ? same : diff           // OK

    // this is OK, because of GroovyTruth there is no inverse of != null
    (x != null) ? diff : same

    // this is OK, because of GroovyTruth there is no inverse of != true
    (x != true) ? diff : same

    // this is OK, because of GroovyTruth there is no inverse of != false
    (x != false) ? diff : same
```


## CouldBeElvis Rule

*Since CodeNarc 0.15*

Catch an if block that could be written as an elvis expression.

Example of violations:

```
    if (!x) {                   // violation
        x = 'some value'
    }

    if (!x)                     // violation
        x = "some value"

    if (!params.max) {          // violation
      params.max = 10
    }

    x ?: 'some value'           // OK
```


## CouldBeSwitchStatement Rule

*Since CodeNarc 1.0*

Checks for three of more if statements that could be converted to a switch. Only applies to equality and instanceof.

Example of violations:

```
    if (x == 1) {                       // violation
       y = x
    } else if (x == 2) {
       y = x * 2
    } else if (x == 3) {
       y = x * 3
    } else {
       y = 0
    }

    if (y instanceof Integer) {         // violation
       x = y + 1
    }
    if (y instanceof String) {
       x = y + '1'
    } else if (y instanceof Boolean) {
       x = !y
    } else {
       x = null
    }

    if (x == 1) {                       // OK
        y = x
    }
    if (x == 2) {
        y = x * 2
    } else {
        y = 0
    }

    if (!x && y) {                      // OK
        doSomething()
    } else if (!x && z) {
        doSomethingElse()
    } else if (!x && i) {
        doAnotherThing()
    }

```


## FieldTypeRequired Rule

*Since CodeNarc 1.1*

Checks that field types are explicitly specified (and not using `def`).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreFieldNames            | Specifies one or more (comma-separated) field names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null`  |

Example of violations:

```
    class MyClass {
        public static final NAME = "joe"        // violation
        private static count = 0                // violation

        private def name = NAME                 // violation
        protected final date = new Date()       // violation

        def defaultName                         // violation
        def maxSoFar = -1L                      // violation
    }
```


## HashtableIsObsolete Rule

*Since CodeNarc 0.17*

Checks for references to the (*effectively*) obsolete `java.util.Hashtable` class.
Use the **Java Collections Framework** classes instead, including `HashMap` or
`ConcurrentHashMap`. See the JDK javadoc.

Example of violations:

```
    def myMap = new Hashtable()           // violation
```


## IfStatementCouldBeTernary Rule

*Since CodeNarc 0.18*

Checks for:

  * An `if` statement where both the `if` and `else` blocks contain only a single `return`
    statement returning a constant or literal value.

  * A block where the second-to-last statement in a block is an `if` statement with no `else`, where
    the block contains a single `return` statement, and the last statement in the block is a `return`
    statement, and both `return` statements return a constant or literal value.
    This check is disabled by setting `checkLastStatementImplicitElse` to `false`.

Example of violations:

```
    if (condition) { return 44 } else { return 'yes' }                  // violation
    if (check()) { return [1, 2] } else { return "count=$count" }       // violation

    if (condition)                                                      // violation
        return null
    else return [a:1]

    def method1() {
        if (condition) {                                                // violation
            return 44
        }
        return 'yes'
    }
```


## ImplicitClosureParameter Rule

*Since CodeNarc 1.5*

Checks for the implicit `it` closure parameter being used.
Also checks if an explicit `it` parameter has been specified.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowUsingItAsParameterName | To stop the rule reporting violations when an explicit closure parameter called `it` is used, set this property to `true`. | `false` |

Example of violations:

```
    def closureWithViolation = { it * 10 }
    def closureWithViolationBecauseOfExplicitItParameter = { it -* it * 10}
```


## ImplicitReturnStatement Rule

<Since CodeNarc 1.6>

Checks for methods that are missing an explicit `return` statement. 

This rule skips `void` methods and `def` (dynamic return type) methods, as well as methods whose last statement is a:
 - `throw`
 - `if`
 - `for`
 - `while`
 - `do .. while`
 - `switch`
 - `try/catch`

Example of violations:

```
    boolean example() { true }          // violation
     
    protected int longerExample() {
        if (baseName == null) {
            return 0
        }
        99                              // violation
    }
```

Note: This rule is pretty much the opposite of the [UnnecessaryReturnKeyword](./codenarc-rules-unnecessary.html#unnecessaryreturnkeyword-rule) rule.
Enabling both rules results in a paradox and may cause a rip in the fabric of *space-time*. Or at least unwanted violations.

## InvertedCondition Rule

*Since CodeNarc 1.1*

An inverted condition is one where a constant expression is used on the left hand side of the equals comparision.
Such conditions can be confusing especially when used in assertions where the expected value is by convention placed
on the right hand side of the comparision.

Example of violations:

```
    boolean isTenCharactersLong(String value) {
        10 == value.size()  // violation
    }
```


## InvertedIfElse Rule

*Since CodeNarc 0.11*

An inverted *if-else* statement is one in which there is a single `if` statement with a single
`else` branch and the boolean test of the `if` is negated. For instance `if (!x) false else true`.
It is usually clearer to write this as `if (x) true else false`.


## LongLiteralWithLowerCaseL Rule

*Since CodeNarc 0.16*

In Java and Groovy, you can specify long literals with the L or l character,
for instance 55L or 24l. It is best practice to always use an uppercase L and never
a lowercase l. This is because 11l rendered in some fonts may look like 111 instead of 11L.

Example of violations:

```
    def x = 1l
    def y = 55l
```


## MethodParameterTypeRequired Rule

*Since CodeNarc 1.1*

Checks that method parameters are not dynamically typed, that is they are explicitly stated and different than def.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreMethodNames           | Specifies one or more (comma-separated) method names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |

Example of violations:

```
    void methodWithDynamicParameter(def parameter) {              // violation
    }

    void methodWithParameterWithoutTypeDeclaration(parameter) {   // violation
    }

    void methodWithObjectParameter(Object parameter)              // OK
```


## MethodReturnTypeRequired Rule

*Since CodeNarc 1.1*

Checks that method return types are not dynamic, that is they are explicitly stated and different than def.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreMethodNames           | Specifies one or more (comma-separated) method names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |

Example of violations:

```
    def methodWithDynamicReturnType() {    // violation
    }

    private methodWithoutReturnType() {    // violation
    }

    Object objectReturningMethod() {       // OK
    }
```


## NoDef Rule


*Since CodeNarc 0.22*

Do not allow using the `def` keyword in code. Use a specific type instead.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| excludeRegex                | Regular expression matching the names of fields, variables, parameters or methods that can be preceded by the `def` keyword.     | '' |


## NoDouble Rule

*Since CodeNarc 1.5*

Checks for use of the `double` or `java.lang.Double` types, in fields, variables, method parameters, constructor parameters and method return types.
Prefer using BigDecimal or int or long, when exact calculations are required. This is due to the limitations and gotchas of the floating point representation
of the *double* type. This is especially important for monetary calculations.

Some related discussions include:

  * **Effective Java**, 2nd edition, by Joshua Bloch, Addison Wesley (2008). Item #48: *Avoid float and double if exact answers are required*.
  * [Why not use Double or Float to represent currency?](https://stackoverflow.com/questions/3730019/why-not-use-double-or-float-to-represent-currency)
  * [Why You Should Never Use Float and Double for Monetary Calculations](https://dzone.com/articles/never-use-float-and-double-for-monetary-calculatio).

Example of violations:

```
    class MyClass {
        int count
        double doubleProperty                               // Violation: Property (field) type
        private Double doubleField = 1.2                    // Violation: Field type

        private double calculateAverage() { return 0 }      // Violation: Method return type

        protected void setAverage(Double average) { }       // Violation: Method parameter type

        MyClass(int count, double rating, double factor) {  // Violation: Constructor parameter
            String name = 'abc'
            Double doubleVar = calculateAverage()           // Violation: Variable
            double double1, double2 = 0                     // Violation: Variable
        }
    }
```


## NoFloat Rule

*Since CodeNarc 1.5*

Checks for use of the `float` or `java.lang.Float` types, in fields, variables, method parameters, constructor parameters and method return types.
Prefer using BigDecimal or int or long, when exact calculations are required. This is due to the limitations and gotchas of the floating point representation
of the *float* type. This is especially important for monetary calculations.

Some related discussions include:

  * **Effective Java**, 2nd edition, by Joshua Bloch, Addison Wesley (2008). Item #48: *Avoid float and double if exact answers are required*.
  * [Why not use Double or Float to represent currency?](https://stackoverflow.com/questions/3730019/why-not-use-double-or-float-to-represent-currency)
  * [Why You Should Never Use Float and Double for Monetary Calculations](https://dzone.com/articles/never-use-float-and-double-for-monetary-calculatio).

Example of violations:

```
    class MyClass {
        int count
        float floatProperty                                 // Violation: Property (field) type
        private Float floatField = 1.2                      // Violation: Field type

        private float calculateAverage() { return 0 }       // Violation: Method return type

        protected void setAverage(Float average) { }        // Violation: Method parameter type

        MyClass(int count, float rating, float factor) {    // Violation: Constructor parameter
            String name = 'abc'
            Float floatVar = calculateAverage()             // Violation: Variable
            float float1, float2 = 0                        // Violation: Variable
        }
    }
```


## NoJavaUtilDate Rule

*Since CodeNarc 1.2*

Do not use the `java.util.Date` class. Prefer the classes in the java.time.* packages. This rule checks for
construction of new java.util.Date objects.

If the class imports another `Date` class, then references to `new Date()` will not cause a violation.

Example of violations:

```
    def timestamp = new Date()              // violation
    Date myDate = new java.util.Date()      // violation
    Date startTime = new Date(123456789L)   // violation
```


Known limitations:

  * Will cause an incorrect violation if the source code is referring to a different `Date` class from the current package. In that case, it may be better to just disable this rule (either per class or globally).


## NoTabCharacter Rule

*Since CodeNarc 0.25*

Checks that all source files do not contain the tab character.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).
The `@SuppressWarnings` annotation-based disablement is also unavailable, but including a `// codenarc-disable NoTabCharacter` comment
somewhere above the violation will disable this rule. See
[Disabling Rules From Comments](./codenarc-configuring-rules.html#disabling-rules-from-comments).


## ParameterReassignment Rule

*Since CodeNarc 0.17*

Checks for a method or closure parameter being reassigned to a new value within the body of the method/closure,
which is a confusing and questionable practice. Use a temporary variable instead.

Example of violations:

```
    void myMethod(int a, String b) {
        println a
        b = 'new value'     // violation
    }

    def myClosure1 = { int a, b -*
        a = 123             // violation
    }
```


## PublicMethodsBeforeNonPublicMethods Rule

*Since CodeNarc 1.2*

Enforce that all public methods are above protected and private methods.

Example of violations:

```
    class MyClass {
        public static int staticMethod1() { }

        protected String method1() { }

        static final String staticMethod2() { }     // violation
        public String method2() { }                 // violation

        private int method3(int id) { }
    }
```


## StaticFieldsBeforeInstanceFields Rule

*Since CodeNarc 1.2*

Enforce that all static fields are above all instance fields within a class

Example of violations:

```
    class MyClass {
        public static final int COUNT = 99

        public String f1

        public static final String F1 = "xxx"       // violation
        private static String F4                    // violation
        static F5 = new Date()                      // violation

        protected String f2
    }
```


## StaticMethodsBeforeInstanceMethods Rule

*Since CodeNarc 1.2*

Enforce that all static methods within each visibility level (public, protected, private) are above all
instance methods within that same visibility level. In other words, public static methods must be above
public instance methods, protected static methods must be above protected instance methods and private
static methods must be above private instance methods.

Example of violations:

```
        class MyClass {
            // Public
            public static int staticMethod1() { }
            public String method1() { }
            int method2() { }
            static final String staticMethod2(int id) { }       // violation

            // Protected
            protected String method3() { }
            protected static staticMethod3() { }                // violation

            // Private
            private int method4() { }
            private int method5() { }
            private static staticMethod4() { }                  // violation
            private String method5() { }
        }
    }
```


## TernaryCouldBeElvis Rule

*Since CodeNarc 0.17*

Checks for ternary expressions where the *boolean* and *true* expressions are the same.
These can be simplified to an *Elvis* expression.

Example of violations:

```
    x ? x : false               // violation; can simplify to x ?: false

    foo() ? foo() : bar()       // violation; can simplify to foo() ?: bar()
    foo(1) ? foo(1) : 123       // violation; can simplify to foo(1) ?: 123

    (x == y) ? same : diff      // OK
    x ? y : z                   // OK
    x ? x + 1 : x + 2           // OK
    x ? 1 : 0                   // OK
    x ? !x : x                  // OK
    !x ? x : null               // OK

    foo() ? bar() : 123         // OK
    foo() ? foo(99) : 123       // OK
    foo(x) ? foo() : 123        // OK
    foo(1) ? foo(2) : 123       // OK
```

NOTE: If the *boolean* and *true* expressions are the same method call, and that method
call has *side-effects*, then converting it to a *Elvis* expression may produce *different*
behavior. The method will only be called *once*, rather than *twice*. But relying on those
*side-effects* as part of a ternary expression behavior is confusing, error-prone and just a
bad idea. In any case, that code should be refactored to move the reliance on the side-effects
out of the ternary expression.

## TrailingComma Rule

*Since CodeNarc 0.25*

Check whether list and map literals contain optional trailing comma.
Rationale: Putting this comma in make is easier
to change the order of the elements or add new elements on the end.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| checkList                   | To disable checking List literals, set this property to `false` | `true` |
| checkMap                    | To disable checking Map literals, set this property to `false`  | `true` |
| ignoreSingleElementList     | If true, skip checking Lists that have only a single element.   | `true` |
| ignoreSingleElementMap      | If true, skip checking Maps that have only a single element.    | `true` |

This is valid code:

```
  int[] array1 = [] // one line declaration
  int[] array2 = [ // empty list
                 ]
  int[] array3 = [1,2,3] // one line declaration
  int[] array4 = [1,
                  2,
                  3, // contains trailing comma
                 ]
```

Example of violations:

```
  int[] array2 = [1,
                  2 // there is no trailing comma
                 ]
```


## VariableTypeRequired Rule

*Since CodeNarc 1.1*

Checks that variable types are explicitly specified in declarations (and not using `def`).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreVariableNames         | Specifies one or more (comma-separated) variable names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null`|

Example of violations:

```
    class MyClass {
        void doStuff() {
            final NAME = "joe"          // violation
            def count = 0, max = 99     // violation
            def defaultName             // violation
        }
    }
```


## VectorIsObsolete Rule

*Since CodeNarc 0.17*

Checks for references to the (*effectively*) obsolete `java.util.Vector` class.
Use the **Java Collections Framework** classes instead, including `ArrayList` or
`Collections.synchronizedList()`. See the JDK javadoc.

Example of violations:

```
    def myList = new Vector()           // violation
```


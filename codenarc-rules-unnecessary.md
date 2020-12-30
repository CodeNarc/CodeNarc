---
layout: default
title: CodeNarc - Unnecessary Rules
---  

# Unnecessary Rules  ("*rulesets/unnecessary.xml*")


## AddEmptyString Rule

*Since CodeNarc 0.13*

Finds empty string literals which are being added. This is an inefficient way to convert any type to a String.

Examples:

```
    // do not add empty strings to things
    def a = '' + 123
    def b = method('' + property)

    // these examples are OK and do not trigger violations
    def c = 456.toString()
    def d = property?.toString() ?: ""
```


## ConsecutiveLiteralAppends Rule

*Since CodeNarc 0.13*

Violations occur when method calls to append(Object) are chained together with literals as parameters. The
chained calls can be joined into one invocation.

Example of violations:

```
    writer.append('foo').append('bar')      // strings can be joined
    writer.append('foo').append(5)          // string and number can be joined
    writer.append('Hello').append("$World") // GString can be joined
```

Example of passing code:

```
    // usage not chained invocation
    writer.append('Hello')
    writer.append('World')

    writer.append(null).append(5)           // nulls cannot be joined

    writer.append().append('Hello')             // no arg append is unknown
    writer.append('a', 'b').append('Hello')     // two arg append is unknown
```


## ConsecutiveStringConcatenation Rule

*Since CodeNarc 0.13*

Catches concatenation of two string literals on the same line. These can safely by joined. In Java, the Java compiler
will join two String literals together and place them in the Constant Pool. However, Groovy will not because the plus()
method may override the + operator.

Examples:

```
    // Violations
    def a = 'Hello' + 'World'   // should be 'HelloWorld'
    def b = "$Hello" + 'World'  // should be "${Hello}World"
    def c = 'Hello' + "$World"  // should be "Hello${World}"
    def d = 'Hello' + 5         // should be 'Hello5'
    def e = 'Hello' + '''
                        world   // should be joined
                      '''
    def f = '''Hello
                  ''' + 'world'   // should be joined


    // Not Violations
    def g = 'Hello' +           // OK because of line break
                'World'
    def h = 'Hello' + null      // OK because not a string
    def i = 'Hello' + method()  // OK because not a string
    def j = 'Hello' - "$World"  // OK because not +
```


## UnnecessaryBigDecimalInstantiation Rule

*Since CodeNarc 0.12*

It is unnecessary to instantiate `BigDecimal` objects. Instead just use the decimal literal
or the 'G' identifier to force the type, such as `123.45` or `123.45G`.

This rule does not produce violations when the parameter evaluates to an integer/long, e.g.
`new BigDecimal(42)`, `new BigDecimal(42L)` or `new BigDecimal("42")`, because using the
"G" suffix on an integer value produces a `BigInteger`, rather than a `BigDecimal`, e.g. `45G`.
So that means there is no way to produce a `BigDecimal` with exactly that value using a literal.

This rule also does not produce violations when the parameter is a double, e.g. `new BigDecimal(12.3)`.
That scenario is covered by the [BigDecimalInstantiation](./codenarc-rules-basic.html#BigDecimalInstantiation)
rule, because that produces an unpredictable (double) value (and so it is *unsafe*, rather than *unnecessary*).


## UnnecessaryBigIntegerInstantiation Rule

*Since CodeNarc 0.12*

It is unnecessary to instantiate `BigInteger` objects. Instead just use the literal with
the 'G' identifier to force the type, such as `8G` or `42G`.


## UnnecessaryBooleanExpression Rule


Checks for unnecessary boolean expressions, including ANDing (&&) or ORing (||) with
`true`, `false`, `null`, or a Map/List/String/Number literal.

This rule also checks for negation (!) of `true`, `false`,
`null`, or a Map/List/String/Number literal.

Examples of violations include:

```
    result = value && true              // AND or OR with boolean constants
    if (false || value) { .. }
    return value && Boolean.FALSE

    result = null && value              // AND or OR with null

    result = value && "abc"             // AND or OR with String literal

    result = value && 123               // AND or OR with Number literal
    result = 678.123 || true

    result = value && [x, y]            // AND or OR with List literal

    result = [a:123] && value           // AND or OR with Map literal

    result = !true                      // Negation of boolean constants
    result = !false
    result = !Boolean.TRUE

    result = !null                      // Negation of null

    result = !"abc"                     // Negation of String literal

    result = ![a:123]                   // Negation of Map literal

    result = ![a,b]                     // Negation of List literal
```


## UnnecessaryBooleanInstantiation Rule

*Since CodeNarc 0.12 (formerly BooleanInstantiation Rule in the "basic" rule set)*

Checks for direct call to a `Boolean` constructor. Use `Boolean.valueOf()` or the `Boolean.TRUE`
and `Boolean.FALSE` constants instead of calling the `Boolean()` constructor directly.

Also checks for `Boolean.valueOf(true)` or `Boolean.valueOf(false)`. Use the `Boolean.TRUE`
or `Boolean.FALSE` constants instead.

Here is an example of code that produces a violation:

```
    def b1 = new Boolean(true)             // violation
    def b2 = new java.lang.Boolean(false)  // violation
    def b3 = Boolean.valueOf(true)         // violation
    def b4 = Boolean.valueOf(false)        // violation
```


## UnnecessaryCallForLastElement Rule

*Since CodeNarc 0.12*

This rule checks for excessively verbose methods of accessing the last element of an array or list. For
instance, it is possible to access the last element of an array by performing `array[array.length - 1]`,
in Groovy it is simpler to either call `array.last()` or `array[-1]`. The same is true for lists.
This violation is triggered whenever a `get`, `getAt`, or array-style access is used with an object
size check.

Code like this all cause violations.

```
    def x = [0, 1, 2]
    def a = x.get(x.size() -1)
    def b = x.get(x.length -1)
    def c = x.getAt(x.size() -1)
    def d = x.getAt(x.length -1)
    def f = x[(x.size() -1]
    def d = x[(x.length -1]
```

All of this code is fine though:

```
    def x = [0, 1, 2]
    def a = x.last()
    def b = x[-1]
    def c = x.getAt(-1)
    def d = x.get(z.size() -1)     // different objects
    def e = x.get(z.length -1)     // different objects
    def f = x.getAt(z.size() -1)   // different objects
```


## UnnecessaryCallToSubstring Rule

*Since CodeNarc 0.13*

Calling String.substring(0) always returns the original string. This code is meaningless.

Examples:

```
    string.substring(0)         // violation
    method().substring(0)       // violation

    prop.substring(1)           // OK, not constant 0
    prop.substring(0, 1)        // OK, end is specified
```


## UnnecessaryCast Rule

*Since CodeNarc 0.21*

Checks for unnecessary cast operations.

Example of violations:

```
    int count = (int)123                    // violation
    def longValue = (long)123456L           // violation
    def bigDecimal = (BigDecimal)1234.56    // violation
    String name = (String) "Joe"            // violation
    def list = (List)[1, 2, 3]              // violation
    def map = (Map)[a:1]                    // violation
```


## UnnecessaryCatchBlock Rule

*Since CodeNarc 0.12*

Violations are triggered when a *catch* block does nothing but throw the original exception. In this scenario
there is usually no need for a *catch* block, just let the exception be thrown from the original code. This
condition frequently occurs when catching an exception for debugging purposes but then forgetting to take the
`catch` statement out.


## UnnecessaryCollectCall Rule

*Since CodeNarc 0.12*

Some method calls to `Object.collect(Closure)` can be replaced with the spread operator. For instance,
`list.collect { it.multiply(2) }` can be replaced by `list*.multiply(2)`.

Examples of violations include:

```
    assert [1, 2, 3].collect { it.multiply(2) }
    assert [1, 2, 3].collect { x -* x.multiply(2) }
    ["1", "2", "3"].collect { it.bytes }
```

The following code does not produce violations:

```
    [1, 2, 3].collect { it * it }   // OK, closure parameter is referenced twice

    [1, 2, 3].mapMethod { it.multiply(5) } // OK, method call is not collect

    [1, 2, 3].collect(5) // OK, collect parameter is not a closure

    // OK, the closure is not a simple one line statement
    [1, 2, 3].collect { println it; it.multiply(5) }

    // OK, closure has too many arguments
    [1, 2, 3].collect { a, b -* a.multiply(b) }

    // OK, closure statement references parameter multiple times
    [1, 2, 3].collect { it.multiply(it) }

    // OK, it is referenced several times in the closure
    [1, 2, 3].collect { it.multiply(2).multiply(it) }
    ["1", "2", "3"].collect { it.bytes.foo(it) }

    // OK, chained methods are too complex to analyze at this point
    [1, 2, 3].collect { it.multiply(2).multiply(4) }

    // in general the above examples can be rewritten like this:
    [1, 2, 3]*.multiply(2)
    ["1", "2", "3"]*.bytes
```


## UnnecessaryCollectionCall Rule

*Since CodeNarc 0.11*

Checks for useless calls to collections. For any collection `c`, calling `c.containsAll(c)`
should always be `true`, and `c.retainAll(c)` should have no effect.


## UnnecessaryConstructor Rule

*Since CodeNarc 0.11*

This rule detects when a constructor is not necessary; i.e., when there's only one constructor, it's
`public`, has an empty body, and takes no arguments, or else contains only a single call to `super()`.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreAnnotations | If `true`, then do not report violations if a constructor has one or more annotations. | `false` |

Example of violations:

```
    class MyClass {
        public MyClass() {          // violation; constructor is not necessary
        }
    }

    class MyClass2 extends OtherClass {
        MyClass2() {                // violation; constructor is not necessary
            super()
        }
    }
```


## UnnecessaryDefInFieldDeclaration Rule

*Since CodeNarc 0.16*

If a field has a visibility modifier or a type declaration, then the def keyword is unneeded. For instance, 'static def constraints = {}' is redundant and can be simplified to 'static constraints = {}.

Example of violations:

```
    class MyClass {
        // def is redundant
        static def constraints = {  }

        // def and private is redundant
        def private field1 = { }

        // def and protected is redundant
        def protected field2 = { }

        // def and public is redundant
        def public field3 = { }

        // def and static is redundant
        def static field4 = { }

        // def and type is redundant
        def Object field5 = { }
    }
```

## UnnecessaryDefInMethodDeclaration Rule

*Since CodeNarc 0.13*

If a method has a visibility modifier or a type declaration, then the def keyword is unneeded.
For instance 'def private method() {}' is redundant and can be simplified to 'private method() {}'.

Examples of violations:

```
    // def and private is redundant
    def private method1() { return 4 }

    // def and protected is redundant
    def protected method2() { return 4 }

    // def and public is redundant
    def public method3() { return 4 }

    // def and static is redundant
    def static method4() { return 4 }

    // def and type is redundant
    def Object method5() { return 4 }

    class MyClass {
        def MyClass() {}    // def is redundant
    }
```


## UnnecessaryDefInVariableDeclaration Rule

*Since CodeNarc 0.15*

If a variable has a visibility modifier or a type declaration, then the def keyword is unneeded.
For instance 'def private n = 2' is redundant and can be simplified to 'private n = 2'.

Examples of violations:

```
    // def and private is redundant
    def private string1 = 'example'

    // def and protected is redundant
    def protected string2 = 'example'

    // def and public is redundant
    def public string3 = 'example'

    // def and static is redundant
    def static string4 = 'example'

    // def and final is redundant
    def final string5 = 'example'

    // def and a type is redundant
    def String string6 = 'example'
```

## UnnecessaryDotClass Rule

*Since CodeNarc 0.15*

To make a reference to a class, it is unnecessary to specify the '.class' identifier. For instance String.class can be shortened to String.

Example of violations:

```
    // The '.class' identifier is unnecessary, violation occurs
    def x = String.class

    // Ok, unnecessary '.class' identifier has been excluded
    def x = String
```

## UnnecessaryDoubleInstantiation Rule

*Since CodeNarc 0.12*

It is unnecessary to instantiate `Double` objects. Instead just use the double literal
with 'D' identifier to force the type, such as `123.45d` or `0.42d`.


## UnnecessaryElseStatement Rule

*Since CodeNarc 0.14*

When an `if` statement block ends with a `return` statement, then the `else` is unnecessary.
The logic in the `else` branch can be run without being in a new scope.

Example of violations:

```
    if(value){
        println 'Executing if logic...'
        return true
    } else {
        println 'Executing else logic...'
    }

    // can be replaced by:

    if(value){
        println 'Executing if logic...'
        return true
    }
    println 'Executing else logic...'
```


## UnnecessaryFinalOnPrivateMethod Rule

*Since CodeNarc 0.14*

A private method is marked final. Private methods cannot be overridden, so marking it final is unnecessary.

Example of violations:

```
    private final method() {}
```


## UnnecessaryFloatInstantiation Rule

*Since CodeNarc 0.12*

It is unnecessary to instantiate `Float` objects. Instead just use the float literal
with the 'F' identifier to force the type, such as `123.45F` or `0.42f`.


## UnnecessaryGetter Rule

*Since CodeNarc 0.12*

Checks for explicit calls to getter/accessor methods which can, for the most part, be replaced by property access.
A getter is defined as a no-argument method call that matches `get[A-Z]` but not `getClass()` or
`get[A-Z][A-Z]` such as `getURL()`.

Calls to getter methods within Spock method calls `Mock()`, `Stub()` and `Spy()` are ignored.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| checkIsMethods              | If `true`, then also check isXxx() getters methods. | `true` |
| ignoreMethodNames           | Specifies one or more (comma-separated) method names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |

These bits of code produce violations:

```
    x.getProperty()
    x.getFirst()
    x.getFirstName()
    x.getA()

    x.isFirst()         // Violation if checkIsMethods is true
    x.isA()             // Violation if checkIsMethods is true
```

These bits of code do not:

```
    x.property
    x.first
    x.firstName
    x.a
    x.getURL()
    x.getClass()
    x.getProperty('key')
```


## UnnecessaryGString Rule

*Since CodeNarc 0.13*

String objects should be created with single quotes, and GString objects created with double quotes.
Creating normal String objects with double quotes is confusing to readers.

Example of violations:

```
    def a = "I am a string"     // violation

    // violation
    def b = """
        I am a string
    """

    def c = "I am a ' string"       // OK

    def d = """I am a ' string"""   // OK

    def e = """I am a ' string"""   // OK

    def f = "I am a \$ string"  // OK

    // OK
    def g = """
        I am a \$ string
    """

    // OK
    def h = """
        I am a $string
    """

    def i = 'i am a string'
    def j = '''i am a
        string
    '''
```


## UnnecessaryIfStatement Rule

Checks for unnecessary **if** statements. The entire **if** statement, or at least the *if* or *else*
block, are considered unnecessary for the four scenarios described below.

(1) When the *if* and *else* blocks contain only an explicit return of `true` and `false`
constants. These cases can be replaced by a simple *return* statement. Examples of violations include:

```
    if (someExpression)         // can be replaced by: return someExpression
        return true
    else
        return false

    if (someExpression) {       // can be replaced by: return !someExpression
        return false
    } else {
        return true
    }

    if (someExpression) {       // can be replaced by: return someExpression
        return Boolean.TRUE
    } else {
        return Boolean.FALSE
    }
```

(2) When the `if` statement is the last statement in a block and the *if* and *else* blocks
are only `true` and `false` expressions. This is an *implicit* return of `true`/`false`.
For example, the `if` statement in the following code can be replaced by `someExpression`
or `someExpression as boolean`:

```
    def myMethod() {
        doSomething()
        if (someExpression)
            true
        else false
    }
```

(3) When the second-to-last statement in a block is an `if` statement with no `else`, where the block
contains a single `return` statement, and the last statement in the block is a `return` statement, and
one `return` statement returns a `true` expression and the other returns a `false` expression.
This check is disabled by setting `checkLastStatementImplicitElse` to `false`.
For example, the `if` statement in the following code can be replaced by `return expression1`:

```
    def myMethod() {
        doSomething()
        if (expression1) {
            return true
        }
        return false
    }
```

(4) When either the *if* block or *else* block of an `if` statement that is not the last statement
in a block contain only a single constant or literal expression. For example, the `if` statement
in the following code has no effect and can be removed:

```
    def myMethod() {
        if (someExpression) { 123 }
        doSomething()
    }
```


## UnnecessaryInstanceOfCheck Rule

*Since CodeNarc 0.15*

This rule finds instanceof checks that cannot possibly evaluate to true. For instance, checking that
`(!variable instanceof String)` will never be true because the result of a not expression is always a boolean.

Example of violations:

```
    if (!variable instanceof String) { ... }    // always false
    def x = !variable instanceof String         // always false

    if (!variable instanceof Boolean) { ... }    // always true
    def x = !variable instanceof Boolean         // always true

    // this code is OK
    if (!(variable instanceof String)) { ... }
```

## UnnecessaryInstantiationToGetClass Rule

*Since in CodeNarc 0.12*

Avoid instantiating an object just to call getClass() on it; use the .class public member instead.

```
    public class Foo {
     // Replace this
     Class c = new String().getClass();

     // with this:
     Class c = String.class;
    }
```


## UnnecessaryIntegerInstantiation Rule

*Since CodeNarc 0.12*

It is unnecessary to instantiate `Integer` objects. Instead just use the literal with
the 'I' identifier to force the type, such as `8I` or `42i`.


## UnnecessaryLongInstantiation Rule

*Since CodeNarc 0.12*

It is unnecessary to instantiate `Long` objects. Instead just use the literal with
the 'L' identifier to force the type, such as `8L` or `42L`.


## UnnecessaryModOne Rule

*Since CodeNarc 0.13*

Any expression mod 1 (exp % 1) is guaranteed to always return zero. This code is probably an error, and should be either (exp & 1) or (exp % 2).

Examples:

```
    if (exp % 1) {}         // violation
    if (method() % 1) {}    // violation

    if (exp & 1) {}     // ok
    if (exp % 2) {}     // ok
```


## UnnecessaryObjectReferences Rule

*Since CodeNarc 0.12*

Violations are triggered when an excessive set of consecutive statements all reference the same variable.
This can be made more readable by using a `with` or `identity` block. By default, 5 references
are allowed. You can override this property using the **maxReferencesAllowed` property on the rule.

These two bits of code produce violations:

```
    def p1 = new Person()
    p1.firstName = 'Hamlet'
    p1.lastName = "D'Arcy"
    p1.employer = 'Canoo'
    p1.street = 'Kirschgaraten 5'
    p1.city = 'Basel'
    p1.zipCode = '4051'

    def p2 = new Person()
    p2.setFirstName('Hamlet')
    p2.setLastName("D'Arcy")
    p2.setEmployer('Canoo')
    p2.setStreet('Kirschgaraten 5')
    p2.setCity('Basel')
    p2.setZipCode('4051')
```

However, these two bits of code do not because they use either a `with` or `identity` block.

```
    def p1 = new Person().with {
        firstName = 'Hamlet'
        lastName = "D'Arcy"
        employer = 'Canoo'
        street = 'Kirschgaraten 5'
        city = 'Basel'
        zipCode = '4051'
    }

    def p2 = new Person().identity {
        firstName = 'Hamlet'
        lastName = "D'Arcy"
        employer = 'Canoo'
        street = 'Kirschgaraten 5'
        city = 'Basel'
        zipCode = '4051'
    }
```


## UnnecessaryNullCheck Rule

*Since CodeNarc 0.12*

Groovy contains the safe dereference operator. It can be used in boolean conditional statements to safely
replace explicit `x == null` tests. Also, testing the 'this' or 'super' reference for null equality is
pointless and can be removed.

Examples of violations:

```
    if (obj != null && obj.method()) { }

    if (obj != null && obj.prop) { }

    // this is pointless and won't avoid NullPointerException
    if (obj.method() && obj != null ) { }

    if (this == null) { }
    if (null == this) { }
    if (this != null) { }
    if (null != this) { }

    if (super == null) { }
    if (null == super) { }
    if (super != null) { }
    if (null != super) { }
```

Examples of acceptable code:

```
    // null check it OK
    if (obj != null) { }

    // null safe dereference in if is OK
    if (obj?.method()) { }

    // null safe dereference in ternary is OK
    (obj?.prop && obj?.prop2) ? x : y

    // obj is reused in a parameter list, so OK
    if (obj != null && obj.method() && isValid(obj)) { }

    // rule is not so complex yet...
    (obj != null && obj.prop && obj.method()) ? x : y
```


## UnnecessaryNullCheckBeforeInstanceOf Rule

*Since CodeNarc 0.12*

There is no need to check for null before an instanceof; the instanceof keyword returns false when given a null argument.

Example:

```
    if (x != null && x instanceof MyClass) {
        // should drop the "x != null" check
    }

    if (x instanceof MyClass && x != null) {
        // should drop the "x != null" check
    }

    // should drop the "x != null" check
    (x != null && x instanceof MyClass) ? foo : bar

    if (x != null && x instanceof MyClass && x.isValid()) {
        // this is OK and causes no violation because the x.isValid() requires a non null reference
    }
```


## UnnecessaryOverridingMethod Rule

*Since CodeNarc 0.11*

Checks for an overriding method that merely calls the same method defined in a superclass. Remove it.


## UnnecessaryPackageReference Rule

*Since CodeNarc 0.14*

Checks for explicit package reference for classes that Groovy imports by default, such as `java.lang.String`,
`java.util.Map` and `groovy.lang.Closure`, as well as classes that were explicitly imported.

You do not need to specify the package for any classes from *java.lang*, *java.util*, *java.io*, *java.net*,
*groovy.lang* and *groovy.util*, as well as the classes *java.math.BigDecimal* and *java.math.BigInteger*.

Examples of violations include:

```
    // Field types
    class MyClass {
        java.math.BigDecimal amount = 42.10                     // violation
    }

    // Within expressions
    if (value.class == java.math.BigInteger) { }                // violation
    println "isClosure=${v instanceof groovy.lang.Closure}"     // violation
    def p = java.lang.Runtime.availableProcessors()             // violation

    // Constructor calls
    def url = new java.net.URL('http://abc@example.com')        // violation

    // Variable types
    void doSomething() {
        java.math.BigInteger maxValue = 0                       // violation
        java.net.URI uri                                        // violation
    }

    // Method return types
    java.io.Reader getReader() { }                              // violation
    groovy.util.AntBuilder getAntBuilder() { }                  // violation

    // Method parameter types
    void writeCount(java.io.Writer writer, int count) { }       // violation
    void init(String name, groovy.lang.Binding binding) { }     // violation

    // Closure parameter types
    def writeCount = { java.io.Writer writer, int count -* }    // violation

    // Extends and implements
    class MyHashMap extends java.util.HashMap { }               // violation
    class MyList implements java.util.List { }                  // violation

    // Explicitly imported classes
    import javax.servlet.http.Cookie
    import javax.sql.DataSource

    class MyClass {
        void doStuff(javax.servlet.http.Cookie cookie) {        // violation
            def dataSource = [:] as javax.sql.DataSource        // violation
        }
    }
```

Known limitations:

  * Does not catch class declarations that explicitly extend `java.lang.Object`. For instance,
    `class MyClass extends java.lang.Object { }`. Just don't do that, okay?

  * Does not catch class declarations that explicitly extend `groovy.lang.Script`. For instance,
    `class MyScript extends groovy.lang.Script{ }`. Don't do that, either!

  * Does not catch unnecessary package references if they are the types of anonymous inner class
    definitions, for older versions of Groovy (* 1.7.10?). For instance,
    `def runnable = new java.lang.Runnable() { ... }`.


## UnnecessaryParenthesesForMethodCallWithClosure Rule

*Since CodeNarc 0.14*

If a method is called and the only parameter to that method is an inline closure then the parentheses of the method call can be omitted.

Example of violations:

```
    [1,2,3].each() { println it }
```


## UnnecessaryPublicModifier Rule

*Since CodeNarc 0.13*

The 'public' modifier is not required on methods, constructors or classes.

Because of Groovy parsing limitations, this rule ignores methods (and constructors) that include Generic types in the method declaration.

Example of violations:

```
    // violation on class
    public class MyClass {
        // violation on constructor
        public MyClass() {}

        // violation on method
        public void myMethod() {}
    }
```


## UnnecessaryReturnKeyword Rule

*Since CodeNarc 0.11*

In Groovy, the `return` keyword is often optional. If a statement is the last line in a method or
closure then you do not need to have the `return` keyword.

Note: This rule is pretty much the opposite of the [ImplicitReturnStatement](./codenarc-rules-convention.html#implicitreturnstatement-rule) rule.
You don't want to enable both rules.
Some describe this as the *CodeNarc Heisenberg Principle*: You can either have the
**ImplicitReturnStatement** rule enabled or **UnnecessaryReturnKeyword**, but not both.


## UnnecessarySafeNavigationOperator Rule


*Since CodeNarc 0.22*

Check for the *safe navigation* operator (`?.`) applied to constants and literals, or `this`
or `super`, or constructor calls, all of which can never be null.

Example of violations:

```
    def myMethod() {
        "abc"?.bytes            // violation
        [1,2]?.getSize()        // violation
        [abc:123]?.name         // violation
        [:]?.toString()         // violation
        123?.class              // violation
        123.45?.getClass()      // violation
        Boolean.FALSE?.class    // violation
        Boolean.TRUE?.class     // violation
        this?.class             // violation
        super?.getClass()       // violation
        new Long(100)?.class    // violation
    }
```


## UnnecessarySelfAssignment Rule

*Since CodeNarc 0.13*

Method contains a pointless self-assignment to a variable or property. Either the code is pointless or the equals()/get()
method has been overridden to have a side effect, which is a terrible way to code getters and violates the contract of
equals().

Examples:

```
    x = x               // violation
    def method(y) {
        y = y           // violation
    }
    a.b.c = a.b.c       // violation

    x = y               // acceptable
    a.b = a.zz          // acceptable
    a.b = a().b         // acceptable
```


## UnnecessarySemicolon Rule

*Since CodeNarc 0.13*

Semicolons as line terminators are not required in Groovy: remove them. Do not use a semicolon as a replacement for
empty braces on for and while loops; this is a confusing practice.

The rule contains a String property called 'excludePattern'. Any source code line matching this pattern will not
trigger a violation. The default value is '\\s?\\*.*|/\\*.*|.*//.*|.*\\*/.*' This is to filter out comments.
Any source line that even looks like it is a comment is ignored.

 - `\s?\*.*`  ==  whitespace plus star character plus anything
 - `/\*.*`    == any line that contains the /* sequence
 - `.*//.*`   == any line that contains the // sequence
 - `.*\*/.*`  == any line that contains the */ sequence

Example of violations:

```
    package my.company.server;  // violation

    import java.lang.String;    // violation

    println(value) ;             // violation

    for (def x : list);         // violation

    // this code is OK
    println(value); println (otherValue)
```

Known limitations:
 - Will not flag a semicolon on a field declaration with no initial value specified when running on Groovy 3.x.


## UnnecessarySetter Rule

*Since CodeNarc 1.0*

Checks for explicit calls to setter methods which can, for the most part, be replaced by assignment to property.
A setter is defined as a method call that matches set[A-Z] but not set[A-Z][A-Z] such as setURL().
Setters take one method argument. Setter calls within an expression are ignored.

These bits of code produce violations:

```
  x.setProperty(1)
  x.setProperty(this.getA())
  x.setProperty([])
```

These bits of code do not:

```
  x.set(1)                              // Nothing after "set"
  x.setup(2)                            // The letter after "set" must be capitalized
  x.setURL('')                          // But setters with multiple capital letters after "set" are ignored
  x.setSomething('arg1', 'arg2')        // Setter must have exactly one argument
  if (!file.setExecutable(true)) { }    // Set method called within expression
  def count = x.setCount(92)            // Set method called within expression
```

## UnnecessarySubstring Rule (DEPRECATED)

*Since CodeNarc 0.15*

NOTE: This rule is *deprecated* and will be removed in a future CodeNarc version. Its recommendation to use subscripts on 
strings is not always safe/valid. See [#562](https://github.com/CodeNarc/CodeNarc/issues/562).

This rule finds usages of `String.substring(int)` and `String.substring(int, int)` that can be replaced by use of the
subscript operator. For instance, `var.substring(5)` can be replaced with `var[5..-1]`.

Note that the String.substring(beginIndex,endIndex) method specifies a range of beginIndex..endIndex-1, while
Groovy's String subscript specifies an inclusive range. So, `"123456".substring(1, 5)` is equivalent to `"123456"[1..4]`.

Example of violations:

```
    myVar.substring(5)          // can use myVar[5..-1] instead
    myVar.substring(1, 5)       // can use myVar[1..4] instead
```

## UnnecessaryStringInstantiation Rule

*Since CodeNarc 0.12 (formerly StringInstantiation Rule in the "basic" rule set)*

Checks for direct call to the `String` constructor that accepts a `String` literal.
In almost all cases, this is unnecessary. Use a `String` literal (e.g., "...") instead of calling the
corresponding `String` constructor (`new String("..")`) directly.

Here is an example of code that produces a violation:

```
    def s = new String('abc')
```


## UnnecessaryTernaryExpression Rule


Checks for ternary expressions where the conditional expression always evaluates to a boolean
and the *true* and *false* expressions are merely returning `true` and `false` constants.
These cases can be replaced by a simple boolean expression. Examples of violations include:

```
    x==99 ? true : false                    // can be replaced by: x==99
    x && y ? true : false                   // can be replaced by: x && y
    x||y ? false : true                     // can be replaced by: !(x||y)
    x *= 1 ? true: false                    // can be replaced by: x *= 1
    x * 99 ? Boolean.TRUE : Boolean.FALSE   // can be replaced by: x * 99
    !x ? true : false                       // can be replaced by: !x
```

The rule also checks for ternary expressions where the *true* and *false* expressions are
the same constant or variable. Examples include:

```
    x ? '123' : '123'              // can be replaced by: '123'
    x ? null : null                // can be replaced by: null
    x ? 23 : 23                    // can be replaced by: 23
    x ? MAX_VALUE : MAX_VALUE      // can be replaced by: MAX_VALUE
    ready ? minValue : minValue    // can be replaced by: minValue
```


## UnnecessaryTransientModifier Rule

*Since CodeNarc 0.13*

The field is marked as transient, but the class isn't Serializable, so marking it as transient has no effect.
This may be leftover marking from a previous version of the code in which the class was
transient, or it may indicate a misunderstanding of how serialization works.

Some Java frameworks change the semantics of the transient keyword. For instance, when using Terracotta
the transient keyword may have slightly different semantics. You may need to turn this rule off depending
on which Java frameworks are in use.

Examples:

```
    class MyClass {
        // class not serializable, violation occurs
        transient String property
    }

    class MySerializableClass implements Serializable {
        // OK, class is serializable
        transient String property
    }
```


## UnnecessaryToString Rule

*Since CodeNarc 0.21*

Checks for unnecessary calls to `toString()`. This includes:

  * Calls to `toString()` on a String literal or expression

  * Calls to `toString()` for the value assigned to a `String` field or variable (if *checkAssignments* is `true`).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| checkAssignments | If `true`, then check for calls to `toString()` for the value assigned to a `String` field or variable. | `true` |

Example of violations:

```
    def name = "Joe".toString()                             // violation - string literal
    def groupId = ((String)row.get('GroupID')).toString()   // violation - string expression

    class MyClass {
        String name = nameNode.toString()           // violation - field
        String code = account.getCode().toString()  // violation - field

        def name = "Joe" + new Date().toString()    // violation - adding object to String

        void run() {
            String name = nameNode.toString()       // violation - variable
            String id = account.id.toString()       // violation - variable

            def string = "processing ${123L.toString()} or ${new Date().toString()}"    // 2 violations - GString value
        }
    }
```


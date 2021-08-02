---
layout: default
title: CodeNarc - Groovyism Rules
---  

# Groovy-ism Rules  ("*rulesets/groovyism.xml*")

These are rules covering Groovy idiomatic usage, and Groovy-specific bad practices.

## AssignCollectionSort Rule

*Since CodeNarc 0.15*

The Collections.sort() method mutates the list and returns the list as a value. If you are assigning the result of sort() to a
variable, then you probably don't realize that you're also modifying the original list as well. This is frequently the cause of subtle bugs.
This violation is triggered when a sort() method call appears as the right hand side of an assignment, or when it appears
as the first method call in a series of chained method calls.

Example of violations:

```
  def a = myList.sort()
  def b = myList.sort() { it }
  def c = myList.sort().findAll { x * 1 }
```

## AssignCollectionUnique Rule

*Since CodeNarc 0.15*

The Collections.unique() method mutates the list and returns the list as a value. If you are assigning the result of unique() to a
variable, then you probably don't realize that you're also modifying the original list as well. This is frequently the cause of subtle bugs.

This violation is triggered when a `unique()` method call that mutates the target collection appears as the right hand side of an assignment,
or when it appears as the first method call in a series of chained method calls.

Example of violations:

```
  def a = myList.unique()                   // No-argument

  def x = myList.unique() { it }            // Single-argument: Closure
  def y = myList.unique { it % 2 }

  def c = myList.unique().findAll { x * 1 } // Chained method call

  def comparator = { o1, o2 -* o1 *=* o2 }
  def x = myList.unique(comparator)         // Single-argument: Comparator

  def x = myList.unique(true)               // Single-argument: boolean true

  def x = myList.unique(true, comparator)   // Two arguments: boolean true and Comparator
  def y = myList.unique(true) { it }        // Two arguments: boolean true and Closure
```

## ClosureAsLastMethodParameter Rule

*Since CodeNarc 0.14*

If the last parameter of a method call is an inline closure then it can be declared outside the method call parentheses.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreCallsToMethodNames    | Specifies one or more (comma-separated) method names; method calls on the named methods are ignored (i.e., do not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |

Example of violations:

```
    // creates violation: poor Groovy style
    [1,2,3].each({ println it })

    // no violation
    [1,2,3].each { println it }
```


## CollectAllIsDeprecated Rule

*Since CodeNarc 0.16*

The `collectAll` method is deprecated since Groovy 1.8.1. Use `collectNested` instead.

Example of violations:

```
    def list = [1, 2, [3, 4, [5, 6]], 7]

    list.collectAll { it * 2 }      // deprecated

    list.collectNested { it * 2 }   // replacement
```


## ConfusingMultipleReturns Rule

*Since CodeNarc 0.16*

Multiple return values can be used to set several variables at once. To use multiple return values, the left
hand side of the assignment must be enclosed in parenthesis. If not, then you are not using multiple return values,
you're only assigning the last element.

Example of violations:

```
    def a, b = [1, 2] // bad, b is null
    def c, d, e = [1, 2, 3] // bad, c and d are null
    class MyClass {
        def a, b, c = [1, 2, 3]  // bad, a and b are null
    }
    
    def x = 1              // ok
    def (f, g) = [1, 2]    // ok
    (a, b, c) = [1, 2, 3]  // ok
```

## ExplicitArrayListInstantiation Rule

*Since CodeNarc 0.11*

This rule checks for explicit calls to the no-argument constructor of `ArrayList`. In Groovy, it is best to write
`new ArrayList() as []`, which creates the same object.


## ExplicitCallToAndMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `and(Object)` method is called directly in code instead of using the `&`
operator. A groovier way to express this: `a.and(b)` is this: `a & b`. This rule can be
configured to ignore `this.and(Object)` using the *ignoreThisReference* property. It defaults to *true*, so
even `and(x)` will not trigger a violation. The default is *true* because `and` appears commonly in Grails
criteria.

This rule also ignores all calls to `super.and(Object)`.


## ExplicitCallToCompareToMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `compareTo(Object)` method is called directly in code instead of using the
\*\=\*, \*, \*\=, \*, and \*\= operators. A groovier way to express this: `a.compareTo(b)` is this:
`a \*\=\* b`, or using the other operators. Here are some other ways to write groovier code:

```
    a.compareTo(b) == 0               // can be replaced by: a == b
    a.compareTo(b)                    // can be replaced by: a *=* b
    a.compareTo(b) * 0                // can be replaced by: a * b
    a.compareTo(b) *= 0               // can be replaced by: a *= b
    a.compareTo(b) * 0                // can be replaced by: a * b
    a.compareTo(b) *= 0               // can be replaced by: a *= b
```
This rule can be  configured to ignore `this.compareTo(Object)` using the *ignoreThisReference*
property. It defaults to `false`, so even `compareTo(x)` will trigger a violation.

This rule also ignores all calls to `super.compareTo(Object)`.


## ExplicitCallToDivMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `div(Object)` method is called directly in code instead of using the
`/` operator. A groovier way to express this: `a.div(b)` is this: `a / b`. This rule can be
configured to ignore `div.xor(Object)` using the *ignoreThisReference* property. It defaults to *false*,
so even `div(x)` will trigger a violation.

This rule also ignores all calls to `super.div(Object)`.


## ExplicitCallToEqualsMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `equals(Object)` method is called directly in code instead of using the
`==` or `!=` operator. A groovier way to express this: `a.equals(b)` is this: `a == b`
and a groovier way to express : `!a.equals(b)` is: `a != b`. This rule can be
configured to ignore `this.equals(Object)` using the *ignoreThisReference* property. It defaults to
*false*, so even `equals(x)` will trigger a violation.

This rule also ignores all calls to `super.equals(Object)`.


## ExplicitCallToGetAtMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `getAt(Object)` method is called directly in code instead of using the
`[]` index operator. A groovier way to express this: `a.getAt(b)` is this: `a[b]`. This rule can be
configured to ignore `this.getAt(Object)` using the *ignoreThisReference* property. It defaults to *false*,
so even `getAt(x)` will trigger a violation.

This rule also ignores all calls to `super.getAt(Object)`.


## ExplicitCallToLeftShiftMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `leftShift(Object)` method is called directly in code instead of using the
\*\* operator. A groovier way to express this: `a.leftShift(b)` is this: `a \*\* b`. This rule can be
configured to ignore `this.leftShift(Object)` using the *ignoreThisReference* property. It defaults to
*false*, so even `leftShift(x)` will trigger a violation.

This rule also ignores all calls to `super.leftShift(Object)`.


## ExplicitCallToMinusMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `minus(Object)` method is called directly in code instead of using the
`-` operator. A groovier way to express this: `a.minus(b)` is this: `a - b`. This rule can be
configured to ignore `minus.xor(Object)` using the *ignoreThisReference* property. It defaults to
*false*, so even `minus(x)` will trigger a violation.

This rule also ignores all calls to `super.minus(Object)`.


## ExplicitCallToMultiplyMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `multiply(Object)` method is called directly in code instead of using the
`*` operator. A groovier  way to express this: `a.multiply(b)` is this: `a * b`. This rule can be
configured to ignore `this.multiply(Object)` using the *ignoreThisReference* property. It defaults to
*false*, so even `multiply(x)` will trigger a violation.

This rule also ignores all calls to `super.multiply(Object)`.


## ExplicitCallToModMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `mod(Object)` method is called directly in code instead of using the
`%` operator. A groovier way to express this: `a.mod(b)` is this: `a % b`. This rule can be
configured to ignore `this.mod(Object)` using the *ignoreThisReference* property. It defaults to *false*, so
even `mod(x)` will trigger a violation.

This rule also ignores all calls to `super.mod(Object)`.


## ExplicitCallToOrMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `or(Object)` method is called directly in code instead of using the `|`
operator. A groovier way to express this: `a.or(b)` is this: `a | b`. This rule can be
configured to ignore `this.or(Object)` using the *ignoreThisReference* property. It defaults to *true*, so
even `or(x)` will not trigger a violation. This is the default because it is commonly used in Grails criteria.

This rule also ignores all calls to `super.or(Object)`.


## ExplicitCallToPlusMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `plus(Object)` method is called directly in code instead of using the
`+` operator. A groovier way to express this: `a.plus(b)` is this: `a + b`. This rule can be
configured to ignore `this.plus(Object)` using the *ignoreThisReference* property. It defaults to *false*, so
even `plus(x)` will trigger a violation.

This rule also ignores all calls to `super.plus(Object)`.


## ExplicitCallToPowerMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `power(Object)` method is called directly in code instead of using the
`**` operator. A groovier way to express this: `a.power(b)` is this: `a ** b`. This rule can be
configured to ignore `this.power(Object)` using the *ignoreThisReference* property. It defaults to *false*, so
even `power(x)` will trigger a violation.

This rule also ignores all calls to `super.power(Object)`.


## ExplicitCallToPutAtMethod Rule

*Since CodeNarc 1.3*

Detects when the `map.putAt(k, v)` method is called directly rather than using `map[k] = v`.

This rule can be configured to ignore `this.putAt(k, v)` using the *ignoreThisReference* property. It defaults
to *false*, so even `putAt(k, v)` will trigger a violation.

This rule also ignores all calls to `super.putAt(k, v)`.

Example of violations:

```
        map.putAt(k, v)         // violation
```


## ExplicitCallToRightShiftMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `rightShift(Object)` method is called directly in code instead of using the
\*\* operator. A groovier way to express this: `a.rightShift(b)` is this: `a \*\* b`. This rule can be
configured to ignore `this.rightShift(Object)` using the *ignoreThisReference* property. It defaults to
*false*, so even `rightShift(x)` will trigger a violation.

This rule also ignores all calls to `super.rightShift(Object)`.


## ExplicitCallToXorMethod Rule

*Since CodeNarc 0.11*

This rule detects when the `xor(Object)` method is called directly in code instead of using the
`^` operator. A groovier way to express this: `a.xor(b)` is this: `a ^ b`. This rule can be
configured to ignore `this.xor(Object)` using the *ignoreThisReference* property. It defaults to *false*, so
even `xor(x)` will trigger a violation.

This rule also ignores all calls to `super.xor(Object)`.


## ExplicitHashMapInstantiation Rule

*Since CodeNarc 0.11*

This rule checks for explicit calls to the no-argument constructor of `HashMap`. In Groovy, it is best to replace
`new HashMap()` with `[:]`, which creates (mostly) the same object. `[:]` is technically a LinkedHashMap but it
is very rare that someone absolutely needs an instance of `HashMap` and not a subclass.


## ExplicitLinkedHashMapInstantiation Rule

*Since in CodeNarc 0.14*

This rule checks for the explicit instantiation of a `LinkedHashMap` using the no-arg constructor. In Groovy, it
is best to replace `new LinkedHashMap()` with `[:]`, which creates the same object.


## ExplicitHashSetInstantiation Rule

*Since CodeNarc 0.11*

This rule checks for explicit calls to the no-argument constructor of `HashSet`. In Groovy, it is best to replace
`new HashSet()` with `[] as Set`, which creates the same object.


## ExplicitLinkedListInstantiation Rule

*Since CodeNarc 0.11*

This rule checks for explicit calls to the no-argument constructor of `LinkedList`. In Groovy, it is best to replace
`new LinkedList()` with `[] as Queue`, which creates the same object.


## ExplicitStackInstantiation Rule

*Since CodeNarc 0.11*

This rule checks for explicit calls to the no-argument constructor of `Stack`. In Groovy, it is best to replace
`new Stack()` with `[] as Stack`, which creates the same object.


## ExplicitTreeSetInstantiation Rule

*Since CodeNarc 0.11*

This rule checks for explicit calls to the no-argument constructor of `TreeSet`. In Groovy, it is best to replace
`new TreeSet()` with `[] as SortedSet`, which creates the same object.


## GetterMethodCouldBeProperty Rule

*Since CodeNarc 0.16*

If a class defines a `public` method that follows the Java getter notation and that returns a constant, literal
or static final field value, then it is cleaner to provide a Groovy property for the value rather than a Groovy method.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreMethodsWithOverrideAnnotation | If `true`, then do not check methods annotated with @Override. | `false` |

Example of violations:

```
    interface Parent {
        String getSomething()
        String getSomethingElse()
    }

    class Child extends Parent {
        static final VALUE = 'value'

        String getSomething() {         // violation
            'something'         
        }

        @Override
        String getSomethingElse() {     // violation
            VALUE       
        }

        int getOtherValue() {           // violation
            123
        }

        Class getTheClass() {           // violation
            return Integer
        }

        static String getName() {       // violation
            'MyName'
        }
    }

    class Child2 extends Parent {
        static final VALUE = 'value'
        final String something = 'something'    // this is cleaner
        final String somethingElse = VALUE      // this is cleaner
        final int otherValue = 123              // this is cleaner
        static final String name = 'MyName'     // this is cleaner
    }
```


## GroovyLangImmutable Rule

*Since CodeNarc 0.13*

The `groovy.lang.Immutable` annotation has been deprecated and replaced by `groovy.transform.Immutable`. Do
not use the `Immutable` in `groovy.lang`.

Example of violations:

```
    @Immutable                          // Violation (no import means groovy.lang.Immutable)
    class Person { }

    @groovy.lang.Immutable              // Violation
    class Person { }

    import groovy.lang.Immutable as Imtl
    @Imtl                               // Violation
    class Person { }

```

Example of valid use of @Immutable:

```
    @groovy.transform.Immutable                 // OK
    class Person { }

    import groovy.transform.Immutable           // OK
    @Immutable
    class Person { }

    import groovy.transform.*
    @Immutable                                  // OK
    class Person { }

    import groovy.transform.Immutable as Imtl
    @Imtl                                       // OK
    class Person { }

    @javax.annotation.concurrent.Immutable      // OK
    class MyClass { }

```


## GStringAsMapKey Rule

*Since CodeNarc 0.11*

A GString should not be used as a map key since its *hashcode* is not guaranteed to be stable.
Consider calling `key.toString()`.

Here is an example of code that produces a violation:

```
    Map map = ["${someRef}" : 'invalid' ]       // violation
```


## GStringExpressionWithinString Rule

*Since CodeNarc 0.19*

Check for regular (single quote) strings containing a GString-type expression (${..}).

Example of violations:

```
    def str1 = 'total: ${count}'                // violation
    def str2 = 'average: ${total / count}'      // violation

    def str3 = "abc ${count}"                   // ok; GString
    def str4 = '$123'                           // ok
    def str5 = 'abc {123}'                      // ok
```

## UseCollectMany Rule

*Since CodeNarc 0.16*

In many case `collectMany()` yields the same result as `collect{}.flatten()`.
It is easier to understand and more clearly conveys the intent.

Example of violations:

```
def l = [1, 2, 3, 4]

l.collect{ [it, it*2] }.flatten() // suboptimal

l.collectMany{ [it, it*2] }       // same functionality, better readability
```

## UseCollectNested Rule

*Since CodeNarc 0.16*

Instead of nested `collect{}` calls use `collectNested{}`.

Example of violations:

```
def list = [1, 2, [3, 4, 5, 6], [7]]

println list.collect { elem -*
    if (elem instanceof List)
        elem.collect {it *2} // violation
    else elem * 2
}

println list.collect([8]) {
    if (it instanceof List)
        it.collect {it *2} // violation
    else it * 2
}

println list.collectNested { it * 2 } // same functionality, better readability
```


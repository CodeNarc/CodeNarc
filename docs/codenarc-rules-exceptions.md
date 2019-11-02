---
layout: default
title: CodeNarc - Exceptions Rules
---  

# Exceptions Rules  ("*rulesets/exceptions.xml*")


## CatchArrayIndexOutOfBoundsException Rule

*Since CodeNarc 0.12*

Checks for catching a `ArrayIndexOutOfBoundsException`. Catching `ArrayIndexOutOfBoundsException` should
be avoided in the first place by checking the array size before accessing an array element. Catching the
exception may mask underlying errors.


## CatchError Rule

Checks for catching a `Error`. In most cases that is much too broad, and is also dangerous
because it can catch exceptions such as `ThreadDeath` and `OutOfMemoryError`.


## CatchException Rule

Checks for catching a `Exception`. In most cases that is too broad or general. It should usually
be restricted to framework or infrastructure code, rather than application code.


## CatchIllegalMonitorStateException Rule

*Since CodeNarc 0.11*

Dubious catching of IllegalMonitorStateException. IllegalMonitorStateException is generally only thrown in case of
a design flaw in your code (calling wait or notify on an object you do not hold a lock on).


## CatchIndexOutOfBoundsException Rule

*Since CodeNarc 0.12*

Checks for catching a `IndexOutOfBoundsException`. Catching `IndexOutOfBoundsException` should
be avoided in the first place by checking for a valid index before accessing an indexed element. Catching the
exception may mask underlying errors.


## CatchNullPointerException Rule

Checks for catching a `NullPointerException`. Catching `NullPointerException` is never
appropriate. It should be avoided in the first place with proper null checking, and it can mask underlying errors.


## CatchRuntimeException Rule

Checks for catching a `RuntimeException`. In most cases that is too broad or general. It should
usually be restricted to framework or infrastructure code, rather than application code.


## CatchThrowable Rule

Checks for catching a `Throwable`. In most cases that is much too broad, and is also dangerous
because it can catch exceptions such as `ThreadDeath` and `OutOfMemoryError`.


## ConfusingClassNamedException Rule

*Since CodeNarc 0.11*

This class is not derived from another exception, but ends with 'Exception'. This will be confusing to users of
this class.


## ExceptionExtendsError Rule

*Since CodeNarc 0.13*

Errors are system exceptions. Do not extend them.

Examples:

```
    class MyError extends Error { }  // violation
    class MyError extends java.lang.Error { }  // violation

    class MyException extends Exception { }  // OK
```


## ExceptionExtendsThrowable Rule

*Since CodeNarc 0.21*

Checks for classes that extend `Throwable`. Custom exception classes should subclass `Exception`
or one of its descendants.

Example of violations:

```
    class MyException extends Throwable { }   // violation
```


## ExceptionNotThrown Rule

*Since CodeNarc 0.18*

Checks for an exception constructor call without a `throw` as the last statement within a catch block.
This rule treats any constructor call for a class named *xxx***Exception** as an exception constructor call.

Example of violations:

```
    void execute() {
        try { } catch(Exception e) { new Exception(e) }     // violation
    }

    try {
        doStuff()
    } catch(DaoException e) {
        log.warning("Ooops", e)
        new ServiceException(e)                             // violation
    } catch(Exception e) {
        new SystemException(e)                              // violation
    }

    try {
        doStuff()
    } catch(Exception e) { throw new DaoException(e) }      // ok
```


## MissingNewInThrowStatement Rule

*Since CodeNarc 0.12*

A common Groovy mistake when throwing exceptions is to forget the new keyword. For instance, `throw RuntimeException()`
instead of `throw new RuntimeException()`. If the error path is not unit tested then the production system will
throw a Method Missing exception and hide the root cause. This rule finds constructs like `throw RuntimeException()` that
look like a new keyword was meant to be used but forgotten.

The following code will all cause violations:

```
    throw RuntimeException()    // ends in Exceptions, first letter Capitalized
    throw RuntimeFailure()      // ends in Failure, first letter Capitalized
    throw RuntimeFault(foo)     // ends in Fault, first letter Capitalized
```

The following code will not cause any exceptions:

```
    throw new RuntimeException()
    throw runtimeFailure()      // first letter lowercase, assumed to be method call
```


## ReturnNullFromCatchBlock Rule

*Since CodeNarc 0.11*

Returning null from a catch block often masks errors and requires the client to handle error codes. In some coding
styles this is discouraged. This rule ignores methods with `void` return type.


## SwallowThreadDeath Rule

*Since CodeNarc 0.14*
Detects code that catches java.lang.ThreadDeath without re-throwing it.

Example of violations:

```
    try {
        def a = 0
    } catch (ThreadDeath td) {
        td.printStackTrace()
    }
```


## ThrowError Rule

Checks for throwing an instance of `java.lang.Error`. This is not appropriate within
normal application code. Throw an instance of a more specific exception subclass instead.


## ThrowException Rule

Checks for throwing an instance of `java.lang.Exception`. Throw an instance of a more
specific exception subclass instead.


## ThrowNullPointerException Rule

Checks for throwing an instance of `java.lang.NullPointerException`. Applications should never
throw a `NullPointerException`.


## ThrowRuntimeException Rule

Checks for throwing an instance of `java.lang.RuntimeException`. Throw an instance of a more
specific exception subclass instead.


## ThrowThrowable Rule

Checks for throwing an instance of `java.lang.Throwable`. Throw an instance of a more
specific exception subclass instead.


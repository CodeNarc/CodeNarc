---
layout: default
title: CodeNarc - Logging Rules
---  

# Logging Rules  ("*rulesets/logging.xml*")


## LoggerForDifferentClass Rule

*Since CodeNarc 0.12*

Checks for instantiating a logger for a class other than the current class. Checks for logger
instantiations for **Log4J**, **SLF4J**, **Logback**, **Apache Commons Logging** and **Java Logging API
(java.util.logging)**.

This rule contains a parameter `allowDerivedClasses`. When set, a logger may be created about this.getClass().

Limitations:
  * Only checks Loggers instantiated within a class field or property (not variables or expressions within a method)
  * For **Log4J**: Does not catch Logger instantiations if you specify the full package name for the `Logger`
    class: e.g.  `org.apache.log4.Logger.getLogger(..)`
  * For **SLF4J** and **Logback**: Does not catch Log instantiations if you specify the full package name for the
    `LoggerFactory` class: e.g. `org.slf4j.LoggerFactory.getLogger(..)`
  * For **Commons Logging**: Does not catch Log instantiations if you specify the full package name for the
    `LogFactory` class: e.g.  `org.apache.commons.logging.LogFactory.getLog(..)`
  * For **Java Logging API**: Does not catch Logger instantiations if you specify the full package name for the
    `Logger` class: e.g.  `java.util.logging.Logger.getLogger(..)`

Here are examples of **Log4J** or **Java Logging API** code that cause violations:

```
    class MyClass {
        private static final LOG = LoggerFactory.getLogger(SomeOtherClass)  // violation
        def log1 = LoggerFactory.getLogger(SomeOtherClass.class)            // violation
        def log2 = LoggerFactory.getLogger(SomeOtherClass.class.name)       // violation
    }
```

Here are examples of **Commons Logging** code that cause violations:

```
    class MyClass {
        private static final LOG = LogFactory.getLog(SomeOtherClass)    // violation
        Log log1 = LogFactory.getLog(SomeOtherClass.class)              // violation
        def log2 = LogFactory.getLog(SomeOtherClass.class.getName())    // violation
    }
```

Here are examples of code that does NOT cause violations:

```
    // Log4J or Java Logging API

    class MyClass {
        private static final LOG = LoggerFactory.getLogger(MyClass)                    // ok
        def log2 = LoggerFactory.getLogger(MyClass.class)                              // ok
        private static log3 = LoggerFactory.getLogger(MyClass.getClass().getName())    // ok
        private static log4 = LoggerFactory.getLogger(MyClass.getClass().name)         // ok
        private static log5 = LoggerFactory.getLogger(MyClass.class.getName())         // ok
        private static log6 = LoggerFactory.getLogger(MyClass.class.name)              // ok
    }

    // Commons Logging

    class MyClass {
        private static final LOG = LogFactory.getLog(MyClass)                   // ok
        def log2 = LogFactory.getLog(MyClass.class)                             // ok
        private static log3 = LogFactory.getLog(MyClass.getClass().getName())   // ok
        private static log4 = LogFactory.getLog(MyClass.getClass().name)        // ok
        private static log5 = LogFactory.getLog(MyClass.class.getName())        // ok
        private static log6 = LogFactory.getLog(MyClass.class.name)             // ok
    }
```


## LoggingSwallowsStacktrace Rule

*Since CodeNarc 0.12*

If you are logging an exception then the proper API is to call error(Object, Throwable), which will log the message and the exception stack trace. If you call error(Object) then the stacktrace may not be logged.


## LoggerWithWrongModifiers Rule

*Since CodeNarc 0.12*

Logger objects should be declared private, static and final.

This rule has a property: `allowProtectedLogger`, which defaults to false. Set it to true if you believe
subclasses should have access to a Logger in a parent class and that Logger should be declared protected or public.

This rule has a property: `allowNonStaticLogger`, which defaults to false. Set it to true if you believe
a logger should be allowed to be non-static.


## MultipleLoggers Rule

*Since CodeNarc 0.12*

This rule catches classes that have more than one logger object defined. Typically, a class has zero or one logger objects.


## Println Rule

Checks for calls to `this.print()`, `this.println()` or `this.printf()`. Consider using
a standard logging facility instead.


## PrintStackTrace Rule

Checks for calls to `Throwable.printStackTrace()` or `StackTraceUtils.printSanitizedStackTrace(Throwable)`.
Consider using a standard logging facility instead.


## SystemErrPrint Rule

Checks for calls to `System.err.print()`, `System.err.println()` or `System.err.printf()`.
Consider using a standard logging facility instead.


## SystemOutPrint Rule

Checks for calls to `System.out.print()`, `System.out.println()` or `System.out.printf()`.
Consider using a standard logging facility instead.


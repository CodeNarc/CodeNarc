---
layout: default
title: CodeNarc - Imports Rules
---  

# Imports Rules  ("*rulesets/imports.xml*")


## DuplicateImport Rule

Checks for a duplicate *import* statements.

NOTE: This is a file-based rule, rather than a typical AST-based rule, so the *applyToClassNames*
and *doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## ImportFromSamePackage Rule


Checks for an *import* of a class that is within the same package as the importing class.

NOTE: This is a file-based rule, rather than a typical AST-based rule, so the *applyToClassNames*
and *doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## ImportFromSunPackages Rule

*Since CodeNarc 0.14*
Avoid importing anything from the 'sun.*' packages. These packages are not portable and are likely to change.

Example of violations:

```
    import sun.misc.foo
    import sun.misc.foo as Foo

    public class MyClass{}
```

NOTE: This is a file-based rule, rather than a typical AST-based rule, so the *applyToClassNames*
and *doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## MisorderedStaticImports Rule

*Since CodeNarc 0.14*
Checks for static *import* statements which should never be after nonstatic imports.

This rule has one property `comesBefore`, which defaults to true. If you like your
static imports to come after the others, then set this property to false.

Examples of violations:

```
    import my.something.another
    import static foo.bar

    public class MyClass{}
```

NOTE: This is a file-based rule, rather than a typical AST-based rule, so the *applyToClassNames*
and *doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## NoWildcardImports Rule

*Since CodeNarc 0.21*

Checks for wildcard (star) imports. If the *ignoreStaticImports* property is *true*, then do not check static imports.
Similarly, do not check the standard imports if ignoreImports is *true*.

Example of violations:

```
    import static foo.bar.*         // violation (unless ignoreStaticImports is true)
    import my.something.*           // violation (unless ignoreImports is true)

    public class MyClass{}
```

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreStaticImports         | If *true*, then do not check static imports. | `false` |
| ignoreImports               | If *true*, then do not check imports.        | `false` |

NOTE: This is a file-based rule, rather than a typical AST-based rule, so the *applyToClassNames*
and *doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## UnnecessaryGroovyImport Rule

Checks for an *import* from any package that is already automatically imported for
Groovy files. A Groovy file does not need to include an import for classes from
*java.lang*, *java.util*, *java.io*, *java.net*, *groovy.lang* and *groovy.util*, as well as the
classes *java.math.BigDecimal* and *java.math.BigInteger*.

NOTE: This is a file-based rule, rather than a typical AST-based rule, so the *applyToClassNames*
and *doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## UnusedImport Rule

Checks for *import* statements for classes that are never referenced within the source file. Also
checks static imports.

Known limitations:
  * Does not check for unused imports containing wildcards (e.g. `import org.codenarc.*`)
  * Misses unused imports if the class/alias name is contained within strings, comments or other (longer)
    names (i.e., if that string shows up almost anywhere within the source code).

NOTE: This is a file-based rule, rather than a typical AST-based rule, so the *applyToClassNames*
and *doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

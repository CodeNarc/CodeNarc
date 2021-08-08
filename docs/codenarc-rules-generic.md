---
layout: default
title: CodeNarc - Generic Rules
---  

# Generic Rules  ("*rulesets/generic.xml*")


These rules provide a generic check that only makes sense when customized. These rules are not *enabled* until
the necessary configuration is provided.

See [CodeNarc – Hidden Gems: Using CodeNarc’s generic rules](http://tenpercentnotcrap.wordpress.com/2013/08/04/codenarc-hidden-gems-using-codenarcs-generic-rules/)
for examples of using the **CodeNarc** *"generic"* rules to enforce your own custom best practices.


## IllegalClassMember Rule

*Since CodeNarc 0.19*

Checks for classes containing fields/properties/methods matching configured illegal member
modifiers or not matching any of the configured allowed member modifiers.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowedFieldModifiers       | Specifies one or more groups of whitespace-delimited modifier names (e.g. "public static" or "protected"). Multiple groups are separated by commas (e.g. "private final, protected"). If a field does not match all of the modifiers in any group, then trigger a violation. If `null` or empty, skip this check.  | `null` |
| allowedMethodModifiers      | Specifies one or more groups of whitespace-delimited modifier names (e.g. "public static" or "protected"). Multiple groups are separated by commas (e.g. "private final, protected"). If a method does not match all of the modifiers in any group, then trigger a violation. If `null` or empty, skip this check.  | `null` |
| allowedPropertyModifiers    | Specifies one or more groups of whitespace-delimited modifier names (e.g. "public static" or "protected"). Multiple groups are separated by commas (e.g. "private final, protected"). If a property does not match all of the modifiers in any group, then trigger a violation. If `null` or empty, skip this check.  | `null` |
| ignoreMethodNames           | Specifies one or more (comma-separated) method names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |
| ignoreMethodsWithAnnotationNames | Specifies one or more (comma-separated) annotation names that should be ignored (i.e., methods with those annotations should not cause a rule violation). The names may optionally contain wildcards (*,?). (Do not include the "@" in the annotation name.| `null` |
| illegalFieldModifiers       | Specifies one or more groups of whitespace-delimited modifier names (e.g. "public static" or "protected"). Multiple groups are separated by commas (e.g. "private final, protected"). If a field matches all of the modifiers in any group, then trigger a violation. If `null` or empty, skip this check.  | `null` |
| illegalMethodModifiers      | Specifies one or more groups of whitespace-delimited modifier names (e.g. "public static" or "protected"). Multiple groups are separated by commas (e.g. "private final, protected"). If a method matches all of the modifiers in any group, then trigger a violation. If `null` or empty, skip this check.  | `null` |
| illegalPropertyModifiers    | Specifies one or more groups of whitespace-delimited modifier names (e.g. "public static" or "protected"). Multiple groups are separated by commas (e.g. "private final, protected"). If a property matches all of the modifiers in any group, then trigger a violation. If `null` or empty, skip this check.  | `null` |

Modifiers for fields and methods include:
  * public
  * protected
  * private
  * static
  * final
  * volatile (fields only)
  * transient (fields only)

Modifiers for properties are only:
  * static
  * final

Note that you must use the standard rule properties, such as `applyToClassNames`, `doNotApplyToFileNames`
and `applyToFilesMatching` to apply this rule to a subset of all classes/files. These rule properties
are described in 
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

Example of violations for methods:

```
    // IllegalClassMember.allowedMethodModifiers = 'public final, private, protected static'

    class MyClass {
        public method1() { }            // violation
        protected method2() { }         // violation
        protected static method3() { }
    }
```

Example of violations for properties:

```
    // IllegalClassMember.illegalPropertyModifiers = 'final'

    class MyClass {
        def property1
        final property2         // violation
        static property3
    }
```

A RuleSet can contain any number of instances of this rule, but each should be configured
with a unique rule *name* and *classNames*, and (optionally) customized *violationMessage* and *priority*.

###  Notes

  1. At least one the `illegalFieldModifiers`, `allowedFieldModifiers`, `illegalPropertyModifiers`,
  `allowedPropertyModifiers`, `illegalMethodModifiers` or `allowedMethodModifiers`
  properties must be set (i.e., not null or empty) or else this rule does nothing. In other words, you must configure
  this rule with at least one kind of illegal or allowed class member.

  2. At least one of the (standard) `applyToClassNames`, `applyToFileNames` or `applyToFilesMatching`
  properties must be set (i.e., not null or empty) or else this rule does nothing. In other words, you must configure
  this rule to apply to a specific set of classes or files.


## IllegalClassReference Rule

*Since CodeNarc 0.15*

Checks for reference to any of the classes configured in `classNames`.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| classNames                  | Specifies the comma-separated list of (fully-qualified) class names. The class name(s) may optionally include wildcard characters ('*' or '?'). Note that the '*' wildcard matches any sequence of zero or more characters in the class/package name, e.g. 'a.*.MyClass' matches `a.b.MyClass` as well as `a.b.c.d.MyClass`. If `classNames` is null or empty, do nothing. | `null` |

Note that you can use the standard rule properties, such as `applyToClassNames`, `doNotApplyToFileNames`
and `applyToFilesMatching` to only apply this rule to a subset of all classes/files. These rule properties
are described in 
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

This rule can be useful for governance and enforcement of *architectural layering*. For instance,
making sure that view or model classes, for instance, do not contain references to DAO classes (e.g., *Dao).

Here is an example configuration of this rule used to ensure that DAO classes are not referenced from
within model classes:

```
    ruleset {
        description "Example CodeNarc Ruleset"

        // ...

        IllegalClassReference {
            name = 'DoNotReferenceDaoFromModelClasses'
            priority = 2
            classNames = '*Dao'
            applyToClassNames = 'com.example.model.*'
            description = 'Do not reference DAOs from model classes.'
        }
    }
```

A RuleSet can contain any number of instances of this rule, but each should be configured
with a unique rule *name* and *classNames*, and (optionally) customized *violationMessage* and *priority*.


## IllegalPackageReference Rule

*Since CodeNarc 0.14*

Checks for reference to any of the packages configured in `packageNames`.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| packageNames                | Specifies the comma-separated list of package names. The package name(s) may optionally include wildcard characters ('*' or '?'). Note that the '*' wildcard matches any sequence of zero or more characters in the package name, e.g. 'a.*' matches 'a.b' as well as 'a.b.c.d'. If `packageNames` is null or empty, do nothing. | `null` |

Note that you can use the standard rule properties, such as `applyToClassNames`, `doNotApplyToFileNames`
and `applyToFilesMatching` to only apply this rule to a subset of all classes/files. These rule properties
are described in 
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

This rule can be useful for governance and enforcement of *architectural layering*. For instance,
making sure that view or model classes, for instance, do not contain references to JDBC-specific packages
(e.g. java.sql and javax.sql).

Here is an example configuration of this rule used to ensure that JDBC packages/classes are only
referenced within DAO classes:

```
    ruleset {
        description "Example CodeNarc Ruleset"

        // ...

        IllegalPackageReference {
            name = 'UseJdbcOnlyInDaoClasses'
            priority = 2
            packageNames = 'groovy.sql, java.sql, javax.sql'
            doNotApplyToClassNames = 'com.example.framework.dao.*, *Dao, *DaoImpl'
            description = 'Reference to JDBC packages should be restricted to DAO classes.'
        }
    }
```

A RuleSet can contain any number of instances of this rule, but each should be configured
with a unique rule *name* and *packageNames*, and (optionally) customized *violationMessage* and *priority*.


## IllegalRegex Rule

Checks for a specified illegal regular expression within the source code.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | The regular expression to check for. If null or empty then do nothing. | `null` |

A RuleSet can contain any number of instances of this rule, but each should be configured
with a unique rule *name* and *regex*, and (optionally) customized *violationMessage* and *priority*.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## IllegalString Rule

*Since CodeNarc 0.20*

Checks for a specified illegal string within the source code.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| string                      | The String to check for. If null or empty then do nothing.     | `null` |

A RuleSet can contain any number of instances of this rule, but each should be configured
with a unique rule *name* and *string*, and (optionally) customized *violationMessage* and *priority*.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).
The `@SuppressWarnings` annotation-based disablement is also unavailable, but including a `// codenarc-disable IllegalString` comment
somewhere above the violation will disable this rule. See
[Disabling Rules From Comments](./codenarc-configuring-rules.html#disabling-rules-from-comments).

## IllegalSubclass Rule

*Since CodeNarc 0.21*

Checks for classes that extend one of the specified set of illegal superclasses.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| superclassNames             | Specifies the comma-separated list of (fully-qualified) class names. The class name(s) may optionally include wildcard characters ('*' or '?'). Note that the '*' wildcard matches any sequence of zero or more characters in the class/package name, e.g. 'a.*.MyClass' matches `a.b.MyClass` as well as `a.b.c.d.MyClass`. If `classNames` is null or empty, do nothing. | `null` |

A RuleSet can contain any number of instances of this rule, but each should be configured
with a unique rule *name* and *string*, and (optionally) customized *violationMessage* and *priority*.


## RequiredRegex Rule

Checks for a specified regular expression that must exist within the source code.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | The regular expression to check for. If null or empty then do nothing.  | `null` |

A RuleSet can contain any number of instances of this rule, but each should be configured
with a unique rule *name* and *regex*, and (optionally) customized *violationMessage* and *priority*.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## RequiredString Rule

Checks for a specified text string that must exist within the source code.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| string                      | The String to check for. If null or empty then do nothing.     | `null` |

A RuleSet can contain any number of instances of this rule, but each should be configured
with a unique rule *name* and *string*, and (optionally) customized *violationMessage* and *priority*.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## StatelessClass Rule

Checks for non-`final` fields on a class. The intent of this rule is to check a configured set
of classes that should remain "stateless" and reentrant. One example might be Grails
service classes which are singletons, by default, and so they should be reentrant.

This rule ignores `final` fields (either instance or static). Fields that are
`static` and non-`final`, however, do cause a violation.

This rule also ignores all classes annotated with the `@Immutable` transformation.
See [http://groovy.codehaus.org/Immutable+transformation](http://groovy.codehaus.org/Immutable+transformation).

This rule also ignores all fields annotated with the `@Inject` or `@Value` annotations.

You can configure this rule to ignore certain fields either by name or by type. This can be
useful to ignore fields that hold references to (static) dependencies (such as DAOs or
Service objects) or static configuration.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreFieldNames            | Specifies one or more (comma-separated) field names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |
| addToIgnoreFieldNames       | Specifies one or more (comma-separated) field names to be added to the `ignoreFieldNames` property value. This is a special write-only property, and each call to `setAddIgnoreFieldNames()` adds to (rather than overwrites) the list of field names to be ignored. | `null` |
| ignoreFieldTypes            | Specifies one or more (comma-separated) field types that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |

Note that you can use the standard rule properties, such as `applyToClassNames`, `doNotApplyToFileNames`
and `applyToFilesMatching` to only apply this rule to a subset of all classes/files. These rule properties
are described in zzz./codenarc-configuring-rules.html#standard-properties-for-configuring-rules}
Standard Properties for Configuring Rulesyy.

###  Notes

  1.  The `ignoreFieldTypes` property matches the field type name as indicated
  in the field declaration, only including a full package specification IF it is included in
  the source code. For example, the field declaration `BigDecimal value` matches
  an `ignoreFieldTypes` value of `BigDecimal`, but not
  `java.lang.BigDecimal`.

  2.  There is one exception for the `ignoreFieldTypes` property: if the field is declared
  with a modifier/type of `def`, then the type resolves to `java.lang.Object`.

  3. At least one of the (standard) `applyToClassNames`, `applyToFileNames` or `applyToFilesMatching`
  properties must be set (i.e., not null or empty) or else this rule does nothing. In other words, you must configure
  this rule to apply to a specific set of classes or files.

  4. This rule will not catch violations of true *statelessness*/*reentrancy* if you define a `final`
  field whose value is itself mutable, e.g. a `final HashMap`.


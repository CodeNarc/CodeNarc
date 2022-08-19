---
layout: default
title: CodeNarc - Naming Rules
---  

# Naming Rules  ("*rulesets/naming.xml*")


## AbstractClassName Rule

Verifies that the name of an abstract class matches the regular expression specified in the
**regex** property. If that property is null or empty, then this rule is not applied
(i.e., it does nothing). It defaults to null, so this rule must be explicitly configured to be
active. This rule ignores interfaces and is applied only to abstract classes.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | Specifies the regular expression used to validate the abstract class name. If null or empty, then this rule does nothing. | `null` |


## ClassName Rule

Verifies that the name of a class matches a regular expression. By default it checks that the
class name starts with an uppercase letter and is followed by zero or more word characters
(letters, numbers or underscores) or dollar signs ($).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | Specifies the regular expression used to validate the class name. It is required and cannot be null or empty. | (\[A-Z\]\\w*\\$?)* |


## ClassNameSameAsFilename Rule

*Since CodeNarc 0.19*

Reports files containing only one top level class / enum / interface which is named differently than the file.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## ClassNameSameAsSuperclass Rule

*Since CodeNarc 0.24*

Checks for any class that has an identical name to its superclass, other than the package. This can be very confusing.

Also see FindBugs NM_SAME_SIMPLE_NAME_AS_SUPERCLASS rule.

Example of violations:

```
    class MyClass extends other.MyClass         // violation
```


## ConfusingMethodName Rule

*Since CodeNarc 0.11*

Checks for very confusing method names. The referenced methods have names that differ only by capitalization.
This is very confusing because if the capitalization were identical then one of the methods would override
the other.

Also, violations are triggered when methods and fields have very similar names.

```
    class MyClass {
        int total
        int total() {
            1
        }
    }
```


## FactoryMethodName Rule

*Since CodeNarc 0.16*

A factory method is a method that creates objects, and they are typically named either buildFoo(), makeFoo(), or
createFoo(). This rule enforces that only one naming convention is used. It defaults to allowing makeFoo(), but that
can be changed using the property `regex`. The regex is a negative expression; it specifically bans
methods named build* or create*. However, methods named `build` or `build*` receive some special treatment because of the
popular Builder Pattern. If the 'build' method is in a class named *Builder then it does not cause a violation.

Builder methods are slightly different than factory methods.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | Specifies the default regular expression used to validate the method name. It is required and cannot be null or empty.       | (build.*\|create.*)    |

Example of violations:

```
    class MyClass {

        // violation. Factory methods should be named make()
        def create() {
        }

        // violation. Factory methods should be named make()
        def createSomething() {
        }

        // violation. Builder method not in class named *Builder
        def build() {
        }

        // violation. Builder method not in class named *Builder
        def buildSomething() {
        }

        // this is OK because it is called make
        def make() {
        }

        // this is also OK
        def makeSomething() {
        }

        // OK, overriding a parent
        @Override
        build() { }

    }

    class WidgetBuilder {

        // OK, the class name ends in Builder
        def build() {
        }
    }
```

## FieldName Rule

Verifies that the name of each field matches a regular expression. By default it checks that
fields that are not *static final* have field names that start with a lowercase letter and contains
only letters or numbers. By default, *static final* field names start with an uppercase letter and
contain only uppercase letters, numbers and underscores.

**NOTE:** This rule checks only regular *fields* of a class, not *properties*. In Groovy,
*properties* are fields declared with no access modifier (public, protected, private). Thus,
this rule only checks fields that specify an access modifier. For naming of *properties*,
see `PropertyNameRule`.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | Specifies the default regular expression used to validate the field name. It is required and cannot be null or empty.        | \[a-z\]\[a-zA-Z0-9\]*  |
| finalRegex                  | Specifies the regular expression used to validate `final` field names. It is optional. If not set, then `final` fields that are non-`static` are validated using **regex**. | `null` |
| staticRegex                 | Specifies the regular expression used to validate `static` field names. It is optional. If not set, then `static` fields that are non-`final` are validated using **regex**. | `null` |
| staticFinalRegex            | Specifies the regular expression used to validate `static final` field names. It is optional. If not set, then `static final` fields are validated using **finalRegex**, **staticRegex** or **regex**.                  |  \[A-Z\]\[A-Z0-9_\]*  |
| ignoreFieldNames            | Specifies one or more (comma-separated) field names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `serialVersionUID` |


The order of precedence for the regular expression properties is: **staticFinalRegex**,
**finalRegex**, **staticRegex** and finally **regex**. In other words, the first
regex in that list matching the modifiers for the field is the one that is applied for the field name validation.


## InterfaceName Rule

Verifies that the name of an interface matches the regular expression specified in the
**regex** property. If that property is null or empty, then this rule is not applied
(i.e., it does nothing). It defaults to null, so this rule must be explicitly configured to be
active.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | Specifies the regular expression used to validate the name of an interface. If null or empty, then this rule does nothing.   | `null` |


## InterfaceNameSameAsSuperInterface Rule

*Since CodeNarc 0.24*

Checks for any interface that has an identical name to its super-interface, other than the package. This can be very confusing.

Example of violations:

```
    interface MyInterface extends other.MyInterface { }     // violation
```


## MethodName Rule

Verifies that the name of each method matches a regular expression. By default it checks that the
method name starts with a lowercase letter. Implicit method names are ignored (i.e., 'main' and 'run'
methods automatically created for Groovy scripts).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | Specifies the regular expression used to validate the method name. It is required and cannot be null or empty.              | \[a-z\]\\w* |
| ignoreMethodNames           | Specifies one or more (comma-separated) method names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |


## ObjectOverrideMisspelledMethodName Rule

*Since CodeNarc 0.11*

Verifies that the names of the most commonly overridden methods of `Object`: `equals`,
`hashCode` and `toString`, are correct.

Here are some examples of code that produces violations:

```
    boolean equal(Object o) {}                  // violation
    boolean equal(int other) {}                 // ok; wrong param type
    boolean equal(Object o, int other) {}       // ok; too many params

    boolean equaLS(Object o) {}                 // violation

    int hashcode() {}                           // violation
    int hashCOde() {}                           // violation
    int hashcode(int value) {}                  // ok; not empty params

    String tostring() {}                        // violation
    String toSTring() {}                        // violation
    String tostring(int value) {}               // ok; not empty params
```


## PackageName Rule

Verifies that the package name of a class matches a regular expression. By default it checks that the
package name consists of only lowercase letters and numbers, separated by periods.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | Specifies the regular expression used to validate the package name. It is required and cannot be null or empty. | \[a-z\]+\[a-z0-9\]*(\\.\[a-z0-9\]+)* |
| packageNameRequired         | Indicates whether a package name declaration is required for all classes.  | `false`                     |


## PackageNameMatchesFilePath Rule

*Since CodeNarc 0.22*

A package source file's path should match the package declaration.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| groupId                     | Specifies the common *group id* part of a package name, that will appear within all checked package names. It must also map to the file path for the corresponding source file. <br/><br/> For instance, a *groupId* of *"org.sample"* means that for all classes that specify a package, that package name must include *"org.sample"*, and the source file must exist under an "org/sample" directory. Then, a `MyClass` class in a `org.sample.util` package must be defined in a "MyClass.groovy" file within a *"org/sample/util"* directory. That directory can be the child of any arbitrary *root path*, e.g. "src/main/groovy".<br/><br/> To find the sub-path relevant for the package the rule searches for the first appearance of *groupId* in the file path. It's *required* to configure this. <br/><br/> If `groupId` is null or empty, this rule does nothing. | `null` |

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## ParameterName Rule

Verifies that the name of each parameter matches a regular expression. This rule applies to method parameters,
constructor parameters and closure parameters. By default it checks that parameter names start with a
lowercase letter and contains only letters or numbers.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | Specifies the regular expression used to validate the parameter name. It is required and cannot be null or empty.              | \[a-z\]\[a-zA-Z0-9\]*  |
| ignoreParameterNames        | Specifies one or more (comma-separated) parameter names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |


## PropertyName Rule

Verifies that the name of each property matches a regular expression. By default it checks that
property names (other than *static final*) start with a lowercase letter and contains only letters or numbers.
By default, *static final* property names start with an uppercase letter and contain only uppercase
letters, numbers and underscores.

**NOTE:** This rule checks only *properties* of a class, not regular *fields*. In Groovy,
*properties* are fields declared with no access modifier (public, protected, private).
For naming of regular *fields*, see `FieldNameRule`.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | Specifies the default regular expression used to validate the property name. It is required and cannot be null or empty.         | \[a-z\]\[a-zA-Z0-9\]* |
| finalRegex                  | Specifies the regular expression used to validate `final` property names. It is optional. If not set, then `final` properties that are non-`static` are validated using **regex**. | `null` |
| staticRegex                 | Specifies the regular expression used to validate `static` property names. It is optional. If not set, then `static` properties that are non-`final` are validated using **regex**. | `null` |
| staticFinalRegex            | Specifies the regular expression used to validate `static final` property names. It is optional. If not set, then `static final` property are validated using **finalRegex**, **staticRegex** or **regex**.                      | \[A-Z\]\[A-Z0-9_\]*  |
| ignorePropertyNames         | Specifies one or more (comma-separated) property names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).      | `null` |


The order of precedence for the regular expression properties is: **staticFinalRegex**, **finalRegex**,
**staticRegex** and finally **regex**. In other words, the first regex in that list matching the
modifiers for the property is the one that is applied for the field name validation.


## VariableName Rule

Verifies that the name of each variable matches a regular expression. By default, this rule checks that
variable names start with a lowercase letter and contain only letters or numbers.

Variables annotated with @Field are ignored.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| regex                       | Specifies the regular expression used to validate the variable name. It is required and cannot be null or empty.     | \[a-z\]\[a-zA-Z0-9\]* |
| finalRegex                  | Specifies the regular expression used to validate `final` variable names. It is optional. If not set, then **regex** is used to validate `final` variable names. | `null` |
| ignoreVariableNames         | Specifies one or more (comma-separated) variable names that should be ignored (i.e., that should not cause a rule violation). The names may optionally contain wildcards (*,?).  | `null` |

NOTE: Until CodeNarc 2.0, the default naming pattern for `final` variable names was that they start with an uppercase letter and contain only 
uppercase letters, numbers and underscores (i.e., like *constants*). Starting with CodeNarc 2.0, that has been changed so that `finalRegex` 
defaults to `null` and thus `final` variable names are treated like regular variables. See [#467](https://github.com/CodeNarc/CodeNarc/issues/467). 
To restore that previous behavior, set `finalRegex` to `[A-Z][A-Z0-9_]*`.


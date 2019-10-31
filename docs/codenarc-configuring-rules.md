---
layout: default
title: CodeNarc - Configuring Rules
---

# CodeNarc - Configuring Rules

**Contents**
  * [Configuring Rules Within a RuleSet File](#configuring-rules-within-a-ruleset-file)
  * [Configuring Rules Using a Properties File](#configuring-rules-using-a-properties-file)
  * [Standard Properties for Configuring Rules](#standard-properties-for-configuring-rules)
  * [Turning Off A Rule](#turning-off-a-rule)
  * [Suppressing A Rule From Within Source Code](#suppressing-a-rule-from-within-source-code)
  * [Customizing Rule Descriptions Shown in the HTML Report](#customizing-rule-descriptions-shown-in-the-html-report)

You can configure rules within the *RuleSet* file or within the "codenarc.properties" file.
Both of these approaches are described in the sections below.

**NOTE**: Keep in mind that *RuleSet* files can be nested (to any depth). That, along with the
"codenarc.properties" file support, allows multiple layers of rule configuration. The hierarchical
layering of configuration can come in handy within organizations comprised of multiple teams
or projects. For instance, you can define an organization *RuleSet* file. Then each team can define its
own *RuleSet* file and/or a "codenarc.properties" file, customizing the rules from the top-level file.


## Configuring Rules Within a RuleSet File

[Creating a RuleSet](./codenarc-custom-ruleset.html) describes how to create a new *RuleSet* and
configure the rules within it.

Here is an example of a Groovy *RuleSet* file that configures all three rules that it contains:

```
    ruleset {
        CyclomaticComplexity {
            maxMethodComplexity = 1
        }
    
        ConfusingTernary(priority:3)
    
        StatelessClass {
            name = 'StatelessDao'
            applyToClassNames = '*Dao'
        }
    }
```

Here is another example of a Groovy *RuleSet* file. This one includes another *RuleSet*, and configures
a couple of the rules that it contains:

```
    ruleset {
        ruleset('rulesets/basic.xml') {
            CatchThrowable {
                priority = 1
            }
            EqualsAndHashCode priority:3
        }
    }
```

If you have an XML *RuleSet*, then you can configure rules when you include a whole *RuleSet*
using `\*ruleset-ref\`*, as in the following example:

```
    *ruleset-ref path='rulesets/size.xml'*
        *rule-config name='ClassSize'*
            *property name='maxLines' value='500'/*
        */rule-config*
    */ruleset-ref*
```

Or you can configure *Rules* that you include individually using `\*rule\`*, as in the following example:

```
    *rule class='org.codenarc.rule.naming.VariableNameRule'*
        *property name="finalRegex" value="F_[A-Z0-9]*"/*
        *property name='priority' value='1'/*
    */rule*
```


## Configuring Rules Using a Properties File

**CodeNarc** reads the properties file named "codenarc.properties", if found on the classpath,
and applies the property values to any matching *Rules*. You can optionally override the location
of the properties file by setting the "codenarc.properties.file" system property to the path or
URL of the properties file. That can include a "file:" URL to load from a relative or absolute
path on the filesystem (e.g., "file:relative/path/override-codenarc.properties"). If the
properties file is not found, then do nothing.

For each properties entry of the form *[rule-name].[property-name]=[property-value]*,
the named property for the rule matching *rule-name* is set to the
specified *property-value*. Properties entries not of this form or specifying rule
names not within the current *RuleSet* are ignored.

The following example "codenarc.properties" file configures several rules. Note that
Fields with `Integer`, `String` and `Boolean` values are configured.

```
    # Sample RuleSet configuration
    # Entries should be of the form:    [rule-name].[property-name]=[property-value]
    
    CatchThrowable.doNotApplyToClassNames=BaseController,AbstractTest
    CatchThrowable.priority = 1
    
    ReturnFromFinallyBlock.priority = 1
    
    # Turn off this rule
    AbstractClassName.enabled=false
    
    # Unset special naming for final fields -- use base 'regex' value for all names
    FieldName.finalRegex=
    # Make sure all field names are prefixed with '_'
    FieldName.regex=_[a-z][a-zA-Z0-9]*
```

Note that you cannot add new rules using the "codenarc.properties" file, though you can disable
(turn off) rules.


## Standard Properties for Configuring Rules

The rules included with **CodeNarc** (as well as any subclasses of `AbstractRule`) provide
several standard properties that can be used to configure how rules are applied to source files.

### Applying Rules Based on Class Name and/or Package Name

The `applyToClassNames` and `doNotApplyToClassNames` properties enable filtering the
classes to which the rule is applied.

If `applyToClassNames` is not empty or null, then the rule is only
applied to classes matching one of the specified class names. Likewise, if
`doNotApplyToClassNames` is not empty or null, then the rule is NOT applied to classes
matching any of the specified class names.

Both of these properties can specify either a single class name or a comma-separated list of names.
The names may optionally contain wildcard characters ('*' or '?'). The wildcard character '*'
matches a sequence of zero or more characters in the input string. The wildcard character '?'
matches exactly one character in the input string.

Each class name(s) can be either a fully-qualified class name (including the package) or else
a class name without a package.

For instance, in the following example "codenarc.properties" excerpt, the *CatchThrowable* rule
is NOT applied to classes named "BaseController" or "AbstractTest", no matter what package they
are in (if any). The *FieldName* rule, on the other hand, is only applied to the
`org.codenarc.CodeNarc` class or any of the classes within `org.codenarc.extra` package.

```
    CatchThrowable.doNotApplyToClassNames=BaseController,AbstractTest
    
    FieldName.applyToClassNames=org.codenarc.CodeNarc,org.codenarc.extra.*
```

Both properties default to `null`.

NOTE: The *applyToClassNames* and *doNotApplyToClassNames* rule configuration properties
are available only to subclasses of `AbstractAstVisitorRule`. This includes
most of the rules provided with **CodeNarc** -- i.e., all of the rules that deal with
the Groovy AST (Abstract Syntax Tree) of a source file. This includes any rule that processes
source code elements such as a package, class, method, field, parameter, etc.. But it does not
include rules that handle imports, blank lines or other file-based rules.


### Applying Rules Based on File Path or Name

#### Matching Either Filename or Pathname, With Optional Wildcards

The `applyToFileNames` and `doNotApplyToFileNames` properties enable filtering the
files to which the rule is applied by specifying a comma-separated list of filenames (with optional
path) of source files. If `applyToFileNames` is not empty or null, then the rule is only
applied to source files matching one of the specified filenames. Likewise, if
`doNotApplyToFilesMatching` is not empty or null, then the rule is NOT applied to source files
matching any of the specified filenames.

Both properties may optionally specify a path (i.e., if the value contains at least one '/' path
separator). In that case, the value is matched against the full pathname of each source file.
If no path is specified, then only the filename of each source file is compared (i.e., its path is ignored).

Both properties may optionally contain wildcard
characters ('*' or '?'). The wildcard character '*' matches a sequence of zero or more characters
in the input string. The wildcard character '?' matches exactly one character in the input string.
Both properties default to `null`.

#### Matching File Pathnames Against a Regular Expression

The `applyToFilesMatching` and `doNotApplyToFilesMatching` properties enable filtering
the files to which the rule is applied by matching a regular expression against the **full pathname**
of each source file. If `applyToFilesMatching` is not empty or null, then the rule is only
applied to source files with matching paths. Likewise, if `doNotApplyToFilesMatching` is not
empty or null, then the rule is NOT applied to source files with matching paths. Both properties
default to `null`.

**NOTE:** File separator characters within pathnames are normalized to
forward slashes (/). On Windows, for instance, that means that the path "c:\dir\file.txt" is
normalized to "c:/dir/file.txt". If your regular expressions contain file separator characters,
be sure that they are all the standard '/', no matter what operating system.


### Other Standard Rule Properties

The `enabled` boolean property allows a rule to be completely turned off (by setting it to `false`).
It defaults to `true`.

The `violationMessage` property enables overriding the rule violation message. If not null,
this is used as the message for all violations of this rule, overriding any message generated
by the rule itself. This property defaults to `null`. Note that setting this to an empty
string "hides" the message, if any, generated by the actual rule.

The `description` property enables overriding the rule description text. If not null,
this value is used as the description text for this rule, overriding any description text
found in the i18n resource bundles. This property defaults to `null`. Also see
[Include Substitutable Message Parameters in a Rule Description](#include-substitutable-message-parameters-in-a-rule-description)

You can also override the default `name` or `priority` properties for each rule.


## Turning Off A Rule

You can turn off a rule by filtering the containing ruleset to exclude the rule.
See [Filtering Rules Within a RuleSet](./codenarc-creating-ruleset.html#filtering-rules-within-a-ruleset).

Alternately, as mentioned above, you can turn off (disable) a rule by setting its `enabled`
boolean property to `false` (assuming it is a subclass of `AbstractRule`), as shown in
the following "codenarc.properties" excerpt.

```
    # Turn off this rule
    AbstractClassName.enabled=false
```
    
And here is an example of disabling a rule within a Groovy *RuleSet* by setting its `enabled`
attribute to `false`.
    
```
    ruleset {
        ruleset('rulesets/basic.xml') {
            CatchThrowable(enabled:false)
        }
    }
```


## Suppressing A Rule From Within Source Code
You can use the `@SuppressWarnings` annotation on a *class*, *method*, *constructor*, *field* or
*property* to suppress one or more **CodeNarc** rules within its source code. *Import* statements can also be annotated,
and in that case the suppression applies to the entire file.

Specify one or more rule name as the parameter of the `@SuppressWarnings` annotation. Note
that this is the rule *name*, not the *class* name (i.e., without the "Rule" suffix). Specify
multiple rule names by passing a *List* of rule names as a single parameter, e.g.
`@SuppressWarnings(['IfStatementBraces', 'ThrowException'])`

NOTE: The `@SuppressWarnings` annotation only works for rule classes that subclass `AbstractAstVisitorRule`.

For example, the following code suppresses (prevents) violations of the **DuplicateStringLiteral**
rule within the `MyClass` class, as well as suppressing the *IfStatementBraces* and *ThrowException*
rules within its `getCount()` method.

```
    @SuppressWarnings('DuplicateStringLiteral')
    class MyClass {
        def y = 'x'
        def z = 'x'

        @SuppressWarnings(['IfStatementBraces', 'ThrowException'])
        int getCount() {
            if (!ready) throw new Exception('Not ready')
        }
    }
```

You can specify "all" or "CodeNarc" within `@SuppressWarnings` (e.g. `@SuppressWarnings('all')` or
`@SuppressWarnings('CodeNarc')`) to suppress violations for all CodeNarc rules for the annotated scope.


## Customizing Rule Descriptions Shown in the HTML Report

The descriptions for rules used in a **CodeNarc** report are shown at the bottom of the resulting
HTML report. Those rule descriptions are retrieved from a *ResourceBundle*. This *ResourceBundle*
consists of a base (parent) bundle, provided with the **CodeNarc** distribution, and an optional
custom (child) bundle, which may be provided by the user.

Because this is based on the standard Java *ResourceBundle* facility, you can customize (override) or
translate these descriptions by providing your own locale-specific message properties file on the
classpath. For example, a German one would be named "codenarc-messages_de.properties".

The rule description message key is of the form "**\*rule-name\*.description.html**". Examples include
"BooleanInstantiation.description.html" and "CatchThrowable.description.html".

(Note that the rule description included in the XML report is specified by the key **\*rule-name\*.description**).

See the javadoc for the `java.util.ResourceBundle` class for more information.


### Include Substitutable Message Parameters in a Rule Description

You can optionally include message parameters within a rule description, whether that description
is specified in a ResourceBundle properties file or through the `description` property of the
rule. Use the standard Groovy template parameter syntax: `$\{..\}`. You can reference rule property
values through the "rule" object within the template parameter closure. For example, this description
includes the rule `priority` within the rule description:
`"Description for rule with priority $\{rule.priority\}"`.


### The Base Messages Resource Bundle

The base *ResourceBundle* provided with the **CodeNarc** distribution  has a basename of
"codenarc-base-messages". The rules provided with **CodeNarc** all have
associated descriptions within the "codenarc-base-messages.properties" file.

You can provide a locale-specific variant of this file on the classpath if you wish, but
in most scenarios, the recommended approach for customizing rule descriptions is to provide a
custom messages bundle file, as described below.


### The Custom Messages Resource Bundle

You can optionally provide descriptions for new rules or override existing rule descriptions
by placing a "codenarc-messages.properties" file on the classpath. This is also a *ResourceBundle*
file, so it can have locale-specific variants, if needed. If this bundle (file) is present, it
is searched first. If the desired message key is not found, only then is the base message bundle
checked.

You can provide rule description entries within this file for custom rules that you create.
You can also include rule description entries for existing rules, in which case the descriptions in this
file override the default descriptions.


### Customizing the Name and Description for an Existing Rule

You may wish to customize both the *name* and the description for an existing rule. For example,
you may want to have multiple instances of a generic rule, each configured differently. Or you
may just want to make the rule name more clearly denote its specific purpose.

For example, in the following excerpt from a *RuleSet* file, because the *name* property is
customized for the **IllegalRegexRule**, the new rule name "AuthorTagNotAllowed" will show up
at the bottom of the HTML report, instead of the default rule name "IllegalRegex".

```
    *rule class='org.codenarc.rule.generic.IllegalRegexRule'*
        *property name="name" value="AuthorTagNotAllowed"/*
        *property name='regex' value='\@author'/*
    */rule*
```

The rule description must be added to the custom messages resource bundle file
("codenarc-messages.properties"). The message key will be "AuthorTagNotAllowed.description.html" to set
the description used in the HTML report, and "AuthorTagNotAllowed.description" for the description
used in the XML report.


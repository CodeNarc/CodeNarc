---
layout: default
title: CodeNarc - Creating a RuleSet
---

# CodeNarc - Creating a RuleSet

**Contents**
  * [Preferred Way To Configure a RuleSet](#Preferred)
  * [Creating a Groovy RuleSet File](#CreatingAGroovyRuleSetFile)
  * [Creating an XML RuleSet File](#CreatingAnXmlRuleSetFile)

<a name="Preferred"/>
## Preferred Way To Configure a RuleSet

**NOTE:** The preferred way to configure the rules that **CodeNarc** will use is to create a custom
*RuleSet* specifying the rule names (i.e., without depending on the *RuleSet* files provided with **CodeNarc**.
This allows finer control over your custom RuleSet and insulates you from the provided *RuleSets* that can
(and often do) change from release to release.

###  Just Copy and Tweak One of the Starter RuleSet Files

See the [Starter RuleSet - All Rules By Category](./StarterRuleSet-AllRulesByCategory.groovy.txt)). It contains
all of the rules provided with the current version of **CodeNarc**, organized by category. Just delete or
comment out the rules you don't want to use.

Alternatively, there is a [Starter RuleSet - All Rules](./StarterRuleSet-AllRules.groovy.txt). It contains all
of the rules provided with the current version of **CodeNarc**, in alphabetical order. Just delete or
comment out the rules you don't want to use.

### The Other RuleSet Options Still Work

You can still create your own custom [Groovy](A_Sample_Groovy_RuleSet_Using_the_Old_Syntax) *RuleSet* using the
older syntax. You can even mix the new rule-name-only syntax with the older Groovy DSL syntax. It's all good!

You can also continue to use the predefined *RuleSets* distributed with **CodeNarc** or you can create your own
[XML](Creating_an_XML_RuleSet_File) *RuleSet*. See the site navigation menu for a list of the *RuleSets*
provided out of the box by **CodeNarc**.


<a name="CreatingAGroovyRuleSetFile"/>
## Creating a Groovy RuleSet File

**CodeNarc** provides a Groovy DSL (domain-specific language) for defining *RuleSets*.

The preferred syntax for defining a *RuleSet* is to specify the list of rules using only the rule names.
As mentioned above, this allows finer control over your custom RuleSet and insulates you from the provided
*RuleSets* that can (and often do) change from release to release.

### A Sample Groovy RuleSet

Here is an example of a Groovy *RuleSet* file using the preferred syntax:

```
    ruleset {
        description 'A custom Groovy RuleSet'
    
        CyclomaticComplexity {
            maxMethodComplexity = 1
        }
    
        ClassName
    
        MethodName
    
        ConfusingTernary(priority:3)
    
        StatelessClass {
            name = 'StatelessDao'
            applyToClassNames = '*Dao'
        }
    }
```

Things to note:

  * The Groovy *RuleSet* file itself must be accessible on the classpath.

  * Each rule is specified by its rule *name*.

  * The **StatelessClass** rule redefines the *name* for that rule instance. You can have multiple
      instances of the same rule class as long as they have unique rule *names*.

  * You can optionally configure a rule instance by specifying rule properties in a *Map* after the
      rule *name* (see **ConfusingTernary** in the example). Or you can specify rule properties in a
      *Closure* after the rule *name* (see **CyclomaticComplexity** and **StatelessClass** in the example).

  * The easiest way to create a new custom *RuleSet* is to copy the
      [Starter RuleSet](./StarterRuleSet-AllRulesByCategory.groovy.txt) (as a .groovy file). It
      contains all of the rules provided with the current version of **CodeNarc**, organized by
      category. Just delete or comment out the rules you don't want to use.

And here is an example that mixes both the preferred (new) syntax along with the older syntax, to
illustrate backward-compatibility:

```
    ruleset {
        MethodName
    
        ConfusingTernary(priority:3)
    
        StatelessClass {
            name = 'StatelessDao'
            applyToClassNames = '*Dao'
        }
    
        // Old style
        rule(org.codenarc.rule.basic.ThrowExceptionFromFinallyBlockRule) {
            priority = 3
        }
    
        // Old style
        ruleset('rulesets/dry.xml')
    }
```


### A Sample Groovy RuleSet Using the Old Syntax

Here is an example of a Groovy *RuleSet* file using the older syntax:

```
    import org.codenarc.rule.basic.ThrowExceptionFromFinallyBlockRule
    
    ruleset {
    
        description 'A sample Groovy RuleSet'
    
        ruleset('rulesets/basic.xml') {
            'CatchThrowable' {
                priority = 1
                enabled = false
            }
            'EqualsAndHashCode' priority:3
            exclude 'Empty*'
        }
    
        rule(ThrowExceptionFromFinallyBlockRule) {
            priority = 3
        }
    
        rule("rules/MyCustomRuleScript.groovy")
    
        ruleset('MyGroovyRuleSet.groovy')
    }
```

Things to note:

  * The Groovy *RuleSet* file itself must be accessible on the classpath.

  * The "outer" **ruleset** defines the contents of the *RuleSet* (within a *closure*).
    It can include an optional **description** and any combination of **ruleset**
    (other *RuleSets*) and **rule** statements (individual *Rules*).

About the "inner" **ruleset** statements:

  * Each **ruleset** statement loads a *RuleSet* file. The path specifies either a
      Groovy file or an XML file. By default, the paths specified are relative to the classpath.
      But these paths may be optionally prefixed by any of the valid java.net.URL prefixes, such as
      "file:" (to load from a relative or absolute path on the filesystem), or "http:".

  * The 'rulesets/basic.xml' *RuleSet* file is interpreted as an XML
      file based on the '.xml' extension. Likewise, the 'MyGroovyRuleSet.groovy' file
      is interpreted as a Groovy *RuleSet* file.

  * The *RuleSet* can be customized by following the *RuleSet* path with an (optional)
      *closure*, containing any combination of the following:

      1. **Rule Configurations** -- A **ruleset** statement can optionally provide configuration
          of the properties for individual rules within the *RuleSet*. Specify the name of the rule
          (as a *String*), followed by its configuration. (Remember to specify the rule *name*, not
          the class name. In most cases, the rule *name* is the class name without the "Rule" suffix).
          The name of the *Rule* can be followed by:

             a. A *Map* of property names and values. See 'EqualsAndHashCode' within the example.

             b. A *closure* containing property assignments statements. See 'CatchThrowable' within the example.

          Properties set this way can be of type *String*, *int*, *long* or *boolean*.

      2. **Rule Filtering (include or exclude statements)** -- A **ruleset** statement can
          optionally specify **include** and/or **exclude** pattern(s) of rule names to include or exclude
          from the *RuleSet*. See [Filtering Rules Within a RuleSet](#Filtering_Rules_Within_a_RuleSet).

About the **rule** statements:

  * Each **rule** statements loads a single *Rule*.

  * The **rule** statement must specify either:

      a. The class name for a *Rule*. See `ThrowExceptionFromFinallyBlockRule` within the example.
            The *Rule* class must be available on the classpath.

      b. The path to a *Rule Script* file. See "MyGroovyRuleSet.groovy" within the example.
          By default, the paths specified are relative to the classpath. But these paths may be
          optionally prefixed by any of the valid java.net.URL prefixes, such as
          "file:" (to load from a relative or absolute path on the filesystem) or "http:".

  * A **rule** can optionally provide configuration of the *Rule* properties by specifying a *closure*
    containing property assignment statements. See `ThrowExceptionFromFinallyBlockRule` within the
    example. As within **ruleset** statements, properties set this way can be of type *String*, *int*,
    *long* or *boolean*.


<a name="CreatingAnXmlRuleSetFile"/>
## Creating an XML RuleSet File

The XML schema for a **CodeNarc** *RuleSet* file is embodied in the "ruleset-schema.xsd" file which
is included within the **CodeNarc** jar. It contains three sections, all of which are optional, though
the sections must be in the order listed:

| **XML Tag**           | **Purpose**                                                   | **How many are allowed** |
|-----------------------|---------------------------------------------------------------|--------------------------|
| `<description>` | Describe the purpose of the *RuleSet*                         |  Zero or one |
| `<ruleset-ref>` | Include a nested *RuleSet*, optionally configuring and/or filtering the rules within it. The path to the *RuleSet* can specify either an XML file or a Groovy *RuleSet* file.  | Zero or more |
| `<rule>`        | Include a single rule; specify its fully-qualified classname  |  Zero or more |
| `<rule-script>` | Include a single rule implemented by a groovy script; specify the path of the script. The path is relative to the classpath by default, but can optionally specify a URL prefix.| Zero or more |


### A Sample XML RuleSet

Here is an example XML *RuleSet* file:

```
    <ruleset xmlns="http://codenarc.org/ruleset/1.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://codenarc.org/ruleset/1.0 http://codenarc.org/ruleset-schema.xsd"
            xsi:noNamespaceSchemaLocation="http://codenarc.org/ruleset-schema.xsd">
    
        <description>Sample rule set</description>
    
        <ruleset-ref path='rulesets/imports.xml'>
            <rule-config name='DuplicateImport'>
                <property name='priority' value='1'/>
            </rule-config>
        </ruleset-ref>
    
        <ruleset-ref path='rulesets/basic.xml'>
            <exclude name='StringInstantiation'/>
        </ruleset-ref>
    
        <rule class='org.codenarc.rule.generic.IllegalRegexRule'>
            <property name="name" value="AuthorTagNotAllowed"/>
            <property name='regex' value='\@author'/>
        </rule>
    
        <rule-script path='rules/MyStaticFieldRule.groovy'/>
    
    </ruleset>
```

Things to note:

  * The *RuleSet* file itself must be accessible on the classpath.

  * The top-level `<ruleset>` element must include the namespace declaration shown.

About the `<ruleset-ref>` elements:

  * By default, The *path* of the `<ruleset-ref>` is relative to the classpath.
    But these paths may be optionally prefixed by any of the valid java.net.URL prefixes, such as
    "file:" (to load from a relative or absolute path on the filesystem) or "http:".

  * You can optionally set arbitrary properties for the rules within the *RuleSet* by
    including one or more `<property>` elements within a `<rule-config>` element. In the example
    above, the **DuplicateImportRule** *priority* property is set to 1. Properties set
    this way can be of type *String*, *int*, *long* or *boolean*.

  * Remember that the *name* for a `<rule-config>` specifies the rule *name*, not the class name.
    (In most cases, the rule *name* is the class name without the "Rule" suffix.)

About the `<rule>` elements:

  * You must specify the fully-qualified class name for each *Rule*.

  * The *Rule* class must be available on the classpath.

  * You can optionally set arbitrary properties for individual rules by including a
    `<property>` element within a \*rule\*. For the **IllegalRegexRule** rule above,
    the *name* property is set to "AuthorTagNotAllowed" and the *regex* property is
    set to "@author". Properties set this way can be of type *String*,
    *int*, *long* or *boolean*.

  * Because the *name* property is customized for the **IllegalRegexRule**,
    the localized rule description will be retrieved from the custom messages resource
    bundle file ("codenarc-messages.properties"), if that bundle file exists, otherwise
    a default generic message will be used. The resource bundle message key will be based
    on the customized name. In this case, the message key will be "AuthorTagNotAllowed.description".

About the `<rule-script>` elements:

  * You must specify the *path* for the rule script Groovy file. By default, this *path* is
    relative to the classpath. But these paths may be optionally prefixed by any of the
    valid java.net.URL prefixes, such as "file:" (to load from a relative or absolute path
    on the filesystem) or "http:".

  * The class defined within the rule script Groovy file must implement the
    `org.codenarc.rule.Rule` interface.

  * You can optionally set arbitrary properties for the rule by including a
    `<property>` element within a \*rule-script\* the same way you can for
    a `<rule>`. That is not shown here.

<a name="Filtering_Rules_Within_a_RuleSet"/>
## Filtering Rules Within a RuleSet

You can use the `<include>` and `<exclude>` elements within a `<ruleset-ref>` to filter a *RuleSet*
and include and/or the exclude individual rules. In the following *RuleSet* excerpt, the entire
"rulesets/basic.xml" *RuleSet* is included, except for the **BooleanInstantiationRule** and
**StringInstantiationRule**.

```
    ruleset('rulesets/basic.xml') {
        exclude 'BooleanInstantiation'
        exclude 'StringInstantiation'
    }
```

And here is the same example in XML *RuleSet* format:

```
    <ruleset-ref path='rulesets/basic.xml'>
        <exclude name='BooleanInstantiation'/>
        <exclude name='StringInstantiation'/>
    </ruleset-ref>
```

Alternatively, you may wish to explicitly specify the rules that you want included from a *RuleSet*
rather than those that are excluded.  In the following *RuleSet* excerpt, ONLY the
**ReturnFromFinallyBlockRule** and **StringInstantiationRule** rules are included from the
 "rulesets/basic.xml" *RuleSet*.

```
    ruleset('rulesets/basic.xml') {
        include 'ReturnFromFinallyBlockRule'
        include 'StringInstantiation'
    }
```

And here is the same example in XML *RuleSet* format:

```
    <ruleset-ref path='rulesets/basic.xml'>
        <include name='ReturnFromFinallyBlockRule'/>
        <include name='StringInstantiation'/>
    </ruleset-ref>
```

**Note**: In all cases, the rule *name* is specified, not the class name. (In most cases, the rule *name* is
the class name without the "Rule" suffix.)

**Note**: If you specify at least one `<include>`, then ONLY rules matching an `<include>` will be included.

**Note**: If you specify an `<include>` and an `<exclude>` for a rule, then the `<exclude>` takes precedence.


### Using Wildcards Within \*include\* and \*exclude\* Names

You may optionally include wildcard characters ('*' or '?') in the rule *name* for both
`<include>` and an `<exclude>` elements. The wildcard character '*' within the *name* matches a
sequence of zero or more characters in the rule name, while the wildcard character '?' within the
*name* matches exactly one character in the rule name.

In the following *RuleSet* excerpt, all rules matching '*Instantiation' are included from the
"rulesets/basic.xml" *RuleSet*. In this case, that will include the **StringInstantiationRule**
and **BooleanInstantiationRule** rules.

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    <ruleset-ref path='rulesets/basic.xml'>
        <include name='*Instantiation'/>
    </ruleset-ref>
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


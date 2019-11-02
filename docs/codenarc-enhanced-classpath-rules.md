---
layout: default
title: CodeNarc - About the Enhanced Classpath Rules
---  

# CodeNarc - About the Enhanced Classpath Rules

Several newer rules use a later compilation phase for parsing of the Groovy source code,
allowing **CodeNarc** to use a richer and more complete Abstract Syntax Tree (AST). The
downside is that the later compiler phase requires **CodeNarc** to have the application classes
being analyzed, as well as any referenced classes, on the classpath.

NOTE: If a rule requiring a later compiler phase is included in the active **CodeNarc** ruleset and
enabled and one or more of the required classes is not on the classpath, then **CodeNarc** will
log a Log4J WARN message for each source file that contains the missing references.


## Grails-CodeNarc Plugin

The [Grails CodeNarc Plugin](http://www.grails.org/plugin/codenarc/) supports the new *enhanced classpath*
rules out of the box. You are free to include these rules in your **CodeNarc** ruleset when you use the
Grails CodeNarc plugin.


## CodeNarc Ant Task - Expanding the Classpath

**CodeNarc** users that use the Ant Task directly will need to adjust their Ant scripts to expand
the classpath, if they want to take advantage of these special new rules. Classes needed when compiling analysed
sources can be added to the classpath via **classpathRef** property or **classpath** nested element of CodeNarc Ant
task which are documented in the [CodeNarc Ant Task](./codenarc-ant-task.html) page.


## CodeNarc - Other Tools and Frameworks - Expanding the Classpath?


Other tools/frameworks that use **CodeNarc** will most likely not be able to use these new rules
initially, because the tool classpath will not support it. The hope is that the other **CodeNarc**
tools will eventually be enhanced to provide the expanded classpath to **CodeNarc** – either optionally
or always – so that they can also take advantage of these new rules.

See the [CodeNarc - Integration with Other Tools / Frameworks](./codenarc-other-tools-frameworks.html) for
links to other tools and frameworks that use **CodeNarc**.

TBD...

## Dual Mode Rules

Historically some rules were deficient because they they ran against AST produced in earlier compilation phases and
thus were lacking all the necessary information to fully determine if a violation actually occurred or not. These
rules have now been improved and are able to run in enhanced mode against AST produced by later compilation phases
providing better analysis. Examples of such rules include `CloseWithoutCloseable` and
`CompareToWithoutComparable`. For backwards compatibility reasons enhanced mode is not enabled on dual mode rules
by default.

If **CodeNarc** analysis has been set up to include the classes referenced from analysed code on the classpath then
you might consider enabling enhanced mode for dual mode rules globally by setting `org.codenarc.enhancedMode`
system property to `true`.

## Now what?

We also expect to continue to introduce more of these "special" enhanced-classpath rules, since
that greatly expands the capabilities for **CodeNarc** rules.

Anyone who does not want to use the new rules or is unable to expand the classpath as required,
can just omit these special rules from the **CodeNarc** ruleset or else disable the rules.

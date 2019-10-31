---
layout: default
title: CodeNarc - Creating a Rule
---

# CodeNarc - Creating a Rule

**CodeNarc** includes many predefined rules, but you can also create your own
custom rules. See the site navigation menu for a list of rules provided out
of the box by **CodeNarc**. **CodeNarc** provides abstract superclasses and
helper classes for creating new rules.

The [**codenarc create-rule**](./codenarc-developer-guide.html#The_codenarc_Command-line_Script)
command-line script is the recommended way to create a new rule.

Also see this [screencast](http://www.youtube.com/watch?v=ZPu8FaZZwRw) showing how
easy it is to create a new rule.


## The Rule Interface

All rules must implement the `org.codenarc.rule.Rule` interface. This
interface specifies that all rules must define a *name*, a *priority* (from 1 to 3),
and also implement the `List applyTo(SourceCode sourceCode)` method. The
method returns the *List* of `Violation` objects that result from applying
the rule to a single source file.


## The AbstractRule Class

The `org.codenarc.rule.AbstractRule` class is the abstract superclass
(or ancestor) for all rules provided with **CodeNarc**. It provides many standard
properties and helper methods for subclasses, as described in
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#Standard_Properties_for_Configuring_Rules).

### {A Sample Rule Subclass of AbstractRule}

Here is an example rule class that is a subclass of `AbstractRule`:

```
    import org.codenarc.rule.AbstractRule
    import org.codenarc.source.SourceCode
    
    /**
     * Sample rule. Checks for static fields.
     */
    class MyStaticFieldRule extends AbstractRule {
        String name = 'MyStaticField'
        int priority = 2
    
        void applyTo(SourceCode sourceCode, List*Violation* violations) {
            sourceCode.ast.classes.each { clazz -*
                clazz.fields.each { fieldNode -*
                    if (fieldNode.static) {
                        violations ** createViolation(sourceCode, fieldNode, "The field ${fieldNode.name} is static")
                    }
                }
            }
        }
    }
```

Things to note about `MyStaticFieldRule` class:

  * It extends `AbstractRule`.

  * It provides *name* and *priority* properties as mandated by the `Rule` interface.

  * It implements the `void applyTo(SourceCode sourceCode, List*Violation* violations)` method
    which is declared *abstract* in the `AbstractRule` superclass.

  * It accesses the AST for the source code, which is an instance of the
    `org.codehaus.groovy.ast.ModuleNode` class from Groovy.

  * It uses the `createViolation()` helper method from `AbstractRule`.

See the **CodeNarc** source code for other examples (look for rule classes that are
direct subclasses of `AbstractRule`).


## The AbstractAstVisitorRule and AbstractAstVisitor Classes

Many of the rules included with **CodeNarc** are implemented using the *Visitor*
pattern, as supported by the Groovy AST (Abstract Syntax Tree).
See the `ClassCodeVisitorSupport` class within the Groovy distribution
([Javadocs](http://groovy.codehaus.org/api/index.html)).

### A Sample Rule Using AbstractAstVisitorRule and AbstractAstVisitor

Here is an example rule class that is a subclass of `AbstractAstVisitorRule`
that uses an associated AST *Visitor* class that is a subclass of
`AbstractAstVisitor`. This is the code for the **EmptyTryBlock** rule included
with **CodeNarc**.

```
    import org.codenarc.rule.AbstractAstVisitor
    import org.codenarc.rule.AbstractAstVisitorRule
    import org.codehaus.groovy.ast.stmt.TryCatchStatement
    import org.codenarc.util.AstUtil
    
    class EmptyTryBlockRule extends AbstractAstVisitorRule {
        String name = 'EmptyTryBlock'
        int priority = 2
        Class astVisitorClass = EmptyTryBlockAstVisitor
    }
    
    class EmptyTryBlockAstVisitor extends AbstractAstVisitor  {
        void visitTryCatchFinally(TryCatchStatement tryCatchStatement) {
            if (AstUtil.isEmptyBlock(tryCatchStatement.tryStatement)) {
                addViolation(tryCatchStatement, "The try statement block is empty")
            }
            super.visitTryCatchFinally(tryCatchStatement)
        }
    }
```

Things to note about this example:

  * This file contains two classes. The `EmptyTryBlockRule` class extends
    `AbstractAstVisitorRule`. The `EmptyTryBlockAstVisitor` extends
    `AbstractAstVisitor`.

  * `EmptyTryBlockRule` includes an *astVisitorClass* property that specifies
    that it uses the `EmptyTryBlockAstVisitor` class.

  * `EmptyTryBlockAstVisitor` implements the `void visitTryCatchFinally(TryCatchStatement)`
    *visitor* method defined by the `ClassCodeVisitorSupport` so that it will
    *visit* every try/catch statement within the source code.

  * It uses the `AstUtil` utility class.

See the **CodeNarc** source code and javadocs for more information and further examples.


## Creating a Rule Script

You can also create a new rule using a Groovy script file (typically a .groovy file).
The script must still define a class that implements the `org.codenarc.rule.Rule`
interface.

The main advantage is that the rule does not have to be compiled into a **.class**
file first, so you can avoid the compile/build phase, and its associated hassles.
The Groovy script file is parsed (and compiled) at runtime.

### A Sample Rule Script

Here is an example rule script. Note that this is the same source code as in the
sample rule class shown
[above](A_Sample_Rule_Subclass_of_AbstractRule).

```
    import org.codenarc.rule.AbstractRule
    import org.codenarc.source.SourceCode
    
    /**
     * Sample rule script. Checks for static fields.
     */
    class MyStaticFieldRule extends AbstractRule {
        String name = 'MyStaticField'
        int priority = 2
    
        void applyTo(SourceCode sourceCode, List*Violation* violations) {
            sourceCode.ast.classes.each { clazz -*
                clazz.fields.each { fieldNode -*
                    if (fieldNode.static) {
                        violations ** createViolation(sourceCode, fieldNode, "The field ${fieldNode.name} is static")
                    }
                }
            }
        }
    }
```

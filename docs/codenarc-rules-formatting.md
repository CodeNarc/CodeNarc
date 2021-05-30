---
layout: default
title: CodeNarc - Formatting Rules
---  

# Formatting Rules  ("*rulesets/formatting.xml*")


## BlankLineBeforePackage Rule

*Since CodeNarc 0.21*

Makes sure there are no blank lines before the package declaration of a source code file.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## BlockEndsWithBlankLine Rule

*Since CodeNarc 1.1*

Checks that code blocks such as method bodies, closures and control structure bodies do not end with an empty line.

Example of violations:

```
    boolean not(boolean value) {
        !value
                                // violation
    }

    3.times {
        println 'hello!'
                                // violation
    }

    for (value in []) {
        println value
                                // violation
    }

    for (i = 0; i * 3; i++) {
        println i
                                // violation
    }

    int j = 0
    while (j * 3) {
      println j++
                                // violation
    }

    if (ready) {
        println 'ready'
                                // violation
    } else {
        println 'not ready'
                                // violation
    }

    try {
        throw new Exception()
                                // violation
    } catch (Exception e) {
        println 'exception'
                                // violation
    } finally {
        println 'finally'
                                // violation
    }

    switch (true) {
        default:
            println 'switch'
                                // violation
    }

    // Known Limitation: If a Closure is within another expression and the closing brace is not followed by anything else on the same line

    def list = [
        123,
        { id -*
                                // Known limitation: should be a violation, but is not
        }
    ]
```


## BlockStartsWithBlankLine Rule

*Since CodeNarc 1.1*

Checks that code blocks such as method bodies, closures and control structure bodies do not start with an empty line.

Example of violations:

```
    boolean not(boolean value) {
                                // violation
        !value
    }

    3.times {
                                // violation
        println 'hello!'
    }

    for (value in []) {
                                // violation
        println value
    }

    for (i = 0; i * 3; i++) {
                                // violation
        println i
    }

    int j = 0
    while (j * 3) {
                                // violation
      println j++
    }

    if (ready) {
                                // violation
        println 'ready'
    } else {
                                // violation
        println 'not ready'
    }

    try {
                                // violation
        throw new Exception()
    } catch (Exception e) {
                                // violation
        println 'exception'
    } finally {
                                // violation
        println 'finally'
    }

    switch (true) {
                                // violation
        default:
            println 'switch'
    }

```


## BracesForClass Rule

*Since CodeNarc 0.15*

Checks the location of the opening brace (\{) for classes. By default, requires them on the same
line, but the `sameLine` property can be set to false to override this.

NOTE: This rule ignores annotation types, e.g. `@interface MyAnnotation {}`.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## BracesForForLoop Rule

*Since CodeNarc 0.15*

Checks the location of the opening brace (\{) for for loops. By default, requires them on the same line, but the
`sameLine` property can be set to false to override this.


## BracesForIfElse Rule

*Since CodeNarc 0.15*

Checks the location of the opening brace (\{) for if statements. By default, requires them on the same
line, but the `sameLine` property can be set to false to override this.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| sameLine                    | If `true`, then the opening brace (\{) for if statement should be on the same line. |  `true` |
| validateElse                | To enable else checking, set the property to `true` | `false`            |
| elseOnSameLineAsClosingBrace| If `true`, then the else statement should be on the same line the same as sameLine as closing brace (\}) | the same as *sameline* |
| elseOnSameLineAsOpeningBrace| If `true`, then the else statement should be on the same line the same as sameLine as opening brace (\{) | the same as *sameline* |

## BracesForMethod Rule

*Since CodeNarc 0.15*

Checks the location of the opening brace (\{) for constructors and methods. By default, requires them on the same
line, but the `sameLine` property can be set to false to override this.


## BracesForTryCatchFinally Rule

*Since CodeNarc 0.15*

Checks the location of the opening brace (\{) for try statements. By default, requires them on the line, but the `sameLine` property can be set to false to override this.


## ClassEndsWithBlankLine Rule

*Since CodeNarc 1.3*

Check whether the class ends with a blank line. By default, it enforces that there must be a blank line before
the closing class brace, except:

  * If the class is synthetic (generated)
  * If the class is empty and is written in a single line
  * If the class is a Script class

A blank line is defined as any line that does not contain any visible characters.

This rule can be configured with the following properties:

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreSingleLineClasses     | a boolean property to forbid single line classes.If it is false,then single line classes are considered a violation. | `true` |
| ignoreInnerClasses          | A boolean property to ignore inner classes. If it is *false* then inner classes can cause violations. | `false` |
| blankLineRequired           | a boolean property to define if there may be a blank line before the closing class brace. If it is false, the last line before the brace must not be blank. Otherwise, it must be blank. | `true` |

Example of violations:

If *ignoreSingleLineClasses* is `true` and *blankLineRequired* is `true`

```
            class Foo {
                int a

                void hi() {
                }
            }
```

If *ignoreSingleLineClasses* is `false` and *blankLineRequired* is `true`

```
            class Foo extends Bar*String* { }
```


If *ignoreSingleLineClasses* is `true` and *blankLineRequired* is `false`

```
            class Foo {
                int a

                void hi() {
                }

            }
```

If *ignoreSingleLineClasses* is `false` and *blankLineRequired* is `false`

```
            class Foo {
                int a

                void hi() {
                }

            }
```


## ClassStartsWithBlankLine Rule

*Since CodeNarc 1.3*

Check whether the class starts with a blank line. By default, it enforces that there must be a blank line after
the opening class brace, except:

  * If the class is synthetic (generated)

  * If the class is empty and is written in a single line

  * If the class is a Script class

A blank line is defined as any line that does not contain any visible characters.

This rule can be configured with the following properties:

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreSingleLineClasses     | A boolean property to ignore single line classes. If it is false, then single line classes are considered a violation. | `true` |
| ignoreInnerClasses          | A boolean property to ignore inner classes. If it is *false* then inner classes can cause violations. | `false` |
| blankLineRequired           | A boolean property to define if there may be a blank line after the opening class brace. If it is false, the first content after the brace must not be a blank line. Otherwise, it must be a blank line.                                       | `true` |

Example of violations:

If *ignoreSingleLineClasses* is `true` and *blankLineRequired* is `true`

```
            class Foo {
                int a

                void hi() {
                }
            }
```

If *ignoreSingleLineClasses* is `false` and *blankLineRequired* is `true`

```
            class Foo extends Bar*String* { }
```


If *ignoreSingleLineClasses* is `true` and *blankLineRequired* is `false`

```
            class Foo {

                int a

                void hi() {
                }

            }
```

If *ignoreSingleLineClasses* is `false` and *blankLineRequired* is `false`

```
            class Foo {
                int a

                void hi() {
                }

            }
```


## ClosureStatementOnOpeningLineOfMultipleLineClosure Rule

*Since CodeNarc 0.20*

Checks for closure logic on first line (after `-\`*) for a multi-line closure. That breaks the symmetry
of indentation (if the subsequent statements are indented normally), and that first statement can be easily
missed when reading the code.

Example of violations:

```
    def closure = { name -* println name
        addToCounts()
        println “done” }
```


## ConsecutiveBlankLines Rule

*Since CodeNarc 0.21*

Makes sure there are no consecutive lines that are either blank or whitespace only. This reduces the need to scroll
further than necessary when reading code, and increases the likelihood that a logical block of code will fit on one
screen for easier comprehension.

Example of violation:

```
    def name


    def value



    def id
```

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## FileEndsWithoutNewline Rule

*Since CodeNarc 0.21*

Makes sure each source file ends with a newline character.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## Indentation Rule

*Since CodeNarc 1.1*

Check the indentation (spaces only; not tabs) for class, field and method declarations, and statements.

This rule is limited, and somewhat opinionated. The default is 4 spaces per indentation level.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| spacesPerIndentLevel        | The number of spaces that make up a single level of indentation. | 4 |

Known Limitations include:

  * Checks spaces only (not tabs)
  * Does not check comments
  * Does not check line-continuations (i.e., checks only the first line of a statement)
  * Does not check multiple statements/members on the same line (only checks the first one)
  * Does not check Map entry expressions
  * Does not check List expressions
  * Does not check calls to `this()` and `super()` within a constructor
  * When classes, methods or fields have annotations, the indentation of the annotation is checked, not the actual member. And only the first annotation is checked, if there is more than one.

Example of violations:

```
// Indent Levels:
0...1...2...3...4...5

class MyClass {                                 // CORRECT
    protected int count                         // CORRECT
  private static final NAME = "Joe"             // violation
           def max, min                         // violation on "max" only

 private String doStuff() {                     // violation
        def internalCounts = [1, 4, 2]          // CORRECT
            id.trim()                           // violation
    }

    private void executeOtherOne() {            // CORRECT
        try {
          executeWithArgs(args)                 // violation
        } catch(Throwable t) {
                       t.printStackTrace()      // violation
        }
        finally {
                closeResources()                // violation
        }
    }
}
```


## LineLength Rule

*Since CodeNarc 0.15*

Checks the maximum length for each line of source code. It checks for number of characters, so lines that
include tabs may appear longer than the allowed number when viewing the file. The maximum line length can
be configured by setting the length property, which defaults to 120.

NOTE: This rule does not support the @SuppressAnnotations annotation or the classname-based rule properties
(applyToClassNames, doNotApplyToClassNames) to enable/disable the rule. If you want to specify or restrict
where this rule is applied, you must use the file-based rule properties: applyToFileNames, doNotApplyToFileNames,
applyToFilesMatching and doNotApplyToFilesMatching.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| length                      | The maximum line length allowed. | 120 |
| ignoreImportStatements      | If `true`, then do not apply this rule to import statements. | `true` |
| ignorePackageStatements     | If `true`, then do not apply this rule to package statements.| `true` |
| ignoreLineRegex             | If specified, then ignore lines matching this regular expression.| `null` |

## MissingBlankLineAfterImports Rule

*Since CodeNarc 0.21*

Makes sure there is a blank line after the imports of a source code file.

Example of violation:

```
    import org.apache.commons.lang.StringUtils
    class MyClass { }                       // violation
```

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## MissingBlankLineAfterPackage Rule

*Since CodeNarc 0.21*

Makes sure there is a blank line after the package statement of a source code file.

Example of violation:

```
  package org.codenarc
  import java.util.Date                     // violation

  class MyClass {
      void go() { /* ... */ }
  }
```

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

## MissingBlankLineBeforeAnnotatedField

*Since CodeNarc 2.1*

Checks that there is a blank line before a field declaration that uses annotations. 

Ignore field declarations where:
 - The previous line contains a comment 
 - The declaration (annotations) start on the first line of the class
 - All annotations are on the same line as the field declaration.

Examples of violations:
```
    class MyClass {
        // No violations for field declarations preceded by a comment
        @Delegate
        AutoCloseable stream
        
        String publicField                  // violation
        @PackageScope
        String packageScopedField
    }
```

## SpaceAfterMethodDeclarationName Rule

*Since CodeNarc 2.1*

Check whether method declarations do not contain unnecessary whitespace between method name and the opening parenthesis 
for parameter list.

Examples of violations:

```
    class ClassWithWhitespaceInConstructorDeclaration {
        
        ClassWithWhitespaceInConstructorDeclaration () { //violation
        }
        
        void methodWithWhitespaceInDeclaration () { //violation
        }
    }
```

## SpaceAfterMethodCallName Rule

*Since CodeNarc 2.1*

Checks that there is no whitespace after the method name when a method call contains parenthesis or that there 
is at most one space after the method name if the call does not contain parenthesis.

Examples of violations:

```
    aMethod ("arg") //violation
    
    aMethod  "arg" //violation
    
    throw new Exception () //violation
    
```

## SpaceAfterCatch Rule

*Since CodeNarc 0.18*

Check that there is exactly one space (blank) after the `catch` keyword and before the opening parenthesis.

Examples of violations:

```
    try { } catch(Exception e) { }          // violation
    try { } catch  (Exception e) { }        // violation
```


## SpaceAfterComma Rule

*Since CodeNarc 0.18*

Checks that there is at least one space or whitespace following each comma. That includes checks for method
and closure declaration parameter lists, method call parameter lists, Map literals and List literals.

Known limitations:

  * May not catch actual violations if the source line contains unicode character literals, e.g. `'\\u00A0'`

Examples of violations:

```
    def value = calculate(1,399, 'abc')         // violation on parameter 399

    def method1(int a,String b) { }             // violation on parameter b

    def closure1 = { int a,String b -* }        // violation on parameter b

    def list1 = [a,b, c]                        // violation on list element b

    def map1 = [a:1,b:2, c:3]                   // violation on map element b:2
```


## SpaceAfterClosingBrace Rule

*Since CodeNarc 0.18*

Check that there is at least one space (blank) or whitespace after each closing brace ("\{") for
method/class/interface declarations, closure expressions and block statements.

A closure expression followed by a dot operator (.), a comma, an opening parenthesis, a closing parenthesis, an
opening square brace, a closing square brace (]), the spread-dot operator (*.), a semicolon or the
null-safe operator (?.) does not cause a violation.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| checkClosureMapEntryValue   | DEPRECATED. Ignored. | `true` |

Known limitations:

  * May not catch actual violations if the source line contains unicode character literals, e.g. `'\\u00A0'`

Examples of violations and exceptions:

```
    if (ready) { return 9 }else { }             // violation
    try { doStuff() }finally { }                // violation

    def matching = list.find { it.isReady() }.filter()  // no violation for dot operator
    assert list.every { it.isReady() }, "Error"         // no violation for comma
    def m = [a:123, b:{ println 7 },c:99]               // no violation for comma
    closures.find { c -* c }()                          // no violation for opening parenthesis
    processItems(list.select { it.isReady() })          // no violation for closing parenthesis
    maps.find { m -* m[index] }[index]                  // no violation for opening square bracket
    processItems([{ named("a") }, { named("b")}])       // no violation for closing square bracket
    def names = records.findAll { it.age * 1 }*.name    // no violation for spread operator
    list?.collect { it?.type }?.join(',')               // no violation for null-safe operator
```


## SpaceAfterFor Rule

*Since CodeNarc 0.18*

Check that there is exactly one space (blank) after the `for` keyword and before the opening parenthesis.

Examples of violations:

```
    for(name in names) { }                  // violation
    for  (int i=0; i * 10; i++) { }         // violation
```


## SpaceAfterIf Rule

*Since CodeNarc 0.18*

Check that there is exactly one space (blank) after the `if` keyword and before the opening parenthesis.

Examples of violations:

```
    if(true) { }                            // violation
    if  (true) { }                          // violation
```

## SpaceAfterNotOperator Rule

*Since CodeNarc 2.1*

Check that there are no whitespace characters directly after the not (!) operator.

Examples of violations:

```
    def negatedValue = ! value //violation
    
    if (! items.empty()) { println "not empty" } //violataion
```

## SpaceAfterOpeningBrace Rule

*Since CodeNarc 0.18*

Check that there is at least one space (blank) or whitespace after each opening brace ("\{") for
method/class/interface declarations, closure expressions and block statements.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| checkClosureMapEntryValue   | If `false`, then do not check for whitespace after opening braces for closure expressions that are literal Map values, e.g. `[abc:\{doStuff()\}]`.                                       | `true` |
| ignoreEmptyBlock            | If `true`, then allow for `\{\}` in code | `false` |


Examples of violations:

```
    class MyClass{int count }                   // violation

    interface MyInterface {static final OK = 1 }// violation

    enum MyEnum {OK, BAD }                      // violation

    def myMethod() {int count }                 // violation

    if (ready) {println 9 }                     // violation

    if (ready) {
    } else {println 99}                         // violation

    for (int i=0; i*10; i++) {println i }       // violation

    for (String name in names) {println name }  // violation

    for (String name: names) {println name }    // violation

    while (ready) {println time }               // violation

    try {doStuff()                              // violation
    } catch(Exception e) {x=77 }                // violation
    } finally {println 'error' }                // violation

    list.each {name -* }                        // violation

    shouldFail(Exception) {doStuff() }          // violation
```


## SpaceAfterSemicolon Rule

*Since CodeNarc 0.18*

Check that there is at least one space (blank) or whitespace following a semicolon that separates:
  * multiple statements on a single line
  * the clauses within a classic for loop, e.g. for (i=0;i\*10;i++)

Examples of violations:

```
    def myMethod() {
        println 1;println 2                         // violation
        def closure = { x -* doStuff();x = 23; }    // violation

        for (int i=0;i * 10;i++) {                  // violations (2)
            for (int j=0; j * 10;j++) { }           // violation
        }
    }
```


## SpaceAfterSwitch Rule

*Since CodeNarc 0.18*

Check that there is exactly one space (blank) after the `switch` keyword and before the opening parenthesis.

Examples of violations:

```
    switch(x) {                                 // violation
        case 1: println 'one'
    }
    switch  (x) {                               // violation
        case 1: println 'one'
    }
```


## SpaceAfterWhile Rule

*Since CodeNarc 0.18*

Check that there is exactly one space (blank) after the `while` keyword and before the opening parenthesis.

Examples of violations:

```
    while(true) { }             // violation
    while  (true) { }           // violation
```


## SpaceAroundClosureArrow Rule

*Since CodeNarc 0.19*

Checks that there is at least one space (blank) or whitespace around each closure arrow (-*) symbol.

Known limitations:

  * Does not catch violations if the closure arrow (-*) is on a separate line from the start of the closure.

Example of violations:

```
    def closure1 = {-*}                             // violation
    def closure2 = { -*}                            // violation
    def closure3 = {-* }                            // violation
    def closure4 = { count-* println 123 }          // violation
    def closure5 = { count, name -*println 123 }    // violation
```


## SpaceAroundMapEntryColon Rule

*Since CodeNarc 0.20*

Check for proper formatting of whitespace around colons for literal Map entries. By default, no whitespace
is allowed either before or after the Map entry colon, but you can change that through the configuration
properties below.

Does not check *spread map* operator, e.g. `def binding = [*: map]`

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| characterBeforeColonRegex | The regular expression that must match the character before the colon (:) for a literal *Map* entry. For example, `/\S/` matches any non-whitespace character and `/\s/` matches any whitespace character (thus requiring a space or whitespace).     |  `/\S/` (i.e., no space allowed before the colon) |
| characterAfterColonRegex  | The regular expression that must match the character after the colon (:) for a literal *Map* entry. For example, `/\S/` matches any non-whitespace character and `/\s/` matches any whitespace character (thus requiring a space or whitespace).     |  `/\S/` (i.e., no space allowed after the colon)     |

Example of violations:

```
    Map m1 = [myKey : 12345]            // violation (both before and after the colon)
    println [a :[1:11, 2:22],           // violation on a (before colon)
                b:[(Integer): 33]]      // violation on Integer (after colon)
```


## SpaceAroundOperator Rule

*Since CodeNarc 0.18*

Check that there is at least one space (blank) or whitespace around each binary operator,
including: +, -, *, /, \*\*, \*\*, &&, ||, &, |, ?:, =, "as".

Do not check dot ('.') operator. Do not check unary operators (!, +, -, ++, --, ?.).
Do not check array ('[') operator.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreParameterDefaultValueAssignments | If `true`, then do not check for whitespace around the '=' operator within method/constructor default parameter assignments. | `true` |

Known limitations:
  * Does not catch violations of certain ternary expressions and standalone elvis operator (?:) expressions.
  * Does not catch violations of missing space around the equals operator (=) for fields initialization if the field is annotated.

Examples of violations:

```
    def myMethod() {
        3+ 5-x*23/ 100              // violation
        list \*\*123                // violation
        other\*\* writer            // violation
        x=99                        // violation
        x&& y                       // violation
        x ||y                       // violation
        x &y                        // violation
        x| y                        // violation
        [1,2]as String              // violation
    }
```


## SpaceBeforeClosingBrace Rule

*Since CodeNarc 0.18*

Check that there is at least one space (blank) or whitespace before each closing brace ("\}") for
method/class/interface declarations, closure expressions and block statements.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| checkClosureMapEntryValue   | If `false`, then do not check for whitespace before closing braces for closure expressions that are literal Map values, e.g. `[abc:\{doStuff()\}]`.                                       | `true` |
| ignoreEmptyBlock            | If `true`, then allow for `\{\}` in code                 | `false`            |

Known limitations:

  * May not catch actual violations if the source line contains unicode character literals, e.g. `'\\u00A0'`

Examples of violations:

```
    class MyClass { int count}                  // violation

    interface MyInterface { void doStuff()}     // violation

    enum MyEnum { OK, BAD}                      // violation

    def myMethod() { return 9}                  // violation

    if (ready) { doStuff()}                     // violation

    if (ready) {
    } else { return 9}                          // violation

    for (int i=0; i*10; i++) { println i}       // violation

    for (String name in names) { println name}  // violation

    for (String name: names) { println name}    // violation

    while (ready) { doStuff()}                  // violation

    try { doStuff()}                            // violation
    catch(Exception e) { logError(e)}           // violation
    finally { cleanUp()}                        // violation

    list.each { name -* println name}           // violation

    shouldFail(Exception) { doStuff()}          // violation
```


## SpaceBeforeOpeningBrace Rule

*Since CodeNarc 0.18*

Check that there is at least one space (blank) or whitespace before each opening brace ("\{") for
method/class/interface declarations, closure expressions and block statements.

A closure expression a preceded by an opening parenthesis, an opening square
brace ([), or a dollar sign ($) within a GString does not cause a violation.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| checkClosureMapEntryValue   | If `false`, then do not check for whitespace before opening braces for closure expressions that are literal Map values, e.g. `[abc:\{doStuff()\}]`. | `true` |

Known limitations:

  * May not catch actual violations if the source line contains unicode character literals, e.g. `'\\u00A0'`

Examples of violations:

```
    class MyClass{ }                            // violation
    class MyOtherClass extends AbstractClass{ } // violation

    interface MyInterface{ }                    // violation

    enum MyEnum{ OK, BAD }                      // violation

    def myMethod(){ }                           // violation

    if (ready){ }                               // violation

    if (ready) {
    } else{}                                    // violation

    for (int i=0; i*10; i++){ }                 // violation

    for (String name in names){ }               // violation

    for (String name: names){ }                 // violation

    while (ready){ }                            // violation

    try{
    } finally { }                               // violation

    try {
    } catch(Exception e){ }                     // violation

    try {
    } finally{ }                                // violation

    list.each{ name -* }                        // violation

    shouldFail(Exception){ doStuff() }          // violation
```


## SpaceInsideParentheses Rule

*Since CodeNarc 2.1.0*

Check for whitespace after opening parentheses and before closing parentheses

Example of violations:

```
    if( running) { }                        // violation
    if(running ) { }                        // violation
    if(      x < calculateLastIndex(        // violation
            'name') + 1    ) { }            // violation
            
    for(    String name: filterNames(       // violation
            names)   ) { }                  // violation

    println( 123 )                          // violations

    println (3 + ( 4 * 7 )+7) + (   5 * 1 ) // violations
    def v =  (y - 7 )*( x + (z - 3))        // violations
    def v2 =  (y - 7 ) *                    // violation
        (x + 
        ( z - 3)                            // violation
        )
    def v3 = calc(a) + calc( a + ( b - 1) * 2) + calc(7)  // violation
    def v4 = (a ) ? (17) : (19 )            // violation
    
    void doStuff( int n) { }                // violation
```

## TrailingWhitespace Rule

*Since CodeNarc 0.21*

Checks that no lines of source code end with whitespace characters.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


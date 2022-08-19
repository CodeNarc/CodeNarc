---
layout: default
title: CodeNarc - DRY Rules
---  

# DRY (Don't Repeat Yourself) Rules  ("*rulesets/dry.xml*")

These rules check for duplicate code, enforcing the DRY (Don't Repeat Yourself) principle.


## DuplicateListLiteral Rule

*Since CodeNarc 0.16*

This rule checks for duplicate *List* literals within the current class. This rule only checks for *List*s
where values are all constants or literals.

List literals within annotations are ignored.

Code containing duplicate *List* literals can usually be improved by declaring the *List* as a constant field.

By default, the rule does not analyze test files. This rule sets the default value of the
*doNotApplyToFilesMatching* property to ignore file names ending in 'Spec.groovy', 'Test.groovy', 'Tests.groovy'
or 'TestCase.groovy'.

Examples of violations:

```
      def var1 = [1, null, Boolean.FALSE, 'x', true]
      def var2 = [1, null, Boolean.FALSE, 'x', true]        // violation

      def var1 = [1, [3, 4]]
      def var2 = [1, [3,4]]     // violation

      def var1 = [123, [3, 4, [x:99], 5]]
      def var2 = [99, [3, 4, [x:99], 5]]        // violation [3, 4, [x:99], 5]
```

Examples of non-violations:

```
    def name
    def var1 = [name, 'b', 'c']
    def var2 = [name, 'b', 'c']   // not a violation; name is a variable

    def var1 = [1, 7+5]
    def var2 = [1, 7+5]      // not a violation; contains a non-constant/literal expression
```


### Notes

  * This rule does not search across several files at once, only in the current file, and only
    within the current class.

  * You can suppress the error by annotating a class or method with the `@SuppressWarnings('DuplicateListLiteral')`
    annotation.


## DuplicateMapLiteral Rule


*Since CodeNarc 0.16*

This rule checks for duplicate *Map* literals within the current class. This rule only checks for *Map*s
where the keys and values are all constants or literals.

Code containing duplicate *Map* literals can usually be improved by declaring the *Map* as a constant field.

By default, the rule does not analyze test files. This rule sets the default value of the
*doNotApplyToFilesMatching* property to ignore file names ending in 'Spec.groovy', 'Test.groovy', 'Tests.groovy'
or 'TestCase.groovy'.

Examples of violations:

```
      def var1 = [a:1, b:null, c:Boolean.FALSE, d:'x', e:true]
      def var2 = [a:1, b:null, c:Boolean.FALSE, d:'x', e:true]      // violation

      def var1 = [a:1, b:[x:3,y:4]]
      def var2 = [a:1, b:[x:3,y:4]]     // violation

      def var1 = [a:1, b:[3,4]]
      def var2 = [a:1, b:[3,4]]     // violation

      def var1 = [null:1, 'b':2, (Boolean.FALSE):3, (4):4, (true):5]
      def var2 = [null:1, 'b':2, (Boolean.FALSE):3, (4):4, (true):5]    // violation
```

Examples of non-violations:

```
    def name
    def var1 = [(name):1, b:1, c:1]
    def var2 = [(name):1, b:1, c:1]   // not a violation; name is a variable

    def var1 = [a:1, b:['x', name]]
    def var2 = [a:1, b:['x', name]]   // not a violation; name is a variable

    def var1 = [a:7+5]
    def var2 = [a:7+5]      // not a violation; contains a non-constant/literal expression
```


### Notes

  * This rule does not search across several files at once, only in the current file, and only
    within the current class.

  * You can suppress the error by annotating a class or method with the `@SuppressWarnings('DuplicateMapLiteral')`
    annotation.


## DuplicateNumberLiteral Rule


*Since CodeNarc 0.11*

This rule checks for duplicate number literals within the current class.

Code containing duplicate *Number* literals can usually be improved by declaring the *Number* as a constant field.

By default, the rule does not analyze test files. This rule sets the default value of the
*doNotApplyToFilesMatching* property to ignore file names ending in 'Spec.groovy', 'Test.groovy', 'Tests.groovy'
or 'TestCase.groovy'.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreNumbers               | The optional comma-separated list of numbers that should be ignored (i.e., not cause a violation). | `0,1` |
| duplicateNumberMinimumValue |  Ignore duplicate numbers less than this value | `null` |

### Notes

  * This rule ignores Long/long values within enums, because the generated code may include generated long id values
    and produce false positive rule violations.

  * This rule does not search across several files at once, only in the current file, and only
    within the current class.

  * You can suppress the error by annotating a class or method with the `@SuppressWarnings('DuplicateNumberLiteral')`
    annotation.


## DuplicateStringLiteral Rule

*Since CodeNarc 0.11*

This rule checks for duplicate String literals within the current class.

Code containing duplicate *String* literals can usually be improved by declaring the *String* as a constant field.

This rule ignores (zero-length) empty strings.

By default, the rule does not analyze test files. This rule sets the default value of the
*doNotApplyToFilesMatching* property to ignore file names ending in 'Spec.groovy', 'Test.groovy', 'Tests.groovy'
or 'TestCase.groovy'.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| ignoreStrings               | The optional comma-separated list of Strings that should be ignored (i.e., not cause a violation). | `''` (empty string) |
| ignoreStringsDelimiter      | The delimiter char for `ignoreStrings`. | `,` (comma) |
| duplicateStringMinimumLength| Ignore duplicate strings whose length is less than this value | `null` |

### Notes

  * This rule does not search across several files at once, only in the current file, and only
    within the current class.

  * You can suppress the error by annotating a class or method with the `@SuppressWarnings('DuplicateStringLiteral')`
    annotation.


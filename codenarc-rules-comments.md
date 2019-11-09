---
layout: default
title: CodeNarc - Comments Rules
---  

# Comments Rules  ("*rulesets/comments.xml*")


These rules are related to comments.


## ClassJavadoc Rule

*Since CodeNarc 0.15*

Makes sure each class and interface definition is preceded by javadoc. Enum definitions are not checked, due to strange
behavior in the Groovy AST. By default, only the main class in a file is checked for Javadoc. The main class is defined as
the class that has the same name as the source file, for instance MyClass is the main class in MyClass.groovy but the class
MyOtherClass defined in the same source file is not the main class. To check all the classes in the file set the rule
property `applyToNonMainClasses` to true.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).


## JavadocConsecutiveEmptyLines Rule

*Since CodeNarc 1.3*

Checks for javadoc comments with more than one consecutive empty line.

Known limitation: Only the first occurrence of consecutive empty lines within a javadoc comment is found.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

Example of violations:

```
    /**
     * Description
     *
     *                                                                          // violation
     * @param startIndex - the starting index
     * @return the full count
     * @throws RuntimeException if you are not pure of spirit
     *
     * NOTE: Only the first occurrence of consecutive empty lines
     *       within a javadoc comment is found, so the following
     *       lines are not flagged as violations!!!
     *
     *
     */
    int countThings(int startIndex) { }
```


## JavadocEmptyAuthorTag Rule

*Since CodeNarc 1.3*

Checks for empty @author tags within javadoc.

Known limitation: Only the first occurrence of an empty @author within a javadoc comment is found.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Return the calculated count of some stuff.
     *
     * @param startIndex - the starting index
     * @return the count
     * @author                                             // violation
     */
    int countThings(int startIndex) { }
```

## JavadocEmptyExceptionTag Rule

*Since CodeNarc 1.3*

Checks for empty @exception tag within javadoc.

Known limitation: Only the first occurrence of an empty @exception within a javadoc comment is found.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Return the calculated count of some stuff.
     *
     * @param startIndex - the starting index
     * @return the count
     * @exception                                           // violation
     */
    int countThings(int startIndex) { }
```

## JavadocEmptyFirstLine Rule

*Since CodeNarc 1.3*

Check for javadoc comments with an empty top line.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

  Example of violations:

```
    /**
     *                                                      // violation
     * Sample class
     *
     * @author Some Developer
     */
    class MyClass {

        /**
         *                                                  // violation
         * Return the calculated count of some stuff,
         * starting with the specified startIndex.
         *
         * @param startIndex - the starting index
         * @return the full count
         * @throws RuntimeException when the Singularity occurs
         */
        int countThings(int startIndex) {
        }
    }
```

## JavadocEmptyLastLine Rule

*Since CodeNarc 1.3*

Check for javadoc comments with an empty line at the bottom.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

Example of violations:

```
    /**
     * Sample class
     *
     * @author Some Developer
     *                                                      // violation
     */
    class MyClass {

        /**
         * Return the calculated count of some stuff,
         * starting with the specified startIndex.
         *
         * @param startIndex - the starting index
         * @return the full count
         * @throws RuntimeException when life finds a way
         *                                                  // violation
         */
        int countThings(int startIndex) {
        }
    }
```

## JavadocEmptyParamTag Rule

*Since CodeNarc 1.3*

Checks for empty *@param* tags within javadoc

Known limitation: Only the first occurrence of an empty @param within a javadoc comment is found.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Return the calculated count of some stuff.
     *
     * @param                                               // violation
     * @return the full count
     * @throws RuntimeException upon self-reflection
     */
    int countThings(int startIndex) { }
```


## JavadocEmptyReturnTag Rule

*Since CodeNarc 1.3*

Checks for empty @return tags within javadoc.

Known limitation: Only the first occurrence of an empty @return within a javadoc comment is found.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Return the calculated count of some stuff.
     *
     * @param startIndex - the starting index
     * @return                                  // violation
     * @throws RuntimeException if you don't say "please"
     */
    int countThings(int startIndex) { }
```

## JavadocEmptySeeTag Rule

*Since CodeNarc 1.3*

Checks for empty *@see* tags within javadoc.

Known limitation: Only the first occurrence of an empty @see within a javadoc comment is found.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Sample class
     *
     * @see                                                         // violation
     */
    class MyClass {

        /**
         * Return the calculated count of some stuff,
         * starting with the specified startIndex.
         *
         * @param startIndex - the starting index
         * @return the full count
         * @throws RuntimeException when you least expect it
         *     @see                                                 // violation
         *
         * NOTE: Only the first occurrence of an empty @see tag
         *       within a javadoc comment is found, so the
         *       following line is not flagged as a violation!!!
         * @see
         */
        int countThings(int startIndex) { }

        /**
         *@see                                                      // violation
         */
        String name = 'joe'
    }
```

## JavadocEmptySinceTag Rule

*Since CodeNarc 1.3*

Checks for empty @since tags within javadoc.

Known limitation: Only the first occurrence of an empty @since within a javadoc comment is found.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Return the calculated count of some stuff.
     *
     * @param startIndex - the starting index
     * @return the count
     * @since                                          // violation
     */
    int countThings(int startIndex) { }
```


## JavadocEmptyThrowsTag Rule

*Since CodeNarc 1.3*

Checks for empty @throws tag within javadoc.

Known limitation: Only the first occurrence of an empty @throws within a javadoc comment is found.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Return the calculated count of some stuff.
     *
     * @param startIndex - the starting index
     * @return the count
     * @throws                                          // violation
     */
    int countThings(int startIndex) { }
```

## JavadocEmptyVersionTag Rule

*Since CodeNarc 1.3*

Checks for empty @version tags within javadoc.

Known limitation: Only the first occurrence of an empty @version within a javadoc comment is found.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Return the calculated count of some stuff.
     *
     * @param startIndex - the starting index
     * @return the count
     * @version                                          // violation
     */
```


## JavadocMissingExceptionDescription Rule

*Since CodeNarc 1.4*

Checks for missing description within @exception javadoc tags.

Known limitation: Only the first occurrence of a missing description for an @exception javadoc comment is found.

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Return the calculated count of some stuff.
     *
     * @param startIndex the starting index; must be *= 0
     * @return the full count
     * @exception RuntimeException                   // violation
     */
    int countThings(int startIndex) { }
```

## JavadocMissingParamDescription Rule

*Since CodeNarc 1.4*

Checks for missing description within Javadoc @param tags.

Known limitation: Only the first occurrence of a missing description for a @param javadoc comment is found

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Return the calculated count of some stuff.
     *
     * @param startIndex                           // violation
     * @return the full count
     * @throws RuntimeException if it senses fear
     */
    int countThings(int startIndex) { }
```

## JavadocMissingThrowsDescription Rule

*Since CodeNarc 1.4*

Checks for missing description within Javadoc @throws tags.

Known limitation: Only the first occurrence of a missing description for a @throws javadoc comment is found

NOTE: This is a file-based rule, rather than an AST-based rule, so the *applyToClassNames* and
*doNotApplyToClassNames* rule configuration properties are not available. See
[Standard Properties for Configuring Rules](./codenarc-configuring-rules.html#standard-properties-for-configuring-rules).

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| allowMultiline              | Set to `true` to allow the tag content (name, description, etc.) to start on the following line. If *false*, that content must start on the same line as the tag.  | `false` |

Example of violations:

```
    /**
     * Return the calculated count of some stuff.
     *
     * @param startIndex the starting index; must be *= 0
     * @return the full count
     * @throws RuntimeException                   // violation
     */
    int countThings(int startIndex) { }
```



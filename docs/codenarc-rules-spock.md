---
layout: default
title: CodeNarc - Spock Rules
---

# Spock Rules  ("*rulesets/spock.xml*")

*This rule set was introduced in CodeNarc 4.1.0.* The **SpockIgnoreRestUsed**, **SpockMissingAssert** and
**SpockUseVerifyEach** rules moved here from the [JUnit](./codenarc-rules-junit.html) rule set; their entries in
*rulesets/junit.xml* are disabled by default and will be removed in a future release.


## SpockIgnoreRestUsed Rule

*Since CodeNarc 0.14*

If Spock's `@IgnoreRest` annotation appears on any method, all non-annotated test methods are not executed.
This behaviour is almost always unintended. It's fine to use @IgnoreRest locally during development, but when
committing code, it should be removed.

The *specificationClassNames* and *specificationSuperclassNames* properties determine which classes are considered
Spock *Specification* classes.

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| specificationClassNames     | Specifies one or more (comma-separated) class names that should be treated as Spock Specification classes. The class names may optionally contain wildcards (*,?), e.g. "*Spec". | `null` |
| specificationSuperclassNames| Specifies one or more (comma-separated) class names that should be treated as Spock Specification superclasses. In other words, a class that extends a matching class name is considered a Spock Specification . The class names may optionally contain wildcards (*,?), e.g. "*Spec". | "*Specification" |

Example of violations:

```
    public class MySpec extends spock.lang.Specification {
        @spock.lang.IgnoreRest
        def "my first feature"() {
            expect: false
        }

        def "my second feature"() {
            given: def a = 2

            when: a *= 2

            then: a == 4
        }
    }
```


## SpockMissingAssert Rule

*Since CodeNarc 3.3.0*

Spock treats all expressions on the first level of a then or expect block as an implicit assertion.
However, everything inside if/for/switch/... blocks is not an implicit assert, just a useless comparison - unless it is
inside a `with`, `verifyAll` or `verifyEach` closure, which turns *every* expression of its closure into a condition,
nested ones included.

This rule finds such expressions, where an explicit call to `assert` would be required. Please note that the rule might
produce false positives, as it relies on method names to determine whether an expression has a boolean type or not.

Example of violations:

```
    public class MySpec extends spock.lang.Specification {
        def "test passes - does not behave as expected"() {
            expect:
            if (true) {
                true == false // violation - is inside an if block, and therefore not treated as an implicit assertion by spock
            }
        }

        def "test fails - behaves as expected"() {
            expect:
            if (true) {
                with(new Object()) {
                    true == false // no violation - expressions in with are treated as implicit assertions by spock
                }
            }
        }
    }
```

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| specificationClassNames     | Specifies one or more (comma-separated) class names that should be treated as Spock Specification classes. The class names may optionally contain wildcards (*,?), e.g. "*Spec". | `null` |
| specificationSuperclassNames| Specifies one or more (comma-separated) class names that should be treated as Spock Specification superclasses. In other words, a class that extends a matching class name is considered a Spock Specification . The class names may optionally contain wildcards (*,?), e.g. "*Spec". | "*Specification" |


## SpockMissingReason Rule

*Since CodeNarc 4.1.0*

Always document why a test is disabled. Without a clear reason, developers will leave an `@Ignore` in place rather than risk breaking something, permanently losing that test coverage.
Spock makes this easy by accepting a reason string on every skip annotation.

The rule reports the following annotations - on a feature method or on the specification class - when they state no
reason:

| Annotation          | Reason member |
|---------------------|---------------|
| `@Ignore`           | `value`       |
| `@PendingFeature`   | `reason`      |
| `@Isolated`         | `value`       |

A reason that is not a `String` literal - a GString, a constant reference, any other expression - always counts as
present; only a missing or blank literal is reported.
`@Retry` is not handled here, as it has no reason member.

`@Requires`, `@IgnoreIf` and `@PendingFeatureIf` accept a `reason` too, but a self-explanatory condition such as
`@IgnoreIf({ os.windows })` does not need one, and that is not a judgement CodeNarc can make.
These three are therefore only
checked when the *checkConditionalAnnotations* property is enabled, and listing them in *annotationNames* has no effect.

The advice in the message follows what the annotation actually means: `@Isolated` asks why the specification
must run in isolation rather than why it is disabled, and `@PendingFeature` asks why the feature is expected
to fail rather than treating it as skipped.

The *reasonRegex* property additionally requires the reason to match a pattern - set it to `SPOCK-\d+` to
require an issue id, or to `https?://` to require a link.
The reason has to *contain* a match, so `@Ignore("flaky on Windows, see SPOCK-1234")` satisfies `SPOCK-\d+`.
It applies only to the annotations named in *reasonRegexAnnotationNames*, and only to a reason that is a `String`
literal - the content of a GString or a constant reference cannot be inspected, so it always passes.
An annotation with no reason at all is reported for the missing reason alone, never twice.

Example of violations:

```
    class MySpec extends spock.lang.Specification {
        @Ignore                                             // violation - no reason given
        def "first feature"() {
            expect: false
        }

        @PendingFeature                                     // violation - no reason given
        def "second feature"() {
            expect: false
        }

        @Ignore("")                                         // violation - a blank reason is not a reason
        def "third feature"() {
            expect: false
        }

        @Ignore("flaky on Windows, see #1790")              // no violation
        def "fourth feature"() {
            expect: false
        }

        @PendingFeature(reason = "coalescing not implemented yet")   // no violation
        def "fifth feature"() {
            expect: false
        }

        @IgnoreIf({ os.windows })                           // no violation - unless checkConditionalAnnotations is enabled
        def "sixth feature"() {
            expect: false
        }
    }
```

Example of violations with *reasonRegex* set to `SPOCK-\d+`:

```
    @Isolated("needs the shared port")                      // no violation - not in reasonRegexAnnotationNames
    class MySpec extends spock.lang.Specification {
        @Ignore("flaky on Windows")                         // violation - the reason states no issue id
        def "first feature"() {
            expect: false
        }

        @Ignore("flaky on Windows, see SPOCK-1234")         // no violation
        def "second feature"() {
            expect: false
        }
    }
```

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| annotationNames             | Specifies one or more (comma-separated) simple annotation names that must state a reason. Teams can add their own project-specific skip annotations here. For an annotation that is not one of the built-in Spock ones, a value in either the `value` or the `reason` member counts as a reason. Has no effect on the conditional annotations, which *checkConditionalAnnotations* governs on its own. Setting this property replaces the default list, so a team adding its own names must specify the full list, including `Ignore, PendingFeature, Isolated`, if those should still be checked. | "Ignore, PendingFeature, Isolated" |
| checkConditionalAnnotations | If `true`, the conditional annotations - `@Requires`, `@IgnoreIf` and `@PendingFeatureIf` - must state a `reason` as well. | `false` |
| reasonRegex                 | If set, a stated reason must contain a match for this regular expression - use it to require an issue id (`SPOCK-\d+`) or a link (`https?://`). Unset by default, which leaves the content of a reason unchecked. Only a reason that is a `String` literal is matched. | `null` |
| reasonRegexAnnotationNames  | Specifies one or more (comma-separated) simple annotation names that *reasonRegex* applies to; only relevant when that property is set. The conditional annotations and `@Isolated` are excluded by default: their reason explains a standing condition (`@IgnoreIf(value = { os.windows }, reason = "no native lib on Windows")`) or an execution constraint (`@Isolated("needs the shared port")`), which is not work to be tracked. Setting this property replaces the default list, so a team adding its own names must specify the full list, including `Ignore, PendingFeature`, if those should still be checked. | "Ignore, PendingFeature" |
| specificationClassNames     | Specifies one or more (comma-separated) class names that should be treated as Spock Specification classes. The class names may optionally contain wildcards (*,?), e.g. "*Spec". | `null` |
| specificationSuperclassNames| Specifies one or more (comma-separated) class names that should be treated as Spock Specification superclasses. In other words, a class that extends a matching class name is considered a Spock Specification . The class names may optionally contain wildcards (*,?), e.g. "*Spec". | "*Specification" |


## SpockUnnecessaryAssert Rule

*Since CodeNarc 4.1.0*

Spock treats every top-level expression of a `then:`, `expect:` or `filter:` block - and *every* expression of a
`with`, `verifyAll` or `verifyEach` closure, including the ones nested inside `if`/`for`/`while`/`switch`/`try` - as an
implicit condition. An explicit `assert` there is redundant: it compiles to the same condition and it obscures the fact
that the surrounding block is an assertion block.

This rule is the exact inverse of [SpockMissingAssert](#spockmissingassert-rule): it reports only where that rule stays
silent, and it never reports where that rule demands an explicit `assert`. In particular, an `assert` is *not* reported
when it

* carries a message (`assert result.valid : "after $stimulus"`, or Groovy's comma form
  `assert result.valid, "after $stimulus"`) - an implicit condition cannot carry one, so this is the documented way to
  add context,
* is in a block without implicit conditions (`given:`, `when:`, `cleanup:`, `where:`),
* is nested inside an `if`/`for`/`while`/`switch`/`try` statement of a `then:`/`expect:`/`filter:` block, where Spock
  does *not* add implicit conditions. Note that inside a `with`/`verifyAll`/`verifyEach` closure the same nesting *is*
  reported, because there Spock asserts every expression, not just the top-level ones,
* is inside a plain helper method,
* or is inside a closure that is not a `with`/`verifyAll`/`verifyEach` body - such as a stub or callback closure
  (`>> { assert ... }`) or an `each { assert ... }` closure, where the `assert` is what makes the check run at all.
  Only the unqualified Spock methods count here; `obj.with { }` is Groovy's `Object.with`, which does not add
  implicit conditions.

Example of violations:

```
    class MySpec extends spock.lang.Specification {
        def "test"() {
            expect:
            assert result == 42                       // violation - 'expect:' conditions are implicit

            when:
            def ship = launch()

            then:
            assert ship.launched                      // violation - 'then:' conditions are implicit
            with(ship) {
                assert registry == 'NCC 1701'         // violation - 'with' conditions are implicit
                if (warpCapable) {
                    assert warpFactor > 1             // violation - 'with' asserts nested expressions as well
                }
            }
            assert ship.crew : "after $stimulus"      // no violation - an implicit condition cannot carry a message
            if (ship.warpCapable) {
                assert ship.warpFactor > 1            // no violation - nested conditions of 'then:' are not implicit
            }
            ship.crew.each {
                assert it.certified                   // no violation - closure is not a 'with'/'verifyAll'/'verifyEach' body
            }
        }
    }
```

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| specificationClassNames     | Specifies one or more (comma-separated) class names that should be treated as Spock Specification classes. The class names may optionally contain wildcards (*,?), e.g. "*Spec". | `null` |
| specificationSuperclassNames| Specifies one or more (comma-separated) class names that should be treated as Spock Specification superclasses. In other words, a class that extends a matching class name is considered a Spock Specification . The class names may optionally contain wildcards (*,?), e.g. "*Spec". | "*Specification" |


## SpockUnnecessaryUnroll Rule

*Since CodeNarc 4.1.0*

Spock 2 unrolls data-driven features by default, so an `@Unroll` without a value is a no-op left over from
Spock 1.x. It is common in migrated code bases, where it is both noise and misleading to the next reader.

This rule reports an `@Unroll` with no member value, on a feature method or on a *Specification* class. An
`@Unroll("...")` or `@Unroll(value = "...")` carries the iteration-name template and is never reported.
`@Unroll("")` is *not* such a template: `Unroll.value()` already defaults to the empty String and Spock only uses a
pattern when it is non-empty, so that form is reported like the bare one. A member value that cannot be evaluated
from the source, such as a reference to a constant, is not reported.

The rule stays silent wherever a bare `@Unroll` still has an effect, that is when the *Specification* class that
declares the feature carries `@Rollup` - the bare `@Unroll` then re-enables unrolling for that one feature.

A `@Rollup` on a *superclass* does not count. Spock applies `@Unroll` and `@Rollup` per declaring class - neither is
inheritable - so a base specification's `@Rollup` covers only the features that base specification declares itself.

A `@Rollup` next to the `@Unroll` it would cancel out does not count either. Spock rejects that combination with an
`InvalidSpecException`, on a feature method as well as on a class, so such a specification cannot run in the first
place.

`@Rollup` on its own, and an `@Unroll` on anything that is not a feature method (a helper method, for example),
are never reported either.

Example of violations:

```
    @Unroll                                                      // violation - redundant on the class as well
    class MySpec extends spock.lang.Specification {
        @Unroll                                                  // violation - Spock 2 unrolls by default
        def "node version #version parses"() {
            expect: parse(version)
            where: version << ['1.0', '2.0']
        }

        @Unroll()                                                // violation - an empty member list is still bare
        def "another #version parses"() {
            expect: parse(version)
            where: version << ['1.0', '2.0']
        }

        @Unroll("")                                              // violation - the empty String is not a template
        def "yet another #version parses"() {
            expect: parse(version)
            where: version << ['1.0', '2.0']
        }

        @Unroll("#operator on #a and #b yields #expected")       // no violation - carries the iteration name
        def "arithmetic operators evaluate their operands"() {
            expect: evaluate(operator, a, b) == expected
            where: operator | a | b | expected
                   '+'      | 1 | 2 | 3
        }
    }

    @Rollup
    class RolledUpSpec extends spock.lang.Specification {
        @Unroll                                                  // no violation - re-enables unrolling for this feature
        def "node version #version parses"() {
            expect: parse(version)
            where: version << ['1.0', '2.0']
        }
    }
```

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| specificationClassNames     | Specifies one or more (comma-separated) class names that should be treated as Spock Specification classes. The class names may optionally contain wildcards (*,?), e.g. "*Spec". | `null` |
| specificationSuperclassNames| Specifies one or more (comma-separated) class names that should be treated as Spock Specification superclasses. In other words, a class that extends a matching class name is considered a Spock Specification . The class names may optionally contain wildcards (*,?), e.g. "*Spec". | "*Specification" |

**NOTE:** This rule assumes Spock 2 or later, where data-driven features are unrolled by default. On Spock 1.x every
bare `@Unroll` has an effect, so disable this rule there. A project can also flip the default the other way round,
with `unroll { ... }` / `runner { rollup ... }` in *SpockConfig.groovy*, which CodeNarc does not see; disable this
rule there too.


## SpockUseTempDir Rule

*Since CodeNarc 4.1.0*

Spock's own `@TempDir` is the lifecycle-correct temporary directory: one per iteration by default, one per
specification when combined with `@Shared`, and always deleted afterwards. The alternatives leak.
`@Rule TemporaryFolder` is a JUnit 4 carry-over that only works through Spock's JUnit rule interop, and
`File.createTempDir()` / `Files.createTempDirectory(..)` are never cleaned up at all unless somebody
hand-writes a `cleanup:` block for them.

The rule reports two distinct shapes, with distinct messages, because the fixes differ in effort:

1. a `@Rule` or `@ClassRule` field whose type - or whose initializer - is `TemporaryFolder`, and
2. a call that creates a file or directory in the JVM-wide default temporary directory:
   `File.createTempDir(..)`, `File.createTempFile(prefix, suffix)`,
   `Files.createTempDirectory(prefix)`, `Files.createTempFile(prefix, suffix)`, and
   `new File(System.getProperty('java.io.tmpdir'), ..)`.

The second shape can be switched off with *checkAdHocCreation*, which leaves only the `TemporaryFolder`
fields reported.

Because CodeNarc runs without a classpath, the receiver of an ad-hoc creation call is matched textually
against `File` and `Files`, and only calls that positively select the default temporary directory are
reported. A call that is handed an explicit target directory - most importantly a `@TempDir` field or
parameter, such as `Files.createTempFile(tempDir, 'a', 'b')` - is left alone. The overloads are told
apart by their argument count, except where `Files` takes trailing `FileAttribute` varargs: there a call
with extra arguments is only reported when its first argument is a String literal, as in
`Files.createTempDirectory('x', attribute)`. This is a heuristic rule: it cannot tell whether a variable
in that first position holds a prefix or a directory, which is why it stays silent whenever the evidence
is not positively there.

Example of violations:

```
    class MySpec extends spock.lang.Specification {
        @Rule
        TemporaryFolder tempFolder = new TemporaryFolder()      // violation - use a '@TempDir' field

        @TempDir
        File tempDir                                            // no violation - this is the fix

        def "test"() {
            given:
            def a = File.createTempDir()                        // violation - never cleaned up
            def b = Files.createTempDirectory('x')              // violation - never cleaned up
            def c = new File(System.getProperty('java.io.tmpdir'), 'x')  // violation - never cleaned up
            def d = new File(tempDir, 'sub')                    // no violation - inside the '@TempDir'
            def e = Files.createTempFile(tempDir.toPath(), 'a', 'b')     // no violation - inside the '@TempDir'

            expect:
            a != null
        }
    }
```

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| checkAdHocCreation          | If `true`, also report calls that create a file or directory in the JVM-wide default temporary directory, such as `File.createTempDir()` or `Files.createTempDirectory('x')`. If `false`, only `@Rule`/`@ClassRule` `TemporaryFolder` fields are reported. | `true` |
| specificationClassNames     | Specifies one or more (comma-separated) class names that should be treated as Spock Specification classes. The class names may optionally contain wildcards (*,?), e.g. "*Spec". | `null` |
| specificationSuperclassNames| Specifies one or more (comma-separated) class names that should be treated as Spock Specification superclasses. In other words, a class that extends a matching class name is considered a Spock Specification . The class names may optionally contain wildcards (*,?), e.g. "*Spec". | "*Specification" |


## SpockUseVerifyEach Rule

*Since CodeNarc 3.7.0*

Checks for `.every`, `.each`, `.eachWithIndex`, or `.forEach` calls containing assertions in Spock specifications
that should use Spock 2.4's `verifyEach` instead. Using `verifyEach` provides better per-item failure diagnostics
by collecting all failures individually rather than failing fast on the first item.

In `then:` and `expect:` blocks, the rule detects:
- `.every { booleanExpr }` — implicit boolean assertion
- `.each`/`.eachWithIndex`/`.forEach` with closures containing `assert` statements or boolean expressions

When `checkAllBlocks` is enabled (the default), the rule also detects `.each`/`.eachWithIndex`/`.forEach` with
explicit `assert` statements in other blocks (e.g., `given:`, `when:`) and in helper methods.

Example of violations:

```
    class MySpec extends spock.lang.Specification {
        def "test"() {
            given:
            def list = [1, 2, 3]

            expect:
            list.every { it > 0 }                   // violation - use verifyEach(list) { it > 0 }
            list.each { assert it > 0 }              // violation - use verifyEach(list) { it > 0 }
            list.eachWithIndex { item, i ->
                assert item > 0                      // violation - use verifyEach(list) { item, i -> item > 0 }
            }
            list.forEach { assert it > 0 }           // violation - use verifyEach(list) { it > 0 }
        }
    }
```

| Property                    | Description            | Default Value    |
|-----------------------------|------------------------|------------------|
| checkAllBlocks              | If `true`, also checks blocks other than `then:` and `expect:` (and helper methods) for `.each`/`.eachWithIndex`/`.forEach` with explicit `assert` statements. | `true` |
| specificationClassNames     | Specifies one or more (comma-separated) class names that should be treated as Spock Specification classes. The class names may optionally contain wildcards (*,?), e.g. "*Spec". | `null` |
| specificationSuperclassNames| Specifies one or more (comma-separated) class names that should be treated as Spock Specification superclasses. In other words, a class that extends a matching class name is considered a Spock Specification . The class names may optionally contain wildcards (*,?), e.g. "*Spec". | "*Specification" |

**NOTE:** This rule requires Spock 2.4+ which introduces the `verifyEach` method. If you are using an older version of Spock, disable this rule.


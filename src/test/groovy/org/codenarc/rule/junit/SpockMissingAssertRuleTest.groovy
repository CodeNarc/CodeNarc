/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.junit

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Violation
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import java.util.stream.Stream

/**
 * Tests for SpockMissingAssertRuleTest
 *
 * @author Jean André Gauthier
 * @author Daniel Clausen
  */
class SpockMissingAssertRuleTest extends AbstractRuleTestCase<SpockMissingAssertRule> {

    @Test
    void ruleProperties_AreValid() {
        assert rule.priority == 2
        assert rule.name == 'SpockMissingAssert'
    }

    @Test
    void topLevelBoolean_NoViolations() {
        final SOURCES = labelsToTest.collect { label ->
            """
            public class MySpec extends spock.lang.Specification {
                def "topLevelBoolean_NoViolations"() {
                    ${label}:
                    "123"
                    123
                    false
                    assert false
                }
            }
            """.stripIndent()
        }
        assertNoViolations(SOURCES)
    }

    @ParameterizedTest
    @MethodSource('statementsToTest')
    void statement_NoViolations(Closure<GString> statement) {
        final SOURCES = labelsToTest.collect { label ->
            """
            public class MySpec extends spock.lang.Specification {
                def "statement_NoViolations"() {
                    ${label}:
                    ${statement("""
                        "123"
                        123
                        assert false
                    """)}
                }
            }
            """.stripIndent()
        }
        assertNoViolations(SOURCES)
    }

    @ParameterizedTest
    @MethodSource('statementsToTest')
    void statement_SingleViolation(Closure<GString> statement) {
        final SOURCES = labelsToTest.collect { label ->
            new Tuple2<>(
                label,
                """
                public class MySpec extends spock.lang.Specification {
                    def "statement_SingleViolation"() {
                        ${label}:
                        ${statement("""
                            "123"
                            123
                            false
                            def foo = {
                                [1,2,3].find {
                                    it == 1
                                }
                            }
                        """)}
                    }
                }
                """.stripIndent())
        }
        SOURCES.forEach { lblSrc ->
            assertSingleViolation(lblSrc.v2) { Violation violation ->
                violation.sourceLine == 'false' &&
                    violation.message == violationMessage(lblSrc.v1)
            }
        }
    }

    @ParameterizedTest
    @MethodSource('statementsToTest')
    void statementWithDef_NoViolation(Closure<GString> statement) {
        final SOURCES = labelsToTest.collect { label ->
                """
                public class MySpec extends spock.lang.Specification {
                    def "statementWithDef_NoViolation"() {
                        ${label}:
                        ${statement("""
                            def foo = {
                                [1,2,3].find {
                                    it == 1
                                }
                            }
                        """)}
                    }
                }
                """.stripIndent()
        }
        assertNoViolations(SOURCES)
    }

    @ParameterizedTest
    @MethodSource('statementsToTest')
    void statementInWith_NoViolation(Closure<GString> statement) {
        final SOURCES = labelsToTest.collect { label ->
            """
            public class MySpec extends spock.lang.Specification {
                def "statementInWith_NoViolation"() {
                    ${label}:
                    with(new Object()) {
                        ${statement("""
                            "123"
                            123
                            false
                        """)}
                    }
                }
            }
            """.stripIndent()
        }
        assertNoViolations(SOURCES)
    }

    @ParameterizedTest
    @MethodSource('statementsToTest')
    void withInStatement_NoViolation(Closure<GString> statement) {
        final SOURCES = labelsToTest.collect { label ->
            """
            public class MySpec extends spock.lang.Specification {
                def "withInStatement_NoViolation"() {
                    ${label}:
                    ${statement("""
                        with(new Object()) {
                            "123"
                            123
                            false
                        }
                    """)}
                }
            }
            """.stripIndent()
        }
        assertNoViolations(SOURCES)
    }

    @Test
    void helperMethod_NoViolation() {
        final SOURCE = """
            public class MySpec extends spock.lang.Specification {

                def 'test1'() {
                    when:
                    if (bar()) {
                        assert true
                    }
                    bar()

                    then:
                    [1, 2, 3].each {
                        assert true
                    }
                }

                def 'test2'() {
                    expect:
                    assert true
                }

                private static boolean bar() {
                    def abc = baz.method()
                    if (abc) {
                        false
                    } else {
                        true
                    }
                }
            }
        """.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void defCollectionMethod_NoViolation() {
        final SOURCE = '''
            public class MySpec extends spock.lang.Specification {
                def "defCollectionMethod_NoViolation"() {
                    given:
                    def foo = method1()
                    method2()

                    when:
                    method3()

                    then:
                    def bar = method4()

                    and:
                    def baz = [1,2,3].find {
                        try {
                            method5()
                            true
                        } catch (AssertionError ignored) {
                            false
                        }
                    }
                }
        }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void xmlMarkupBuilder_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "xmlMarkupBuilder_NoViolation"() {
                    expect:
                    "$str" {
                        method()
                    }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void assertWithCollectionMethod_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "assertWithCollectionMethod_NoViolation"() {
                    expect:
                    if (a == b) {
                        assert [1, 2, 3].every { true }
                    }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void nestedClosureInFor_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "nestedClosureInFor_NoViolation"() {
                    given:
                    for (a in [1,2,3]) {
                        then:
                        methodCall {
                            !myCondition()
                        }
                    }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void nestedClosureInCollectionLoops_MultipleViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "nestedClosureInCollectionLoops_MultipleViolations"() {
                    given:
                    for (a in [1,2,3]) {
                        then:
                        [1,2,3].each {
                            !myCondition()
                        }
                        [1,2,3].eachWithIndex {
                            !myCondition()
                        }
                        3.times {
                            !myCondition()
                        }
                    }
                    then:
                    for (a in [1,2,3]) {
                        methodCall({ it in ["a","b","c"] }).each {
                            with(it) {
                                !myCondition1()
                            }
                            assert !myCondition2()
                            !myCondition3()
                        }
                    }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 8, source: '!myCondition()', message: violationMessage('then')],
            [line: 11, source: '!myCondition()', message: violationMessage('then')],
            [line: 14, source: '!myCondition()', message: violationMessage('then')],
            [line: 24, source: '!myCondition3()', message: violationMessage('then')]
        )
    }

    @Test
    void booleanExpressionRelationalOperators_MultipleViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "complexBooleanExpression_MultipleViolations"() {
                    expect:
                    for (a in [1,2,3]) {
                        myCondition() == myVariable
                        myCondition() != myVariable
                        obj.method().myCondition() == obj.method().myVariable
                        obj.method().myCondition() != obj.method().myVariable
                        obj.method() == obj.method()
                        obj.method() != obj.method()
                        obj.method() < obj.method()
                        obj.method() <= obj.method()
                        obj.method() > obj.method()
                        obj.method() >= obj.method()
                        obj.method() === obj.method()
                        obj.method() !== obj.method()
                    }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 6, source: 'myCondition() == myVariable', message: violationMessage('expect')],
            [line: 7, source: 'myCondition() != myVariable', message: violationMessage('expect')],
            [line: 8, source: 'obj.method().myCondition() == obj.method().myVariable', message: violationMessage('expect')],
            [line: 9, source: 'obj.method().myCondition() != obj.method().myVariable', message: violationMessage('expect')],
            [line: 10, source: 'obj.method() == obj.method()', message: violationMessage('expect')],
            [line: 11, source: 'obj.method() != obj.method()', message: violationMessage('expect')],
            [line: 12, source: 'obj.method() < obj.method()', message: violationMessage('expect')],
            [line: 13, source: 'obj.method() <= obj.method()', message: violationMessage('expect')],
            [line: 14, source: 'obj.method() > obj.method()', message: violationMessage('expect')],
            [line: 15, source: 'obj.method() >= obj.method()', message: violationMessage('expect')],
            [line: 16, source: 'obj.method() === obj.method()', message: violationMessage('expect')],
            [line: 17, source: 'obj.method() !== obj.method()', message: violationMessage('expect')]
        )
    }

    @Test
    void booleanExpressionMethodPatterns_MultipleViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "complexBooleanExpression_MultipleViolations"() {
                    expect:
                    for (a in [1,2,3]) {
                        myVar.asBoolean()
                        [1,2,3].any { myCondition() }
                        [1,2,3].contains(4)
                        [1,2,3].every { myCondition() }
                        abc.equals(myVariable)
                    }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 6, source: 'myVar.asBoolean()', message: violationMessage('expect')],
            [line: 7, source: '[1,2,3].any { myCondition() }', message: violationMessage('expect')],
            [line: 8, source: '[1,2,3].contains(4)', message: violationMessage('expect')],
            [line: 9, source: '[1,2,3].every { myCondition() }', message: violationMessage('expect')],
            [line: 10, source: 'abc.equals(myVariable)', message: violationMessage('expect')]
        )
    }

    @Test
    void booleanExpressionLogicalOperators_MultipleViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "complexBooleanExpression_MultipleViolations"() {
                    expect:
                    for (a in [1,2,3]) {
                        obj.method() & obj.method()
                        obj.method() | obj.method()
                        obj.method() ^ obj.method()
                        ~obj.method()
                        !obj.method()
                        obj.method() && obj.method()
                        obj.method() || obj.method()
                    }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 10, source: '!obj.method()', message: violationMessage('expect')],
            [line: 11, source: 'obj.method() && obj.method()', message: violationMessage('expect')],
            [line: 12, source: 'obj.method() || obj.method()', message: violationMessage('expect')],
        )
    }

    @Test
    void booleanExpressionConditionalOperators_MultipleViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "complexBooleanExpression_MultipleViolations"() {
                    expect:
                    for (a in [1,2,3]) {
                        !obj.method()
                        !'foo'
                        obj.method() ? obj.isBoolean() : obj.asBoolean()
                        obj.method() ?: obj.isBoolean()
                    }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 6, source: '!obj.method()', message: violationMessage('expect')],
            [line: 7, source: '!\'foo\'', message: violationMessage('expect')]
            // line: 8 ternary operator is not supported
            // line: 9 elvis operator is not supported
        )
    }

    @Test
    void booleanExpressionRegexOperators_MultipleViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "complexBooleanExpression_MultipleViolations"() {
                    expect:
                    for (a in [1,2,3]) {
                        ~'foo'
                        text =~ /match/
                        text !=~ /match/
                        text ==~ /match/
                    }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            // line: 6 Pattern
            // line: 7 Matcher
            [line: 8, source: 'text !=~ /match/', message: violationMessage('expect')],
            [line: 9, source: 'text ==~ /match/', message: violationMessage('expect')]
        )
    }

    @Test
    void booleanExpressionCasts_MultipleViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "complexBooleanExpression_MultipleViolations"() {
                    expect:
                    for (a in [1,2,3]) {
                        (boolean) obj.method()
                        (Boolean) obj.method()
                        obj.method() as boolean
                        obj.method() as Boolean
                        obj.method() instanceof Boolean
                    }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 6, source: '(boolean) obj.method()', message: violationMessage('expect')],
            [line: 7, source: '(Boolean) obj.method()', message: violationMessage('expect')],
            [line: 8, source: 'obj.method() as boolean', message: violationMessage('expect')],
            [line: 9, source: 'obj.method() as Boolean', message: violationMessage('expect')],
            [line: 10, source: 'obj.method() instanceof Boolean', message: violationMessage('expect')]
        )
    }

    @Test
    void booleanExpressionMiscExpressions_MultipleViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "complexBooleanExpression_MultipleViolations"() {
                    expect:
                    for (a in [1,2,3]) {
                        myCondition()
                        isMyCondition()
                        hasMyCondition()
                        !myCondition()
                        obj.method().myCondition()
                        obj.method().isMyCondition()
                        obj.method().hasMyCondition()
                        !obj.method().myCondition()
                        obj.method() in [1,2,3]
                    }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            // line: 6 myCondition() doesn't match boolean method pattern
            [line: 7, source: 'isMyCondition()', message: violationMessage('expect')],
            [line: 8, source: 'hasMyCondition()', message: violationMessage('expect')],
            [line: 9, source: '!myCondition()', message: violationMessage('expect')],
            // line: 10 myCondition() doesn't match boolean method pattern
            [line: 11, source: 'obj.method().isMyCondition()', message: violationMessage('expect')],
            [line: 12, source: 'obj.method().hasMyCondition()', message: violationMessage('expect')],
            [line: 13, source: '!obj.method().myCondition()', message: violationMessage('expect')],
            [line: 14, source: 'obj.method() in [1,2,3]', message: violationMessage('expect')]
        )
    }

    @Test
    void realisticTest_SingleViolation() {
        final SOURCE = '''
            import spock.lang.*

            class DataDrivenSpec extends Specification {
              def "maximum of two numbers"() {
                expect:
                Math.max(a, b) == c

                where:
                a << [3, 5, 9]
                b << [7, 4, 9]
                c << [7, 5, 9]
              }

              def "minimum of #a and #b is #c"() {
                expect:
                Math.min(a, b) == c

                where:
                a | b || c
                3 | 7 || 3
                5 | 4 || 4
                9 | 9 || 9
              }

              def "#person.name is a #sex.toLowerCase() person"() {
                expect:
                if (true)
                    person.getSex() == sex

                where:
                person                    || sex
                new Person(name: "Fred")  || "Male"
                new Person(name: "Wilma") || "Female"
              }

              static class Person {
                String name
                String getSex() {
                  name == "Fred" ? "Male" : "Female"
                }
              }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 29, 'person.getSex() == sex', violationMessage('expect'))
    }

    @Override
    protected SpockMissingAssertRule createRule() {
        new SpockMissingAssertRule()
    }

    private static List<String> labelsToTest = ['then', 'expect']

    @SuppressWarnings('UnusedPrivateMethod')
    private static Stream<Closure<GString>> statementsToTest() {
        Stream.of(
            (content) -> """
            do {
                $content
            } while (true)
            """,
                (content) -> """
            for (int i = 0; i < 10; i++) {
                $content
            }
            """,
                (content) -> """
            if (true) {
                $content
            }
            """,
                (content) -> """
            if (true) {
                123
            } else if (false) {
                $content
            }
            """,
                (content) -> """
            if (true) {
                123
            } else if (false) {
                456
            } else {
                $content
            }
            """,
                (content) -> """
            switch (123) {
                case 123:
                    $content
                    break
                default:
                    break
            }
            """,
                (content) -> """
            switch (123) {
                case 456:
                    break
                default:
                    $content
                    break
            }
            """,
                (content) -> """
            try {
                $content
            } catch (Exception e) {
                123
            }
            """,
                (content) -> """
            try {
                123
            } catch (Exception e) {
                $content
            }
            """,
                (content) -> """
            try {
                123
            } catch (Exception e) {
                456
            } finally {
                $content
            }
            """,
                (content) -> """
            while (true) {
                $content
            }
            """,
        )
    }

    private static String violationMessage(String label) {
        "'${label}:' might contain a boolean expression in a nested statement, which is not implicitly asserted"
    }

    private void assertNoViolations(List<String> sources) {
        sources.forEach { source -> assertNoViolations(source) }
    }
}

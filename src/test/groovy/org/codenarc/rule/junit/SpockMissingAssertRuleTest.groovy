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
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SpockMissingAssert'
    }

    @Test
    void testTopLevelBoolean_NoViolations() {
        final SOURCES = labelsToTest.collect { label ->
            """
            public class MySpec extends spock.lang.Specification {
                def "testTopLevelExpression_NoViolations"() {
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
    void testStatement_NoViolations(Closure<GString> statement) {
        final SOURCES = labelsToTest.collect { label ->
            """
            public class MySpec extends spock.lang.Specification {
                def "testStatement_NoViolations"() {
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
    void testStatement_SingleViolation(Closure<GString> statement) {
        final SOURCES = labelsToTest.collect { label ->
            new Tuple2<>(
                label,
                """
                public class MySpec extends spock.lang.Specification {
                    def "testStatement_SingleViolation"() {
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
    void testStatementWithDef_NoViolation(Closure<GString> statement) {
        final SOURCES = labelsToTest.collect { label ->
                """
                public class MySpec extends spock.lang.Specification {
                    def "def_NoViolation"() {
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
    void testStatementInWith_NoViolation(Closure<GString> statement) {
        final SOURCES = labelsToTest.collect { label ->
            """
            public class MySpec extends spock.lang.Specification {
                def "testStatementInWith_NoViolation"() {
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
    void testWithInStatement_NoViolation(Closure<GString> statement) {
        final SOURCES = labelsToTest.collect { label ->
            """
            public class MySpec extends spock.lang.Specification {
                def "testWithInStatement_NoViolation"() {
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
    void assertWithNestedClosureInFor_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "assertWithNestedClosureInFor_NoViolation"() {
                    expect:
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
    void assertWithNestedClosureInCollectionLoops_MultipleViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "assertWithNestedClosureInFor_NoViolation"() {
                    expect:
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
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 8, source: '!myCondition()', message: violationMessage('then')],
            [line: 11, source: '!myCondition()', message: violationMessage('then')],
            [line: 14, source: '!myCondition()', message: violationMessage('then')]
        )
    }

    @Test
    void realisticTest_NoViolation() {
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
        assertNoViolations(SOURCE)
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
        "'${label}:' contains a boolean expression in a nested statement, which is not implicitly asserted"
    }

    private void assertNoViolations(List<String> sources) {
        sources.forEach { source -> assertNoViolations(source) }
    }
}

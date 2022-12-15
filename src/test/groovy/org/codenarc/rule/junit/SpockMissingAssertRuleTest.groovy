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
        final SOURCE = '''
            public class MySpec extends spock.lang.Specification {
                def "testTopLevelExpression_NoViolations"() {
                    expect:
                    "123"
                    123
                    false
                    assert false
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @ParameterizedTest
    @MethodSource("statementsToTest")
    void testStatement_NoViolations(Closure<GString> statement) {
        final SOURCE = """
            public class MySpec extends spock.lang.Specification {
                def "testIfStatement_NoViolations"() {
                    expect:
                    ${statement("""
                        "123"
                        123
                        assert false
                    """)}
                }
            }
        """.stripIndent()
        assertNoViolations(SOURCE)
    }

    @ParameterizedTest
    @MethodSource("statementsToTest")
    void testStatement_SingleViolation(Closure<GString> statement) {
        final SOURCE = """
            public class MySpec extends spock.lang.Specification {
                def "testIfStatement_NoViolations"() {
                    expect:
                    ${statement("""
                        "123"
                        123
                        false
                    """)}
                }
            }
        """.stripIndent()
        assertSingleViolation(SOURCE) { Violation violation ->
            violation.sourceLine == 'false' &&
            violation.message == "'expect:' contains a boolean expression in a nested statement, which is not implicitly asserted";
        }
    }

    @ParameterizedTest
    @MethodSource("statementsToTest")
    void testStatementInWith_NoViolation(Closure<GString> statement) {
        final SOURCE = """
            public class MySpec extends spock.lang.Specification {
                def "testIfStatement_NoViolations"() {
                    expect:
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
        assertNoViolations(SOURCE)
    }

    @ParameterizedTest
    @MethodSource("statementsToTest")
    void testWithInStatement_NoViolation(Closure<GString> statement) {
        final SOURCE = """
            public class MySpec extends spock.lang.Specification {
                def "testIfStatement_NoViolations"() {
                    expect:
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
        assertNoViolations(SOURCE)
    }

    @Override
    protected SpockMissingAssertRule createRule() {
        new SpockMissingAssertRule()
    }

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
}

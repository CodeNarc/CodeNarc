/*
 * Copyright 2026 the original author or authors.
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.util.stream.Stream

/**
 * Tests for SpockUseVerifyEachRule
 *
 * @author Leonard Brünings
 */
class SpockUseVerifyEachRuleTest extends AbstractRuleTestCase<SpockUseVerifyEachRule> {

    @Test
    void ruleProperties_AreValid() {
        assert rule.priority == 3
        assert rule.name == 'SpockUseVerifyEach'
        assert rule.checkAllBlocks
    }

    @ParameterizedTest
    @MethodSource('iterationMethodsWithAssert')
    void iterationMethod_InThenAndExpect_TwoViolations(String methodName, String closureBody, int expectLine, int thenLine) {
        final SOURCE = """
            class MySpec extends spock.lang.Specification {
                def "test"() {
                    given:
                    def list = []
                    def names = []

                    expect:
                    list.${methodName} { ${closureBody} }

                    when:
                    "nothing"

                    then:
                    list.${methodName} { ${closureBody} }
                }
            }
        """.stripIndent()
        assertViolations(SOURCE,
            [line: expectLine, source: "list.${methodName} { ${closureBody} }".toString(), message: violationMessage(methodName)],
            [line: thenLine, source: "list.${methodName} { ${closureBody} }".toString(), message: violationMessage(methodName)],
        )
    }

    @Test
    void each_BooleanExpressionInExpectBlock_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test each with boolean"() {
                    expect:
                    list.each { it.name == 'foo' }
                }
            }
        '''.stripIndent()
        assertSingleViolation(SOURCE, 5, "list.each { it.name == 'foo' }", violationMessage('each'))
    }

    @ParameterizedTest
    @MethodSource('nonImplicitAssertBlocks')
    void allBlocks_ThreeViolations(String block) {
        final SOURCE = """
            class MySpec extends spock.lang.Specification {
                def "test all blocks"() {
                    ${block}:
                    list.each { assert it.field == value }
                    list.eachWithIndex { item, idx -> assert item.name == names[idx] }
                    list.forEach { assert it.isValid() }
                    list.every { it.field == value }

                    expect:
                    true
                }
            }
        """.stripIndent()
        rule.checkAllBlocks = true
        assertViolations(SOURCE,
            [line: 5, source: 'list.each { assert it.field == value }', message: violationMessage('each')],
            [line: 6, source: 'list.eachWithIndex { item, idx -> assert item.name == names[idx] }', message: violationMessage('eachWithIndex')],
            [line: 7, source: 'list.forEach { assert it.isValid() }', message: violationMessage('forEach')],
        )
    }

    @Test
    void helperMethod_SingleViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test"() {
                    expect:
                    true
                }

                void assertItems(list) {
                    list.each { assert it.field == value }
                }
            }
        '''.stripIndent()
        rule.checkAllBlocks = true
        assertSingleViolation(SOURCE, 9, 'list.each { assert it.field == value }', violationMessage('each'))
    }

    @Test
    void verifyEach_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test verifyEach"() {
                    expect:
                    verifyEach(list) { it.field == value }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void assignment_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test assignment"() {
                    given:
                    list.each { it.field = 'setup' }

                    expect:
                    true
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void every_InGivenBlock_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test every in given"() {
                    given:
                    list.every { it.field == value }

                    expect:
                    true
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void nonTargetMethod_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test non-target methods"() {
                    expect:
                    list.collect { it.name }
                    list.findAll { it.isValid() }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void nonSpockClass_NoViolation() {
        final SOURCE = '''
            class MyTest extends Foo {
                def "test"() {
                    expect:
                    list.each { assert it.field == value }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void checkAllBlocksFalse_GivenBlock_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test checkAllBlocks false"() {
                    given:
                    list.each { assert it.field == value }

                    expect:
                    true
                }
            }
        '''.stripIndent()
        rule.checkAllBlocks = false
        assertNoViolations(SOURCE)
    }

    @Test
    void noAssertionInClosure_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test no assertion"() {
                    expect:
                    list.each { println it }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void assignmentWithEvery_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test assignment with every"() {
                    given:
                    def allValid = list.every { it.field == value }

                    expect:
                    def result = list.every { it.field == value }

                    when:
                    "nothing"

                    then:
                    def check = list.every { it.field == value }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void conditionWithEvery_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test condition with every"() {
                    expect:
                    if (list.every { it.field == value }) {
                        assert something
                    }

                    when:
                    "nothing"

                    then:
                    if (list.every { it.field == value }) {
                        assert something
                    }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void assignmentWithEach_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test assignment with each"() {
                    given:
                    def result = list.each { assert it.field == value }

                    expect:
                    def x = list.each { assert it.field == value }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Test
    void every_InFilterBlock_NoViolation() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "filter block"() {
                    expect:
                    i == 1
                    c == 2

                    where:
                    i << (1..6)

                    combined:
                    c << (1..3)

                    list = [i, c]

                    filter:
                    list.every { it > 0 }
                }
            }
        '''.stripIndent()
        assertNoViolations(SOURCE)
    }

    @Override
    protected SpockUseVerifyEachRule createRule() {
        new SpockUseVerifyEachRule()
    }

    @SuppressWarnings('UnusedPrivateMethod')
    private static Stream<Arguments> iterationMethodsWithAssert() {
        Stream.of(
            Arguments.of('every', 'it.field == value', 9, 15),
            Arguments.of('each', 'assert it.field == value', 9, 15),
            Arguments.of('eachWithIndex', 'item, idx -> assert item.name == names[idx]', 9, 15),
            Arguments.of('forEach', 'assert it.isValid()', 9, 15),
        )
    }

    @SuppressWarnings('UnusedPrivateMethod')
    private static Stream<String> nonImplicitAssertBlocks() {
        Stream.of('given', 'when')
    }

    private static String violationMessage(String methodName) {
        "Replace '${methodName}' with Spock's 'verifyEach' for better per-item failure diagnostics"
    }
}

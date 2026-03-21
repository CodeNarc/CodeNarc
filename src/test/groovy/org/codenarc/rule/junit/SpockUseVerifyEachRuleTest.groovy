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

/**
 * Tests for SpockUseVerifyEachRule
 *
 * @author Leonard Brünings
 */
class SpockUseVerifyEachRuleTest extends AbstractRuleTestCase<SpockUseVerifyEachRule> {

    private static final String VIOLATION_MESSAGE_EVERY = "Replace 'every' with Spock's 'verifyEach' for better per-item failure diagnostics"
    private static final String VIOLATION_MESSAGE_EACH = "Replace 'each' with Spock's 'verifyEach' for better per-item failure diagnostics"
    private static final String VIOLATION_MESSAGE_EACH_WITH_INDEX = "Replace 'eachWithIndex' with Spock's 'verifyEach' for better per-item failure diagnostics"
    private static final String VIOLATION_MESSAGE_FOR_EACH = "Replace 'forEach' with Spock's 'verifyEach' for better per-item failure diagnostics"

    @Test
    void ruleProperties_AreValid() {
        assert rule.priority == 2
        assert rule.name == 'SpockUseVerifyEach'
        assert rule.checkAllBlocks
    }

    @Test
    void every_InThenAndExpect_TwoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test every"() {
                    given:
                    def list = []

                    expect:
                    list.every { it.field == value }

                    when:
                    "nothing"

                    then:
                    list.every { it.field == value }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 8, source: 'list.every { it.field == value }', message: VIOLATION_MESSAGE_EVERY],
            [line: 14, source: 'list.every { it.field == value }', message: VIOLATION_MESSAGE_EVERY],
        )
    }

    @Test
    void each_InThenAndExpect_TwoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test each"() {
                    given:
                    def list = []

                    expect:
                    list.each { assert it.field == value }

                    when:
                    "nothing"

                    then:
                    list.each { assert it.field == value }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 8, source: 'list.each { assert it.field == value }', message: VIOLATION_MESSAGE_EACH],
            [line: 14, source: 'list.each { assert it.field == value }', message: VIOLATION_MESSAGE_EACH],
        )
    }

    @Test
    void eachWithIndex_InThenAndExpect_TwoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test eachWithIndex"() {
                    given:
                    def list = []
                    def names = []

                    expect:
                    list.eachWithIndex { item, idx -> assert item.name == names[idx] }

                    when:
                    "nothing"

                    then:
                    list.eachWithIndex { item, idx -> assert item.name == names[idx] }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 9, source: 'list.eachWithIndex { item, idx -> assert item.name == names[idx] }', message: VIOLATION_MESSAGE_EACH_WITH_INDEX],
            [line: 15, source: 'list.eachWithIndex { item, idx -> assert item.name == names[idx] }', message: VIOLATION_MESSAGE_EACH_WITH_INDEX],
        )
    }

    @Test
    void forEach_InThenAndExpect_TwoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test forEach"() {
                    given:
                    def list = []

                    expect:
                    list.forEach { assert it.isValid() }

                    when:
                    "nothing"

                    then:
                    list.forEach { assert it.isValid() }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 8, source: 'list.forEach { assert it.isValid() }', message: VIOLATION_MESSAGE_FOR_EACH],
            [line: 14, source: 'list.forEach { assert it.isValid() }', message: VIOLATION_MESSAGE_FOR_EACH],
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
        assertSingleViolation(SOURCE, 5, "list.each { it.name == 'foo' }", VIOLATION_MESSAGE_EACH)
    }

    @Test
    void allBlocks_GivenBlock_ThreeViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test all blocks given"() {
                    given:
                    list.each { assert it.field == value }
                    list.eachWithIndex { item, idx -> assert item.name == names[idx] }
                    list.forEach { assert it.isValid() }
                    list.every { it.field == value }

                    expect:
                    true
                }
            }
        '''.stripIndent()
        rule.checkAllBlocks = true
        assertViolations(SOURCE,
            [line: 5, source: 'list.each { assert it.field == value }', message: VIOLATION_MESSAGE_EACH],
            [line: 6, source: 'list.eachWithIndex { item, idx -> assert item.name == names[idx] }', message: VIOLATION_MESSAGE_EACH_WITH_INDEX],
            [line: 7, source: 'list.forEach { assert it.isValid() }', message: VIOLATION_MESSAGE_FOR_EACH],
        )
    }

    @Test
    void allBlocks_WhenBlock_ThreeViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test all blocks when"() {
                    when:
                    list.each { assert it.field == value }
                    list.eachWithIndex { item, idx -> assert item.name == names[idx] }
                    list.forEach { assert it.isValid() }
                    list.every { it.field == value }

                    then:
                    true
                }
            }
        '''.stripIndent()
        rule.checkAllBlocks = true
        assertViolations(SOURCE,
            [line: 5, source: 'list.each { assert it.field == value }', message: VIOLATION_MESSAGE_EACH],
            [line: 6, source: 'list.eachWithIndex { item, idx -> assert item.name == names[idx] }', message: VIOLATION_MESSAGE_EACH_WITH_INDEX],
            [line: 7, source: 'list.forEach { assert it.isValid() }', message: VIOLATION_MESSAGE_FOR_EACH],
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
        assertSingleViolation(SOURCE, 9, 'list.each { assert it.field == value }', VIOLATION_MESSAGE_EACH)
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

    @Override
    protected SpockUseVerifyEachRule createRule() {
        new SpockUseVerifyEachRule()
    }
}

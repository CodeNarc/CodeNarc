/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for ToStringReturnsNullRule
 *
 * @author Chris Mair
 */
class ToStringReturnsNullRuleTest extends AbstractRuleTestCase<ToStringReturnsNullRule> {

    private static final String ERROR_MESSAGE = 'The toString() method within class MyClass returns null'

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ToStringReturnsNull'
    }

    @Test
    void testToString_ReturnsNonNull_NoViolation() {
        final SOURCE = '''
            class MyClass {
                String toString() {
                    return 'MyClass'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testToStringWithParameters_NoViolation() {
        final SOURCE = '''
            class MyClass {
                String toString(int count) { return null }
                void toString(String name) { return null }
                String toString(String name, int count) { return null }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleReturn_ReturnsNull_Violation() {
        final SOURCE = '''
            class MyClass {
                String toString() {
                    return null
                }
            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'return null', message:ERROR_MESSAGE])
    }

    @Test
    void testNoReturnKeyword_Null_Violation() {
        final SOURCE = '''
            class MyClass {
                String toString() {
                    calculateStuff()
                    null
                }
            }
        '''
        assertViolations(SOURCE,
            [line:5, source:'null', message: ERROR_MESSAGE])
    }

    @Test
    void testEmptyMethod_ImplicitlyReturnsNull_Violation() {
        final SOURCE = '''
            class MyClass {
                String toString() {
                }
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'String toString()', message: ERROR_MESSAGE])
    }

    @Test
    void testMultipleReturns_MayReturnNull_Violation() {
        final SOURCE = '''
            class MyClass {
                String toString() {
                    if (foo()) {
                        return 'MyClass'
                    } else {
                        return null
                    }
                }
            }
        '''
        assertViolations(SOURCE,
            [line:7, source:'return null', message:ERROR_MESSAGE])
    }

    @Test
    void testToString_ContainsClosure_NoViolation() {
        final SOURCE = '''
            class MyClass {
                @Override
                String toString() {
                    StringBuilder sb = new StringBuilder()
                    (1..10).each { int index ->
                        sb << getCharacter[index]
                    }
                    return sb.toString();
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected ToStringReturnsNullRule createRule() {
        new ToStringReturnsNullRule()
    }
}

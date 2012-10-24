/*
 * Copyright 2010 the original author or authors.
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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for CompareToWithoutComparableRule
 *
 * @author Hamlet D'Arcy
 */
class CompareToWithoutComparableRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CompareToWithoutComparable'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass implements Comparable {
                int compareTo(Object o) {
                    0
                }
            }
            class MyClass2 implements Serializable, Comparable {
                int compareTo(Object o) {
                    0
                }
            }
            class MyClass3 {
                int compareTo(Object o1, Object o2) {
                    0
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoInterfaces() {
        final SOURCE = '''
            class MyClass {
                int compareTo(Object o) {
                    0
                }
            }
        '''
        assertSingleViolation(SOURCE, 2, 'class MyClass', 'compareTo method at line 3 would implement Comparable.compareTo(Object) but the enclosing class does not implement Comparable')
    }

    @Test
    void testTwoInterfaces() {
        final SOURCE = '''
            class MyClass implements Serializable, Cloneable {
                int compareTo(Object o) {
                    0
                }
            }
        '''
        assertSingleViolation(SOURCE, 2, 'class MyClass', 'compareTo method at line 3 would implement Comparable.compareTo(Object) but the enclosing class does not implement Comparable')
    }

    protected Rule createRule() {
        new CompareToWithoutComparableRule()
    }
}

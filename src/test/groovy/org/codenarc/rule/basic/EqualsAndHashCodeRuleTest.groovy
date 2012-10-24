/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for EqualsAndHashCodeRule
 *
 * @author Chris Mair
 */
class EqualsAndHashCodeRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EqualsAndHashCode'
    }

    @Test
    void testApplyTo_EqualsButNoHashCode() {
        final SOURCE = '''
            class MyClass {
                boolean equals(Object object) {
                    // do something
                }
            }
        '''
        assertSingleViolation(SOURCE, 2, 'class MyClass {')
    }

    @Test
    void testApplyTo_HashCodeButNoEquals() {
        final SOURCE = '''
            class MyClass {
                int hashCode() {
                    return 0
                }
            }
        '''
        assertSingleViolation(SOURCE, 2, 'class MyClass {')
    }

    @Test
    void testApplyTo_BothEqualsAndHashCode() {
        final SOURCE = '''
            class MyClass {
                boolean equals(java.lang.Object object) {
                    return true
                }
                int hashCode() {
                    return 0
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_EqualsButWithDifferentSignature() {
        final SOURCE = '''
            class MyClass {
                boolean equals(String string) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_HashCodeButWithDifferentSignature() {
        final SOURCE = '''
            class MyClass {
                int hashCode(String value) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NeitherEqualsNorHashCode() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    def b = new String(myBytes)
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new EqualsAndHashCodeRule()
    }

}

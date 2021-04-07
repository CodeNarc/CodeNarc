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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for UnnecessarySafeNavigationOperatorRule
 *
 * @author Chris Mair
 */
class UnnecessarySafeNavigationOperatorRuleTest extends AbstractRuleTestCase<UnnecessarySafeNavigationOperatorRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessarySafeNavigationOperator'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            def myMethod() {
                x?.toString()
                x?.y?.z
                this.name
                123.class
                [1].size()
                [:].class
                [abc:123].size()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSafeNavigationOperator_PropertyAccess_Violations() {
        final SOURCE = '''
            def myMethod() {
                "abc"?.bytes
                [1,2]?.name
                [abc:123]?.name
                [:]?.name
                123?.class
                123.45?.class
                Boolean.FALSE?.class
                Boolean.TRUE?.class
                this?.class
                super?.class
                new Long(0)?.class
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'"abc"?.bytes', message:'The safe navigation operator (?.) is unnecessary for "abc" in class None'],
            [line:4, source:'[1,2]?.name', message:'The safe navigation operator (?.) is unnecessary for "[1, 2]" in class None'],
            [line:5, source:'[abc:123]?.name', message:'The safe navigation operator (?.) is unnecessary for "[abc:123]" in class None'],
            [line:6, source:'[:]?.name', message:'The safe navigation operator (?.) is unnecessary for'], // Older versions of Groovy show [] instead of [:]
            [line:7, source:'123?.class', message:'The safe navigation operator (?.) is unnecessary for "123" in class None'],
            [line:8, source:'123.45?.class', message:'The safe navigation operator (?.) is unnecessary for "123.45" in class None'],
            [line:9, source:'Boolean.FALSE?.class', message:'The safe navigation operator (?.) is unnecessary for "Boolean.FALSE" in class None'],
            [line:10, source:'Boolean.TRUE?.class', message:'The safe navigation operator (?.) is unnecessary for "Boolean.TRUE" in class None'],
            [line:11, source:'this?.class', message:'The safe navigation operator (?.) is unnecessary for "this" in class None'],
            [line:12, source:'super?.class', message:'The safe navigation operator (?.) is unnecessary for "super" in class None'],
            [line:13, source:'new Long(0)?.class', message:'The safe navigation operator (?.) is unnecessary for "new Long(0)" in class None'],
        )
    }

    @Test
    void testSafeNavigationOperator_MethodCall_Violations() {
        final SOURCE = '''
            def myMethod() {
                "abc"?.toString()
                [1,2]?.toString()
                [abc:123]?.toString()
                [:]?.toString()
                123?.getClass()
                123.45?.getClass()
                Boolean.FALSE?.getClass()
                Boolean.TRUE?.getClass()
                this?.getClass()
                super?.getClass()
                new Long(100)?.toString()
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'"abc"?.toString()', message:'The safe navigation operator (?.) is unnecessary for "abc" in class None'],
            [line:4, source:'[1,2]?.toString()', message:'The safe navigation operator (?.) is unnecessary for "[1, 2]" in class None'],
            [line:5, source:'[abc:123]?.toString()', message:'The safe navigation operator (?.) is unnecessary for "[abc:123]" in class None'],
            [line:6, source:'[:]?.toString()', message:'The safe navigation operator (?.) is unnecessary for'], // Older versions of Groovy show [] instead of [:]
            [line:7, source:'123?.getClass()', message:'The safe navigation operator (?.) is unnecessary for "123" in class None'],
            [line:8, source:'123.45?.getClass()', message:'The safe navigation operator (?.) is unnecessary for "123.45" in class None'],
            [line:9, source:'Boolean.FALSE?.getClass()', message:'The safe navigation operator (?.) is unnecessary for "Boolean.FALSE" in class None'],
            [line:10, source:'Boolean.TRUE?.getClass()', message:'The safe navigation operator (?.) is unnecessary for "Boolean.TRUE" in class None'],
            [line:11, source:'this?.getClass()', message:'The safe navigation operator (?.) is unnecessary for "this" in class None'],
            [line:12, source:'super?.getClass()', message:'The safe navigation operator (?.) is unnecessary for "super" in class None'],
            [line:13, source:'new Long(100)?.toString()', message:'The safe navigation operator (?.) is unnecessary for "new Long(100)" in class None'],
        )
    }

    @Test
    void testSafeNavigationOperator_MultipleAssignment() {
        final SOURCE = '''
            def myMethod() {
                def (timeA, timeB) = [a, b]*.timeLast
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected UnnecessarySafeNavigationOperatorRule createRule() {
        new UnnecessarySafeNavigationOperatorRule()
    }
}

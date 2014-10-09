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

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for UnnecessarySafeNavigationOperatorRule
 *
 * @author Chris Mair
 */
class UnnecessarySafeNavigationOperatorRuleTest extends AbstractRuleTestCase {

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
            [lineNumber:3, sourceLineText:'"abc"?.bytes', messageText:'The safe navigation operator (?.) is unnecessary for "abc" in class None'],
            [lineNumber:4, sourceLineText:'[1,2]?.name', messageText:'The safe navigation operator (?.) is unnecessary for "[1, 2]" in class None'],
            [lineNumber:5, sourceLineText:'[abc:123]?.name', messageText:'The safe navigation operator (?.) is unnecessary for "[abc:123]" in class None'],
            [lineNumber:6, sourceLineText:'[:]?.name', messageText:'The safe navigation operator (?.) is unnecessary for'], // Older versions of Groovy show [] instead of [:]
            [lineNumber:7, sourceLineText:'123?.class', messageText:'The safe navigation operator (?.) is unnecessary for "123" in class None'],
            [lineNumber:8, sourceLineText:'123.45?.class', messageText:'The safe navigation operator (?.) is unnecessary for "123.45" in class None'],
            [lineNumber:9, sourceLineText:'Boolean.FALSE?.class', messageText:'The safe navigation operator (?.) is unnecessary for "Boolean.FALSE" in class None'],
            [lineNumber:10, sourceLineText:'Boolean.TRUE?.class', messageText:'The safe navigation operator (?.) is unnecessary for "Boolean.TRUE" in class None'],
            [lineNumber:11, sourceLineText:'this?.class', messageText:'The safe navigation operator (?.) is unnecessary for "this" in class None'],
            [lineNumber:12, sourceLineText:'super?.class', messageText:'The safe navigation operator (?.) is unnecessary for "super" in class None'],
            [lineNumber:13, sourceLineText:'new Long(0)?.class', messageText:'The safe navigation operator (?.) is unnecessary for "new Long(0)" in class None'],
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
            [lineNumber:3, sourceLineText:'"abc"?.toString()', messageText:'The safe navigation operator (?.) is unnecessary for "abc" in class None'],
            [lineNumber:4, sourceLineText:'[1,2]?.toString()', messageText:'The safe navigation operator (?.) is unnecessary for "[1, 2]" in class None'],
            [lineNumber:5, sourceLineText:'[abc:123]?.toString()', messageText:'The safe navigation operator (?.) is unnecessary for "[abc:123]" in class None'],
            [lineNumber:6, sourceLineText:'[:]?.toString()', messageText:'The safe navigation operator (?.) is unnecessary for'], // Older versions of Groovy show [] instead of [:]
            [lineNumber:7, sourceLineText:'123?.getClass()', messageText:'The safe navigation operator (?.) is unnecessary for "123" in class None'],
            [lineNumber:8, sourceLineText:'123.45?.getClass()', messageText:'The safe navigation operator (?.) is unnecessary for "123.45" in class None'],
            [lineNumber:9, sourceLineText:'Boolean.FALSE?.getClass()', messageText:'The safe navigation operator (?.) is unnecessary for "Boolean.FALSE" in class None'],
            [lineNumber:10, sourceLineText:'Boolean.TRUE?.getClass()', messageText:'The safe navigation operator (?.) is unnecessary for "Boolean.TRUE" in class None'],
            [lineNumber:11, sourceLineText:'this?.getClass()', messageText:'The safe navigation operator (?.) is unnecessary for "this" in class None'],
            [lineNumber:12, sourceLineText:'super?.getClass()', messageText:'The safe navigation operator (?.) is unnecessary for "super" in class None'],
            [lineNumber:13, sourceLineText:'new Long(100)?.toString()', messageText:'The safe navigation operator (?.) is unnecessary for "new Long(100)" in class None'],
        )
    }

    protected Rule createRule() {
        new UnnecessarySafeNavigationOperatorRule()
    }
}

/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for TernaryCouldBeElvisRule
 *
 * @author Chris Mair
 */
class TernaryCouldBeElvisRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'TernaryCouldBeElvis'
    }

    @Test
    void testElvis_NoViolation() {
        final SOURCE = '''
            x ?: 1
            x ?: null
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTernaryWithDifferentBooleanAndTrueVariableExpressions_NoViolation() {
        final SOURCE = '''
            (x == y) ? same : diff
            (x) ? same : diff

            x ? y : z
            x ? 99 : 33
            x ? x + 1 : x + 2
            x ? 1 : 0
            x ? !x : x
            !x ? x : null
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTernary_SimpleVariable_SameBooleanAndTrueExpression_Violation() {
        final SOURCE = '''
            x ? x : false
        '''
        assertSingleViolation(SOURCE, 2, 'x ? x : false', 'x ?: false')
    }

    @Test
    void testTernaryWithDifferentBooleanAndTrueMethodCalls_NoViolation() {
        final SOURCE = '''
            foo() ? bar() : 123
            foo() ? foo(99) : 123
            foo(x) ? foo() : 123
            foo(1) ? foo(2) : 123
            foo(1,2) ? foo(1) : 123
            foo(1) ? !foo(1) : 123
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTernary_MethodCall_SameBooleanAndTrueExpression_Violation() {
        final SOURCE = '''
            foo() ? foo() : bar()
            foo(1) ? foo(1) : 123
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'foo() ? foo() : bar()', messageText:'foo() ?: this.bar()'],
            [lineNumber:3, sourceLineText:'foo(1) ? foo(1) : 123', messageText:'foo(1) ?: 123'])
    }

    protected Rule createRule() {
        new TernaryCouldBeElvisRule()
    }
}

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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryNullCheckBeforeInstanceOfRule
 *
 * @author 'Hamlet D'Arcy'
 * @author Chris Mair
 */
class UnnecessaryNullCheckBeforeInstanceOfRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryNullCheckBeforeInstanceOf'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            if (x instanceof MyClass) { }
            if (x instanceof MyClass) { } else if (x instanceof OtherClass) {}
            (x instanceof MyClass) ? foo : bar
            if (x != null && y instanceof MyClass) { } // different references
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIfStatement() {
        final SOURCE = '''
            if (x != null && x instanceof MyClass) { }
        '''
        assertSingleViolation(SOURCE, 2,
                '(x != null && x instanceof MyClass)',
                'The condition ((x != null) && (x instanceof MyClass)) can be safely simplified to (x instanceof MyClass)')
    }

    @Test
    void testElseIfStatement() {
        final SOURCE = '''
            if (foo) {} else if (x != null && x instanceof MyClass) { }
        '''
        assertSingleViolation(SOURCE, 2,
                '(x != null && x instanceof MyClass)',
                'The condition ((x != null) && (x instanceof MyClass)) can be safely simplified to (x instanceof MyClass)')
    }

    @Test
    void testTernaryStatement() {
        final SOURCE = '''
            (x != null && x instanceof MyClass) ? foo : bar
        '''
        assertSingleViolation(SOURCE, 2,
                '(x != null && x instanceof MyClass)',
                'The condition ((x != null) && (x instanceof MyClass)) can be safely simplified to (x instanceof MyClass)')
    }

    @Test
    void testTernaryStatementSwappedOrder() {
        final SOURCE = '''
            (x instanceof MyClass && x != null) ? foo : bar
        '''
        assertSingleViolation(SOURCE, 2,
                '(x instanceof MyClass && x != null)',
                'The condition ((x instanceof MyClass) && (x != null)) can be safely simplified to (x instanceof MyClass)')
    }

    @Test
    void testStandaloneBinaryExpression() {
        final SOURCE = '''
            boolean equals(Object object) {
                object != null && object instanceof RequestKey
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'object != null && object instanceof RequestKey',
                'The condition ((object != null) && (object instanceof RequestKey)) can be safely simplified to (object instanceof RequestKey)')
    }

    protected Rule createRule() {
        new UnnecessaryNullCheckBeforeInstanceOfRule()
    }
}

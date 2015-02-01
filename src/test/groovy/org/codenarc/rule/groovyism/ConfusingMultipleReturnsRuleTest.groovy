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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ConfusingMultipleReturnsRule
 *
 * @author Hamlet D'Arcy
 */
class ConfusingMultipleReturnsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ConfusingMultipleReturns'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            final uninitialized_variable
            def x = 1              // ok
            def z = null
            def (f, g) = [1, 2]    // ok
            (a, b, c) = [1, 2, 3]  // ok
            class MyClass {
                def a = null, b = null, c = null
                def d, e, f
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDeclaration() {
        final SOURCE = '''
            def c
            def d = null
            def e, f, g
            def h = null, i = null, j = null
            def a, b = [1, 2] // bad, b is null
        '''
        assertSingleViolation(SOURCE, 6, 'def a, b = [1, 2]', 'Confusing declaration in class None. The variable \'a\' is initialized to null')
    }

    @Test
    void testInClass() {
        final SOURCE = '''
            class MyClass {
                def a, b, c = [1, 2, 3]  // bad, a and b are null
            }
        '''
        assertTwoViolations(SOURCE,
            3, 'def a, b, c = [1, 2, 3]', "Confusing declaration in class MyClass. The field 'a' is initialized to null",
            3, 'def a, b, c = [1, 2, 3]', "Confusing declaration in class MyClass. The field 'b' is initialized to null", )
    }

    protected Rule createRule() {
        new ConfusingMultipleReturnsRule()
    }
}

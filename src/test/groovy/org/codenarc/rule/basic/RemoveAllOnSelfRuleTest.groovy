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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for RemoveAllOnSelfRule
 *
 * @author Hamlet D'Arcy
 */
class RemoveAllOnSelfRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'RemoveAllOnSelf'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
        	def x = [1, 2, 3]

            x.clear()
            x.removeAll(otherVariable)
            x.removeAll(x, otherVariable)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolation() {
        final SOURCE = '''
            def x = [1, 2, 3]
            x.removeAll(x)
        '''
        assertSingleViolation(SOURCE, 3, 'x.removeAll(x)', 'A call to x.removeAll(x) can be replaced with x.clear()')
    }

    @Test
    void testViolationInClass() {
        final SOURCE = '''
            class MyClass {
                List x = [1, 2, 3]
                def someMethod() {
                    x.removeAll(x)
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'x.removeAll(x)')
    }

    protected Rule createRule() {
        new RemoveAllOnSelfRule()
    }

}

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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryCallForLastElementRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryCallForLastElementRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryCallForLastElement'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
                def x = [0, 1, 2]
                def a = x.last()
                def b = x[-1]
                def c = x.getAt(-1)
                def d = x.get(z.size() -1)     // different objects
                def e = x.get(z.length -1)     // different objects
                def f = x.getAt(z.size() -1)   // different objects
                x.get(x.size() - 2)
                x.get(x.length - 2)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testGetAccessList() {
        final SOURCE = '''
            x.get(x.size() - 1)
        '''
        assertSingleViolation SOURCE, 2,
                'x.get(x.size() - 1)', 'Unnecessarily complex access of last element. This can be simplified to x.last() or x[-1]'
    }

    @Test
    void testGetAccessArray() {
        final SOURCE = '''
            x.get(x.length - 1)
        '''
        assertSingleViolation SOURCE,
                2, 'x.get(x.length - 1)', 'Unnecessarily complex access of last element. This can be simplified to x.last() or x[-1]'
    }

    @Test
    void testGetAtAccessList() {
        final SOURCE = '''
            x.getAt(x.size() - 1)
        '''
        assertSingleViolation SOURCE,
                2, 'x.getAt(x.size() - 1)', 'Unnecessarily complex access of last element. This can be simplified to x.last() or x[-1]'
    }

    @Test
    void testGetAtAccessArray() {
        final SOURCE = '''
            x.getAt(x.length -1)
        '''
        assertSingleViolation SOURCE,
                2, 'x.getAt(x.length -1)', 'Unnecessarily complex access of last element. This can be simplified to x.last() or x[-1]'
    }

    @Test
    void testArrayStyleAccess() {
        final SOURCE = '''
            x[x.size() -1]
            x[x.length -1]
        '''
        assertTwoViolations SOURCE,
                2, 'x[x.size() -1]', 'Unnecessarily complex access of last element. This can be simplified to x.last() or x[-1]',
                3, 'x[x.length -1]', 'Unnecessarily complex access of last element. This can be simplified to x.last() or x[-1]'
    }

    protected Rule createRule() {
        new UnnecessaryCallForLastElementRule()
    }
}

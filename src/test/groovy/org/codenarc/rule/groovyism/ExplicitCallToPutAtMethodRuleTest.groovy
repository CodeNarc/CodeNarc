/*
 * Copyright 2019 the original author or authors.
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
import org.junit.Test

/**
 * Tests for ExplicitCallToPutAtMethodRule
 *
 * @author Chris Mair
 */
class ExplicitCallToPutAtMethodRuleTest extends AbstractRuleTestCase<ExplicitCallToPutAtMethodRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitCallToPutAtMethod'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            a[b] = c
            a.putAt()
            a.putAt(a)
            a.putAt(a, b, c)

            super.putAt(k, v)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_ThisPutAt_ignoreThisReference_NoViolations() {
        final SOURCE = '''
            putAt(a, b)
        '''
        rule.ignoreThisReference = true
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolation() {
        final SOURCE = '''
            map.putAt(k, v)
        '''
        assertViolations(SOURCE,
                [line:2, source:'map.putAt(k, v)', message:'Explicit call to map.putAt(k, v) method can be rewritten as map[k] = v'])
    }

    @Test
    void testThisPutAt_Violation() {
        final SOURCE = '''
            putAt(k, v)
            this.putAt(a, b)
        '''
        assertViolations(SOURCE,
                [line:2, source:'putAt(k, v)', message:'Explicit call to this.putAt(k, v) method can be rewritten as this[k] = v'],
                [line:3, source:'this.putAt(a, b)', message:'Explicit call to this.putAt(a, b) method can be rewritten as this[a] = b'])
    }

    @Override
    protected ExplicitCallToPutAtMethodRule createRule() {
        new ExplicitCallToPutAtMethodRule()
    }
}

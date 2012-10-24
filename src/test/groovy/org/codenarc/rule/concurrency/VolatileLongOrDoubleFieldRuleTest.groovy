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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Unit test for VolatileLongOrDoubleFieldRule.
 * 
 * @author Hamlet D'Arcy
 */
class VolatileLongOrDoubleFieldRuleTest extends AbstractRuleTestCase {
    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'VolatileLongOrDoubleField'
    }

    @Test
    void testApplyTo_Violation_Doubles() {
        final SOURCE = '''
            class VolatileLongOrDoubleFieldClass1 {
                private volatile double d
                private volatile Double e
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'private volatile double d',
                4, 'private volatile Double e')
    }

    @Test
    void testApplyTo_Violation_Floats() {
        final SOURCE = '''
            class VolatileLongOrDoubleFieldClass2 {
                private volatile long f
                private volatile Long g
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'private volatile long f',
                4, 'private volatile Long g')
    }

    @Test
    void testApplyTo_Violation_FloatsWithoutModifier() {
        final SOURCE = '''
            class VolatileLongOrDoubleFieldClass3 {
                def volatile long f
                def volatile Long g
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def volatile long f',
                4, 'def volatile Long g')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class VolatileLongOrDoubleFieldClass4 {
                double d
                Double e
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new VolatileLongOrDoubleFieldRule()
    }
}

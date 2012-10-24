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
 * Tests for WaitOutsideOfWhileLoopRule
 *
 * @author Chris Mair
 */
class WaitOutsideOfWhileLoopRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'WaitOutsideOfWhileLoop'
    }

    @Test
    void testApplyTo_WaitWithinWhileLoop_NoViolation() {
        final SOURCE = '''
            synchronized(data) {
                while (!data.isReady()) {
                    data.wait()
                }
                data.calculateStatistics()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_WaitWithinNestedLoop_NoViolation() {
        final SOURCE = '''
            synchronized(data) {
                while(dataFile.hasRecords()) {
                    doStuff()
                    while(!data.isReady()) {
                        data.wait()
                    }
                    processData()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_WaitNotWithinWhileLoop_Violation() {
        final SOURCE = '''
            synchronized(data) {
                data.wait()
                data.calculateStatistics()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'data.wait()')
    }

    @Test
    void testApplyTo_WaitWithinForLoop_Violation() {
        final SOURCE = '''
            synchronized(data) {
                for(;!data.isReady();) {
                    data.wait()
                }
                data.calculateStatistics()
            }
        '''
        assertSingleViolation(SOURCE, 4, 'data.wait()')
    }

    protected Rule createRule() {
        new WaitOutsideOfWhileLoopRule()
    }
}

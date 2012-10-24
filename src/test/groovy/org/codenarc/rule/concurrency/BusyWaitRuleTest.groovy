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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for BusyWaitRule
 *
 * @author 'Hamlet D'Arcy'
 */
class BusyWaitRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BusyWait'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            // here is the proper way to wait:
            countDownLatch.await()

            // this is weird code to write, but does not cause a violation
            // sleep inside a for-each loop is OK for some reason
            for (def x : collections) {
                sleep(1000)
            }

            for (def x = 0; x < 10; x++) {
                sleep(1000, 1, 3)   // sleep does not take 3 parameters
            }

            while (x) {
                // you should use a lock here, but technically you are
                // not just busy waiting because you are doing other work
                doSomething()
                sleep(1000) 
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testWhileLoop() {
        final SOURCE = '''
            while (x) {
                Thread.sleep(1000)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'Thread.sleep(1000)', 'Busy wait detected. Switch the usage of Thread.sleep() to a lock or gate from java.util.concurrent')
    }

    @Test
    void testWhileLoopWithClosure() {
        final SOURCE = '''
            while (x) {
                sleep(1000) { /* interruption handler */}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'sleep(1000)', 'Busy wait detected. Switch the usage of Thread.sleep() to a lock or gate from java.util.concurrent')
    }

    @Test
    void testForLoop() {
        final SOURCE = '''
            for (int x = 10; x; x--) {
                sleep(1000)     // sleep is added to Object in Groovy
            }
        '''
        assertSingleViolation(SOURCE, 3, 'sleep(1000)', 'Busy wait detected. Switch the usage of Thread.sleep() to a lock or gate from java.util.concurrent')
    }

    protected Rule createRule() {
        new BusyWaitRule()
    }
}

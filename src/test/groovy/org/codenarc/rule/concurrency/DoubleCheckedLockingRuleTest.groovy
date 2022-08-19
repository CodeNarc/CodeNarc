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
import org.junit.jupiter.api.Test

/**
 * Tests for DoubleCheckedLockingRule
 *
 * @author 'Hamlet D'Arcy'
 */
class DoubleCheckedLockingRuleTest extends AbstractRuleTestCase<DoubleCheckedLockingRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'DoubleCheckedLocking'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            def result = object
            if (result == null) {
                synchronized(this) {
                    result = object
                    if (result == null)
                        object = result = createObject()
                }
            }

            // and a better solution for a singleton:
            class myClass  {
                private static class ObjectHolder {
                   public static Object object = createObject()
                }
                public static Object getObject() {
                    return ObjectHolder.object
                }
            }

            if (object1 == null) {
                synchronized(this) {
                    if (object1 == null) {
                        object2 = createObject()
                    }
                }
            }

            if (object == 5) {
                synchronized(this) {
                    if (object == null) {
                        object = createObject()
                    }
                }
            }

            if (object == 5) {
                synchronized(this) {
                    if (5 == object) {
                        object = createObject()
                    }
                }
            }

            if (object == 5) {
                synchronized(this) {
                    if (object == null) {
                        x = createObject()
                    }
                }
            }

            if (object == 5) {
                synchronized(this) {
                    if (object == null) {
                        doSomething()
                        object = createObject()
                    }
                }
            }

            if (object == 5) {
                synchronized(this) {
                    doSomething()
                    if (object == null) {
                        object = createObject()
                    }
                }
            }

            if (object == 5) {
                doSomething()
                synchronized(this) {
                    if (object == null) {
                        object = createObject()
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testPlainViolation() {
        final SOURCE = '''
            if (object == null) {
                synchronized(this) {
                    if (object == null) {
                        object = createObject()
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'object = createObject()', 'Double checked locking detected for variable object. replace with more robust lazy initialization')
    }

    @Test
    void testBackwardIfViolations() {
        final SOURCE = '''
            if (null == object) {
                synchronized(this) {
                    if (null == object) {
                        object = createObject()
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'object = createObject()', 'Double checked locking detected for variable object. replace with more robust lazy initialization')
    }

    @Test
    void testNoCompare() {
        final SOURCE = '''
            if (!object) {
                synchronized(this) {
                    if (!object) {
                        object = createObject()
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'object = createObject()', 'Double checked locking detected for variable object. replace with more robust lazy initialization')
    }

    @Override
    protected DoubleCheckedLockingRule createRule() {
        new DoubleCheckedLockingRule()
    }
}

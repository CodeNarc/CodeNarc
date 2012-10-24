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
package org.codenarc.rule.logging

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for MultipleLoggersRule
 *
 * @author 'Hamlet D'Arcy'
  */
class MultipleLoggersRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'MultipleLoggers'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                private static final LOG = Logger.getLogger(MyClass)
                private static final LOG2 = '' // not a logger
            }
            class MyOtherClass {
                private static final LOG3 = Logger.getLogger(MyClass)
                class MyInnerClass {
                    private static final LOG4 = Logger.getLogger(MyClass)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class MyClass {
                def LOG = Logger.getLogger(MyClass)
                def logger = Logger.getLogger(MyClass)
            }
        '''
        assertSingleViolation(SOURCE, 4, 'def logger = Logger.getLogger(MyClass)', 'Violation in class MyClass. The class defines multiple loggers: LOG, logger')
    }

    @Test
    void testInnerClass() {
        final SOURCE = '''
            class MyClass {
                class MyInnerClass {
                    def LOG = Logger.getLogger(MyClass)
                    def logger = Logger.getLogger(MyClass)
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'def logger = Logger.getLogger(MyClass)',
            'Violation in class MyClass$MyInnerClass. The class defines multiple loggers: LOG, logger')
    }

    protected Rule createRule() {
        new MultipleLoggersRule()
    }
}

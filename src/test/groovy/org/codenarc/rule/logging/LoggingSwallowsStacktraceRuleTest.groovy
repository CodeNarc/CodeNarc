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
 * Tests for LoggingSwallowsStacktraceRule
 *
 * @author 'Hamlet D'Arcy'
  */
class LoggingSwallowsStacktraceRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'LoggingSwallowsStacktrace'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	class MyClass {
                private static final Log LOG = LogFactory.getLog( Main.class )

                def method() {
                    try {
                        LOG.error(foo)
                    } catch (Exception e) {
                        if (true) {
                            LOG.error('error') // ok... surrounded by if
                        }
                        LOG.error(e.getMessage(), e) // ok, logs exception
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
        	class MyClass {
                private static final Log LOG = LogFactory.getLog( Main.class )
                private static final Log logger = LogFactory.getLog( Main.class )

                def method() {
                    try {
                        LOG.error(foo)
                    } catch (Exception e) {
                        if (true) {
                            LOG.error('error') // ok... surrounded by if
                        }
                        LOG.error(e) // violation
                        logger.error(e) // violation
                        foo(e)
                    }
                }
            }
        '''
        assertTwoViolations(SOURCE,
                13, 'LOG.error(e)', 'The error logging may hide the stacktrace from the exception named e',
                14, 'logger.error(e)', 'The error logging may hide the stacktrace from the exception named e')
    }
    
    @Test
    void testReportedDefect() {
        final SOURCE = '''
            class MyClass {

            private log = Logger.getLogger(Class)
            private logger = Logger.getLogger(MyClass)

            }'''

        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new LoggingSwallowsStacktraceRule()
    }
}

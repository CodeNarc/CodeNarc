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

/**
 * Tests for LoggingSwallowsStacktraceRule
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class LoggingSwallowsStacktraceRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "LoggingSwallowsStacktrace"
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	class MyClass {
                private static final Log LOG = LogFactory.getLog( Main.class );

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

    void testSingleViolation() {
        final SOURCE = '''
        	class MyClass {
                private static final Log LOG = LogFactory.getLog( Main.class );

                def method() {
                    try {
                        LOG.error(foo)
                    } catch (Exception e) {
                        if (true) {
                            LOG.error('error') // ok... surrounded by if
                        }
                        LOG.error(e) // violation
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 12, 'LOG.error(e)', 'The error logging may hide the stacktrace from the exception named e')
    }

    protected Rule createRule() {
        new LoggingSwallowsStacktraceRule()
    }
}
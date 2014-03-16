/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.rule.exceptions

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for ExceptionNotThrownRule
 *
 * @author Chris Mair
 */
class ExceptionNotThrownRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExceptionNotThrown'
    }

    @Test
    void testThrowsException_NoViolation() {
        final SOURCE = '''
            try { } catch(Exception e) { throw new Exception(e) }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCatchWithNoException_NoViolation() {
        final SOURCE = '''
            try {
            } catch(Exception e) {
                log.error("Error doing stuff", e)
            } catch(Throwabl e) {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoCatch_NoViolation() {
        final SOURCE = '''
            try {
            } finally {
                cleanUp()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testConstructsExceptionWithinCatch_Violations() {
        final SOURCE = '''
            class MyClass {
                void execute() {
                    try { } catch(Exception e) { new Exception(e) }
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:4,
                sourceLineText:'try { } catch(Exception e) { new Exception(e) }',
                messageText:'The catch statement within class MyClass constructs a [Exception] but does not throw it'])
    }

    @Test
    void testConstructsOtherExceptions_Violations() {
        final SOURCE = '''
            try {
                doStuff()
            } catch(DaoException e) {
                log.warning("Ooops", e)
                new ServiceException(e)
            } catch(Exception e) {
                new SystemException(e)
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:6,
                sourceLineText:'new ServiceException(e)',
                messageText:'The catch statement within class None constructs a [ServiceException] but does not throw it'],
            [lineNumber:8,
                sourceLineText:'new SystemException(e)',
                messageText:'The catch statement within class None constructs a [SystemException] but does not throw it'])
    }

    protected Rule createRule() {
        new ExceptionNotThrownRule()
    }
}

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
package org.codenarc.rule.exceptions

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for MissingNewInThrowStatementRule
 *
 * @author Hamlet D'Arcy
  */
class MissingNewInThrowStatementRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'MissingNewInThrowStatement'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            throw runtimeFailure()      
            throw RuntimeObject()
            throw new RuntimeException()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testException() {
        final SOURCE = '''
            throw RuntimeException()    // ends in Exceptions, first letter Capitalized
        '''
        assertSingleViolation(SOURCE, 2, 'throw RuntimeException()', 'The throw statement appears to be throwing the class literal RuntimeException instead of a new instance')
    }

    @Test
    void testClassLiteral() {
        final SOURCE = '''
            throw RuntimeException    // class literal never allowed
        '''
        assertSingleViolation(SOURCE, 2, 'throw RuntimeException', 'The throw statement appears to be throwing the class literal RuntimeException instead of a new instance')
    }

    @Test
    void testFailure() {
        final SOURCE = '''
            throw RuntimeFailure()      // ends in Failure, first letter Capitalized
        '''
        assertSingleViolation(SOURCE, 2, 'throw RuntimeFailure()', 'The throw statement appears to be throwing the class literal RuntimeFailure instead of a new instance')
    }

    @Test
    void testFault() {
        final SOURCE = '''
            throw RuntimeFault(foo)     // ends in Fault, first letter Capitalized
        '''
        assertSingleViolation(SOURCE, 2, 'throw RuntimeFault(foo)', 'The throw statement appears to be throwing the class literal RuntimeFault instead of a new instance')
    }

    protected Rule createRule() {
        new MissingNewInThrowStatementRule()
    }
}

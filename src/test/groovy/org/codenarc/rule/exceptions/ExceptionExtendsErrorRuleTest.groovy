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
 * Tests for ExceptionExtendsErrorRule
 *
 * @author Hamlet D'Arcy
  */
class ExceptionExtendsErrorRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExceptionExtendsError'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyException extends Exception { }  // OK
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolations() {
        final SOURCE = '''
            class MyError1 extends Error { }  // violation

            class MyError2 extends java.lang.Error { }  // violation
        '''
        assertTwoViolations(SOURCE,
                2, 'class MyError1 extends Error', 'The class MyError1 extends Error, which is meant to be used only as a system exception',
                4, 'class MyError2 extends java.lang.Error', 'The class MyError2 extends Error, which is meant to be used only as a system exception')
    }

    protected Rule createRule() {
        new ExceptionExtendsErrorRule()
    }
}

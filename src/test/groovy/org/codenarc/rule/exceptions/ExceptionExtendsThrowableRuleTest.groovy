/*
 * Copyright 2013 the original author or authors.
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
import org.junit.Test

/**
 * Tests for ExceptionExtendsThrowableRule
 *
 * @author Chris Mair
 */
class ExceptionExtendsThrowableRuleTest extends AbstractRuleTestCase<ExceptionExtendsThrowableRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExceptionExtendsThrowable'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class MyException extends Exception { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolation() {
        final SOURCE = '''
            class MyException extends Throwable { }
        '''
        assertViolations(SOURCE,
            [line:2, source:'class MyException extends Throwable', message:'The class MyException extends Throwable'])
    }

    @Override
    protected ExceptionExtendsThrowableRule createRule() {
        new ExceptionExtendsThrowableRule()
    }
}

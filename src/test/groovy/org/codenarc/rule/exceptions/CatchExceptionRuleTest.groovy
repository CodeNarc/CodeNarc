/*
 * Copyright 2008 the original author or authors.
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
 * Tests for CatchExceptionRule
 *
 * @author Chris Mair
  */
class CatchExceptionRuleTest extends AbstractRuleTestCase<CatchExceptionRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CatchException'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
                try {
                    doSomething()
                }
                catch(Exception t) {
                }
        '''
        assertSingleViolation(SOURCE, 5, 'catch(Exception t) {')
    }

    @Test
    void testApplyTo_Violation_FullPackageName() {
        final SOURCE = 'try {  } catch(java.lang.Exception t) { }'
        assertSingleViolation(SOURCE, 1, 'catch(java.lang.Exception t) {')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''
                def myMethod() {
                    try {
                    } catch(Throwable t) { }
                }
            '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected CatchExceptionRule createRule() {
        new CatchExceptionRule()
    }

}

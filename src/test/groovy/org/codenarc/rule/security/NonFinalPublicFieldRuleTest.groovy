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
package org.codenarc.rule.security

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for NonFinalPublicFieldRule
 *
 * @author 'Hamlet D'Arcy'
  */
class NonFinalPublicFieldRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'NonFinalPublicField'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	class MyClass {
                final String myField
                public final String myConstant = ''
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
        	class MyClass {
                public String myField
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public String myField', 'The field myField is public but not final, which violates secure coding principles')
    }

    protected Rule createRule() {
        new NonFinalPublicFieldRule()
    }
}

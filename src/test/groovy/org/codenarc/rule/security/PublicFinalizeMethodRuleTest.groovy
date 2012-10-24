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
 * Tests for PublicFinalizeMethodRule
 *
 * @author Hamlet D'Arcy
  */
class PublicFinalizeMethodRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'PublicFinalizeMethod'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	class MyClass1 {
                protected finalize() {}

                public finalize(String arg) {
                    // overloading finalize is a bad practice, but OK for this rule
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
        	class MyClass {
                public finalize() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public finalize()', 'The finalize() method should only be declared with protected visibility')
    }

    @Test
    void testPrivateDeclaration() {
        final SOURCE = '''
        	class MyClass {
                private finalize() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private finalize()', 'The finalize() method should only be declared with protected visibility')
    }

    protected Rule createRule() {
        new PublicFinalizeMethodRule()
    }
}

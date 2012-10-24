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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for EmptyInstanceInitializerRule
 *
 * @author Hamlet D'Arcy
 */
class EmptyInstanceInitializerRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EmptyInstanceInitializer'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                def x = { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class MyClass {
                { }
            }
        '''
        assertSingleViolation(SOURCE, 3, '{ }', 'The class MyClass defines an empty instance initializer. It is safe to delete it')
    }

    protected Rule createRule() {
        new EmptyInstanceInitializerRule()
    }
}

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
 * Tests for EmptyMethodRule
 *
 * @author Hamlet D'Arcy
 */
class EmptyMethodRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EmptyMethod'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                @Override
                public void method1() {}

                public void method2() {
                    4 + 5
                }
            }
            abstract class MyBaseClass {
                // OK, handled by EmptyMethodInAbstractClass Rule
                public void method() {}
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDeclaredMethod() {
        final SOURCE = '''
            class MyClass {
                public void method() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'method()', 'The method method is both empty and not marked with @Override')
    }

    @Test
    void testDeffedMethod() {
        final SOURCE = '''
            class MyClass {
                def method() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'method()', 'The method method is both empty and not marked with @Override')
    }

    protected Rule createRule() {
        new EmptyMethodRule()
    }
}

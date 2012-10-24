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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryTransientModifierRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryTransientModifierRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryTransientModifier'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MySerializableClass implements Serializable {
                // OK, class is serializable
                transient String property
            }

            class MyClass {
                class InnerClass implements Serializable {
                    // class not serializable, violation occurs
                    transient String property
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTransientPropertyInClassThatIsNotSerializable() {
        final SOURCE = '''
                class MyClass {
                    // class not serializable, violation occurs
                    transient String property
                }
        '''
        assertSingleViolation(SOURCE, 4, 'transient String property',
            'Violation in class MyClass. The field \'property\' is marked transient, but MyClass does not implement Serializable')
    }

    @Test
    void testTransientPropertyInInnerClassThatIsNotSerializable() {
        final SOURCE = '''
                class MyClass {
                    class InnerClass {
                        // class not serializable, violation occurs
                        transient String property
                    }
                }
        '''
        assertSingleViolation(SOURCE, 5, 'transient String property',
            'Violation in class MyClass$InnerClass. The field \'property\' is marked transient, but MyClass$InnerClass does not implement Serializable')
    }

    protected Rule createRule() {
        new UnnecessaryTransientModifierRule()
    }
}

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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryConstructorRule
 *
 * @author 'Tomasz Bujok'
 * @author Chris Mair
  */
class UnnecessaryConstructorRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryConstructor'
    }

    @Test
    void testConstructors_NoViolations() {
        final SOURCE = '''
            class MyClass {
                public MyClass() {}
                public MyClass(String text) {}

                class InnerClass {
                }
            }
            class MyUtility {
              private MyUtility(){
                def inner = new Object() {}
              }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class MyClass {
                public MyClass() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public MyClass() {}')
    }

    @Test
    void testConstructor_CallsOnlySuper_Violation() {
        final SOURCE = '''
            class MyClass extends OtherClass {
                MyClass() {
                    super()
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'MyClass() {')
    }

    @Test
    void testConstructor_CallsSuperAndDoesOtherStuff_NoViolation() {
        final SOURCE = '''
            class MyClass extends OtherClass {
                MyClass() {
                    super()
                    doSomethingElse()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testConstructor_CallsThis_NoViolation() {
        final SOURCE = '''
            class MyClass extends OtherClass {
                MyClass() {
                    this('abc')
                }
                private MyClass(String name) {
                    println name
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testConstructor_NotEmpty_NoViolation() {
        final SOURCE = '''
            class MyClass {
                MyClass() {
                    println 123
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInnerClass() {
        final SOURCE = '''
            class MyClass {

                static class MyInnerClass {
                    public MyInnerClass() {}
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'public MyInnerClass() {}', 'The constructor can be safely deleted')
    }

    protected Rule createRule() {
        new UnnecessaryConstructorRule()
    }
}

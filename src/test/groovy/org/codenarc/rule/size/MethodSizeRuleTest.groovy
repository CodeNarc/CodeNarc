/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.size

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for MethodSizeRule
 *
 * @author Chris Mair
  */
class MethodSizeRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'MethodSize'
    }

    @Test
    void testApplyTo_LongerThanDefaultMaxLines() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    ${'println 23\n' * 98}
                }
            }
        """
        assertSingleViolation(SOURCE, 3, null, 'myMethod')
    }

    @Test
    void testApplyTo_SetMaxLines() {
        final SOURCE = """
            /** class description */
            class MyClass {
                def myMethod() {
                    'println 23'
                }
            }
        """
        rule.maxLines = 2
        assertSingleViolation(SOURCE, 4, null, 'myMethod')
    }

    @Test
    void testApplyTo_ConstructorExceedsMaxLines() {
        final SOURCE = """
            class MyClass {
                MyClass() {
                    'println 23'
                }
            }
        """
        rule.maxLines = 2
        assertSingleViolation(SOURCE, 3, null, CONSTRUCTOR_METHOD_NAME)
    }

    @Test
    void testApplyTo_NoMethodDefinition() {
        final SOURCE = '''
            class MyClass {
                int count
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_EqualToMaxLines() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    ${'println 23\n' * 97}
                }
            }"""
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_CountsAnnotationsInNumberOfLines() {
        final SOURCE = '''
            class MyClass {
                @Override
                def myMethod() {
                    println 123
                }
            }'''
        rule.maxLines = 3
        assertSingleViolation(SOURCE, 3, null, 'myMethod')
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesSingleName() {
        final SOURCE = '''
          class MyClass {
               def myMethod() {
                   'println 23'
               }
          }
        '''
        rule.maxLines = 2
        rule.ignoreMethodNames = 'myMethod'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesNoNames() {
        final SOURCE = '''
          class MyClass {
               def myMethod1() { 'println 23' }
               String myMethod2() {
                   'error: 23'
               }
          }
        '''
        rule.maxLines = 2
        rule.ignoreMethodNames = 'otherMethod'
        assertSingleViolation(SOURCE, 4, null, 'myMethod2')
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MultipleNamesWithWildcards() {
        final SOURCE = '''
            class MyClass {
                def names
                private open(String destination) {
                }
                String initialize() {
                    names = ['a', 'b']
                }
                void process() {
                }
                void doOtherStuff() {
                }
            }
        '''
        rule.maxLines = 1
        rule.ignoreMethodNames = 'init*ze,doO??erSt*ff,other'
        assertTwoViolations(SOURCE,
            4, null, 'open',
            9, null, 'process')
    }

    protected Rule createRule() {
        new MethodSizeRule()
    }

}

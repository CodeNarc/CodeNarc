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
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for MethodNameRule
 *
 * @author Chris Mair
  */
class MethodNameRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'MethodName'
    }

    @Test
    void testRegexIsNull() {
        rule.regex = null
        shouldFailWithMessageContaining('regex') { applyRuleTo('def myMethod() { }') }
    }

    @Test
    void testApplyTo_DoesNotMatchDefaultRegex() {
        final SOURCE = '''
          class MyClass {
            def MyMethod() { println 'bad' }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'def MyMethod', 'The method name MyMethod in class MyClass does not match [a-z]\\w*')
    }

    @Test
    void testApplyTo_DoesMatchDefaultRegex() {
        final SOURCE = '''
            class MyClass {
              def myMethod_Underscores() { println 'bad' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreConstructors() {
        final SOURCE = '''
            class MyClass {
                MyClass() { }
                def myMethod_Underscores() { println 'bad' }
                public MyClass() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_DoesNotMatchCustomRegex() {
        final SOURCE = '''
            class MyClass {
              def myMethod() { println 'bad' }
            }
        '''
        rule.regex = /z.*/
        assertSingleViolation(SOURCE, 3, 'def myMethod()')
    }

    @Test
    void testApplyTo_DoesMatchCustomRegex() {
        final SOURCE = '''
            class MyClass {
              def zMethod() { println 'bad' }
            }
        '''
        rule.regex = /z.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_DoesNotMatchCustomRegex_NoClassDefined() {
        final SOURCE = ' def myMethod() { println "bad" }'
        rule.regex = /z.*/
        assertSingleViolation(SOURCE, 1, 'def myMethod()')
    }

    @Test
    void testApplyTo_DoesMatchCustomRegex_NoClassDefined() {
        final SOURCE = ' def zMethod() { println "bad" } '
        rule.regex = /z.*/
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesSingleName() {
        final SOURCE = '''
          class MyClass {
            def MyMethod() { println 'bad' }
          }
        '''
        rule.ignoreMethodNames = 'MyMethod'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesNoNames() {
        final SOURCE = '''
          class MyClass {
            def MyMethod() { println 'bad' }
          }
        '''
        rule.ignoreMethodNames = 'OtherMethod'
        assertSingleViolation(SOURCE, 3, 'def MyMethod')
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MultipleNamesWithWildcards() {
        final SOURCE = '''
          class MyClass {
            def MyMethod() { println 'bad' }
            String GOOD_NAME() { }
            def _amount() { }
            def OTHER_name() { }
          }
        '''
        rule.ignoreMethodNames = 'OTHER?name,_*,GOOD_NAME' 
        assertSingleViolation(SOURCE, 3, 'def MyMethod')
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
    void testApplyTo_ClosureDefinition() {
        final SOURCE = '''
            class MyClass {
                def MY_CLOSURE = { println 'ok' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new MethodNameRule()
    }

}

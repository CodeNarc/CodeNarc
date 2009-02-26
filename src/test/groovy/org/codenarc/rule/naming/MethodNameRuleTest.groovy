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

import org.codenarc.rule.AbstractRuleTest
import org.codenarc.rule.Rule

/**
 * Tests for MethodNameRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class MethodNameRuleTest extends AbstractRuleTest {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'MethodName'
    }

    void testRegexIsNull() {
        rule.regex = null
        shouldFailWithMessageContaining('regex') { applyRuleTo('def myMethod() { }') }
    }

    void testApplyTo_DoesNotMatchDefaultRegex() {
        final SOURCE = '''
          class MyClass {
            def MyMethod() { println 'bad' }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'def MyMethod')
    }

    void testApplyTo_DoesMatchDefaultRegex() {
        final SOURCE = '''
            class MyClass {
              def myMethod_Underscores() { println 'bad' }
            }
        '''
        assertNoViolations(SOURCE)
    }

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

    void testApplyTo_DoesNotMatchCustomRegex() {
        final SOURCE = '''
            class MyClass {
              def myMethod() { println 'bad' }
            }
        '''
        rule.regex = /z.*/
        assertSingleViolation(SOURCE, 3, 'def myMethod()')
    }

    void testApplyTo_DoesMatchCustomRegex() {
        final SOURCE = '''
            class MyClass {
              def zMethod() { println 'bad' }
            }
        '''
        rule.regex = /z.*/
        assertNoViolations(SOURCE)
    }

    void testApplyTo_DoesNotMatchCustomRegex_NoClassDefined() {
        final SOURCE = ' def myMethod() { println "bad" }'
        rule.regex = /z.*/
        assertSingleViolation(SOURCE, 1, 'def myMethod()')
    }

    void testApplyTo_DoesMatchCustomRegex_NoClassDefined() {
        final SOURCE = ' def zMethod() { println "bad" } '
        rule.regex = /z.*/
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NoMethodDefinition() {
        final SOURCE = '''
            class MyClass {
              int count
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_ClosureDefinition() {
        final SOURCE = '''
            class MyClass {
                def MY_CLOSURE = { println 'ok' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new MethodNameRule()
    }

}
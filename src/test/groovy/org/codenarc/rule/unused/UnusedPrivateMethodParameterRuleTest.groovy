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
package org.codenarc.rule.unused

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for UnusedPrivateMethodParameterRule
 *
 * @author Chris Mair
  */
class UnusedPrivateMethodParameterRuleTest extends AbstractRuleTestCase<UnusedPrivateMethodParameterRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnusedPrivateMethodParameter'
    }

    @Test
    void testApplyTo_SingleUnusedPrivateMethodParameter() {
        final SOURCE = '''
            class MyClass {
                private void myMethod(int value) { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private void myMethod(int value) { }', 'Method parameter [value] is never referenced')
    }

    @Test
    void testIgnoreRegexDefaults() {
        final SOURCE = '''
            class MyClass {
                private void myMethod1(int ignore) { }
                private void myMethod2(int ignored) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCustomIgnoreRegex() {
        final SOURCE = '''
            class MyClass {
                private void myMethod1(int value) { }
                private void myMethod2(int ignore) { }
                private void myMethod3(int ignored) { }
            }
        '''
        rule.ignoreRegex = 'value|ignored|ignore'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SingleUnusedPrivateMethodParameterSuspiciousReferenceInAnonymousClass() {
        final SOURCE = '''
            class MyClass {
                private void myMethod(int value) { }

                // this is NOT a reference, but the AST does not have enough information for this
                def x = new Object() { def y = value  }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MultipleUnusedParametersForSinglePrivateMethod() {
        final SOURCE = '''
          class MyClass {
              private void myMethod(int value, String name) { }
              private void myMethod2(int ignore) { }
              private void myMethod3(int ignored) { }
          }
        '''
        assertViolations(SOURCE,
            [line:3, source:'private void myMethod(int value, String name) { }', message:'value'],
            [line:3, source:'private void myMethod(int value, String name) { }', message:'name'])
    }

    @Test
    void testApplyTo_MultiplePrivateMethodsWithUnusedParameters() {
        final SOURCE = '''
          class MyClass {
              private void myMethod1(String id, int value) { print value }
              protected void myMethod2(int otherValue) { print otherValue }
              private int myMethod3(Date startDate) { }
          }
        '''
        assertViolations(SOURCE,
            [line:3, source:'private void myMethod1(String id, int value) { print value }', message:'id'],
            [line:5, source:'private int myMethod3(Date startDate) { }', message:'startDate'])
    }

    @Test
    void testApplyTo_MultiplePrivateConstructorMethodsWithUnusedParameters() {
        final SOURCE = '''
          class MyClass {
              private MyClass(String id, int value) { print value }
              private MyClass(int otherValue) { print otherValue }
              private MyClass(Date startDate, Date endDate) { }
          }
        '''
        assertViolations(SOURCE,
                [line:3, source:'private MyClass(String id, int value) { print value }', message:'id'],
                [line:5, source:'private MyClass(Date startDate, Date endDate) { }', message:'startDate'],
                [line:5, source:'private MyClass(Date startDate, Date endDate) { }', message:'endDate'])
    }

    @Test
   void testApplyTo_AllParametersUsed() {
        final SOURCE = '''
            class MyClass {
                private String myMethod1(String id, int value) { doSomething(value); return id }
                private void myMethod2(int value) { def x = value }
                private def myMethod3(Date startDate) { return "${startDate}" }
                private def myMethod4(Date startDate) {
                    return new Object() {
                        def x = startDate
                    }
                }
                private def myMethod5(Date startDate) {
                    return new Object() {
                        String toString() { return startDate }
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonPrivateMethods() {
        final SOURCE = '''
            class MyClass {
                void myMethod1(String id, int value) { }
                protected void myMethod2(int value) { }
                public int myMethod3(Date startDate) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_OnlyReferenceIsAMapKeyOrValue() {
        final SOURCE = '''
            class MyClass {
                private myMethod1(String id, int value) {
                    return [(id):value]
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ParameterIsAClosureThatIsCalled() {
        final SOURCE = '''
            class MyClass {
                private myMethod1(Closure closure, def closure2, closure3) {
                    def value1 = closure()
                    def value2 = closure2.call()
                    return closure3(value1, value2)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoMethods() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    @Override
    protected UnusedPrivateMethodParameterRule createRule() {
        new UnusedPrivateMethodParameterRule()
    }

}

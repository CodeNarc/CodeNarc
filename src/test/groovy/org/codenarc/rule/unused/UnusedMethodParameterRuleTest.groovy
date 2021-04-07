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
package org.codenarc.rule.unused

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for UnusedMethodParameterRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class UnusedMethodParameterRuleTest extends AbstractRuleTestCase<UnusedMethodParameterRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnusedMethodParameter'
    }

    @Test
    void testApplyTo_SingleUnusedMethodParameter() {
        final SOURCE = '''
            class MyClass {
                void myMethod(int value) { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void myMethod(int value) { }', 'Method parameter [value] is never referenced')
    }

    @Test
    void testIgnoreRegexDefaults() {
        final SOURCE = '''
            class MyClass {
                void myMethod1(int ignore) { }
                void myMethod2(int ignored) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCustomIgnoreRegex() {
        final SOURCE = '''
            class MyClass {
                void myMethod1(int value) { }
                void myMethod2(int ignore) { }
                void myMethod3(int ignored) { }
            }
        '''
        rule.ignoreRegex = 'value|ignored|ignore'
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoresCategoryClassesByDefault() {
        final SOURCE = '''
            class MyCategory {
                void myMethod1(String string, int value) { }
                void myMethod1(String string, int value, name) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCustomIgnoreClassRegex() {
        final SOURCE = '''
            class MyCustomClass {
                void myMethod1(int value) { }
                void myMethod1(int value, name) { }
            }
        '''
        rule.ignoreClassRegex = '.*Custom.*'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SingleUnusedPrivateMethodParameterSuspiciousReferenceInAnonymousClass() {
        final SOURCE = '''
            class MyClass {
                void myMethod(int value) { }

                // this is NOT a reference, but the AST does not have enough information for this
                def x = new Object() { def y = value  }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MultipleUnusedParametersForSingleMethod() {
        final SOURCE = '''
          class MyClass {
              void myMethod(int value, String name) { }
              void myMethod2(int ignore) { }
              void myMethod3(int ignored) { }
          }
        '''
        assertViolations(SOURCE,
            [line:3, source:'void myMethod(int value, String name) { }', message:'value'],
            [line:3, source:'void myMethod(int value, String name) { }', message:'name'])
    }

    @Test
    void testApplyTo_MultipleMethodsWithUnusedParameters() {
        final SOURCE = '''
          class MyClass {
              void myMethod1(String id, int value) { print value }
              protected void myMethod2(int otherValue) { print otherValue }
              int myMethod3(Date startDate) { }
          }
        '''
        assertViolations(SOURCE,
            [line:3, source:'void myMethod1(String id, int value) { print value }', message:'id'],
            [line:5, source:'int myMethod3(Date startDate) { }', message:'startDate'])
    }

    @Test
    void testApplyTo_MultipleConstructorMethodsWithUnusedParameters() {
        final SOURCE = '''
          class MyClass {
              MyClass(String id, int value) { print value }
              protected MyClass(int otherValue) { print otherValue }
              protected MyClass(Date startDate, Date endDate) { }
          }
        '''
        assertViolations(SOURCE,
            [line:3, source:'MyClass(String id, int value) { print value }', message:'id'],
            [line:5, source:'protected MyClass(Date startDate, Date endDate) { }', message:'startDate'],
            [line:5, source:'protected MyClass(Date startDate, Date endDate) { }', message:'endDate'])
    }

    @Test
   void testApplyTo_AllParametersUsed() {
        final SOURCE = '''
            class MyClass {
                MyClass() { }
                MyClass(String id) { println id }

                String myMethod1(String id, int value) { doSomething(value); return id }
                void myMethod2(int value) { def x = value }
                def myMethod3(Date startDate) { return "${startDate}" }
                def myMethod4(Date startDate) {
                    return new Object() {
                        def x = startDate
                    }
                }
                def myMethod5(Date startDate) {
                    return new Object() {
                        String toString() { return startDate }
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonPrivateMethodsWithOverride() {
        final SOURCE = '''
            class MyClass {
                @Override
                void myMethod1(String id, int value) { }

                @Override protected void myMethod2(int value) { }

                @Override public int myMethod3(Date startDate) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testPointcutAnnotation() {
        final SOURCE = '''
            class MyClass {
                @Pointcut("@annotation(example.annotation.Test)")
                void myMethod1(String name) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_OnlyReferenceIsAMapKeyOrValue() {
        final SOURCE = '''
            class MyClass {
                def myMethod1(String id, int value) {
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
                def myMethod1(Closure closure, def closure2, closure3) {
                    def value1 = closure()
                    def value2 = closure2.call()
                    return closure3(value1, value2)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMainMethod() {
        // In Groovy, the main() method does not have to specify void return type or String[] args type.
        // But it must be static, with one parameter.
        final SOURCE = '''
            class MyClass1 {
                public static void main(String[] args) { }
            }
            class MyClass2 {
                static main(args) { }
            }
            class MyClass3 {
                public static void main(java.lang.String[] args) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreNonMatchingMainMethods() {
        final SOURCE = '''
            class MyClass1 {
                void main(String[] args) { }    // not static
            }
            class MyClass2 {
                static main(arg1, arg2) { }     // too many args
            }
            class MyClass3 {
                static main(int value) { }     // wrong arg type
            }
            class MyClass4 {
                static int main(String[] args) { }     // wrong return type
            }
        '''
        assertViolations(SOURCE,
            [line:3, source:'void main(String[] args) { }', message:'args'],
            [line:6, source:'static main(arg1, arg2) { }', message:'arg1'],
            [line:6, source:'static main(arg1, arg2) { }', message:'arg2'],
            [line:9, source:'static main(int value) { }', message:'value'],
            [line:12, source:'static int main(String[] args) { }', message:'args'])
    }

    @Test
    void testApplyTo_NoMethods() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    @Override
    protected UnusedMethodParameterRule createRule() {
        new UnusedMethodParameterRule()
    }
}

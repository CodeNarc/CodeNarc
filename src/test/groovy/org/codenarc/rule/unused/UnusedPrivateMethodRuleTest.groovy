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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnusedPrivateMethodRule
 *
 * @author Chris Mair
  */
class UnusedPrivateMethodRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnusedPrivateMethod'
    }

    @Test
    void testLocalUseOfGetter() {
        final SOURCE = '''
            class ExecuteTest {
                private String getCmd() { 'cmd' }
                private String getCmd2() { 'cmd' }
                private String getCMD() { 'cmd' }
                private String getCMD2() { 'cmd' }
                private void setCMD3(x) {  }
                private boolean isCmd4() { false }
                private boolean isCmd5() { true }

                private String getUnused() { 'unused' }

                void myMethod() {
                    cmd.execute(cmd2)
                    new Foo().CMD
                    foo.CMD2.bar = 'xxx'
                    CMD3 = 'yyy'
                    cmd4.class
                    cmd5.toString()
                }
            }
            '''
        assertSingleViolation(SOURCE, 11, 'getUnused()', 'The method getUnused is not used within the class')
    }

    @Test
    void testGetterMethodWithIsPrefix_AccessedAsProperty_NoViolation() {
        final SOURCE = '''
            class A {
                private boolean isCompleted() {
                    true
                }
                boolean ready() {
                    completed
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticMethodsInOuterClass() {
        final SOURCE = '''
            package groovy.bugs

            class MyClass {
                void myMethod() {
                    MyClass.myPrivateMethod()
                    MyOuterClass.myInnerPrivateMethod()
                }
                private static void myPrivateMethod() {
                }
                private static void myUncalledPrivateMethod() {
                }
            }

            class MyOuterClass {
                private static myInnerPrivateMethod() {
                }
                private static myUnCalledPrivateMethod() {
                }
            }'''
        assertTwoViolations(SOURCE,
                11, 'myUncalledPrivateMethod', 'The method myUncalledPrivateMethod is not used within the class',
                18, 'myUnCalledPrivateMethod', 'The method myUnCalledPrivateMethod is not used within the class')
    }

    @Test
    void testStaticMethodsInOuterClass_MethodHandles() {
        final SOURCE = '''
            class MyClass {
                void myMethod() {
                    def x = MyClass.&myPrivateMethod
                    def y = MyOuterClass.&myInnerPrivateMethod
                }
                private static void myPrivateMethod() {
                }
                private static void myUncalledPrivateMethod() {
                }
            }

            class MyOuterClass {
                private static myInnerPrivateMethod() {
                }
                private static myUnCalledPrivateMethod() {
                }
            }'''
        assertTwoViolations(SOURCE,
                9, 'myUncalledPrivateMethod', 'The method myUncalledPrivateMethod is not used within the class',
                16, 'myUnCalledPrivateMethod', 'The method myUnCalledPrivateMethod is not used within the class')
    }

    @Test
    void testStaticMethodsInInnerClass_MethodHandles() {
        final SOURCE = '''
            package groovy.bugs

            class MyClass {
                void myMethod() {
                    def x = MyInnerClass.&myInnerPrivateMethod1
                    def y = MyInnerClass.myInnerPrivateMethod2()
                }

                class MyInnerClass {
                    private static myInnerPrivateMethod1() { }
                    private static myInnerPrivateMethod2() { }
                    private static myInnerPrivateMethod3() { }
                    private static myInnerPrivateMethod4() { }
                }
            } '''
        assertTwoViolations(SOURCE,
                13, 'myInnerPrivateMethod3', 'The method myInnerPrivateMethod3 is not used within the class',
                14, 'myInnerPrivateMethod4', 'The method myInnerPrivateMethod4 is not used within the class')
    }

    @Test
    void testGetterMethodsInOuterClass() {
        final SOURCE = '''
                package groovy.bugs

                class MyClass {
    @Test
                  void test () {
                     Foo.test()
                  }
                }

                class Foo {
                    private static test() { }
                }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testGetterMethodsInInnerClass() {
        final SOURCE = '''
                package groovy.bugs

                class MyClass {
    @Test
                    void test () {
                        Foo.test()
                    }
                    class Foo {
                        private static test() { }
                    }
                }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testAnonymousInnerClassAsField() {
        final SOURCE = '''
             class MyClass {
                 private static def methodWithParameters(a,b) { a >> b }

                 def x = new Class() {
                     def call() {
                         methodWithParameters(10,1)
                     }
                 }
             } '''
        assertNoViolations SOURCE
    }

    @Test
    void testClosureAsField() {
        final SOURCE = '''
            class MyClass {
                private static def methodWithParameters(a,b) { a >> b }

                def x = {
                    methodWithParameters(10,1)
                }
            } '''
        assertNoViolations SOURCE
    }

    @Test
    void testAnonymousInnerClassAsLocalVariable() {
        final SOURCE = '''
            class MyClass {
                private static def methodWithParameters(a,b) { a >> b }

                def myMethod() {
                    return new Class() {
                        def call() {
                            methodWithParameters(10,1)
                        }
                    }
                }
            } '''
        assertNoViolations SOURCE
    }

    @Test
    void testClosureAsLocalVariable() {
        final SOURCE = '''
            class MyClass {
                private static def methodWithParameters(a,b) { a >> b }

                def myClosure = {
                    methodWithParameters(10,1)
                }
            } '''
        assertNoViolations SOURCE
    }

    @Test
    void testInnerClass() {
        final SOURCE = '''
            class MyClass {
                private static def methodWithParameters(a,b) { a >> b }

                class MyInnerClass {
                    def call() {
                        methodWithParameters(10,1)
                    }
                }
            } '''
        assertNoViolations SOURCE
    }

    @Test
    void testApplyTo_SingleUnusedPrivateMethod() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'private int countStuff() { }', 'The method countStuff is not used within the class')
    }

    @Test
    void testApplyTo_MultipleUnusedPrivateMethods() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { }
                def otherMethod() {
                }
                private static String buildName(int count) {
                    return "abc" + count
                }
          }
        '''
        assertTwoViolations(SOURCE, 3, 'private int countStuff() { }', 6, 'private static String buildName(int count) {')
    }

    @Test
    void testApplyTo_PrivateConstructor_NotUsed() {
        final SOURCE = '''
            class MyClass {
                private MyClass() {
                }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AllPrivateMethodsUsed() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { return 99 }
                int somePublicMethod() { }
                def abc = 'abc'
                private String getName() { 'abc' }
                private getPrice() { 0.0 }

                def doStuff() {
                    def count = countStuff()
                    def newName = this.getName()
                }

                def myClosure = { println "price is ${getPrice()}" }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonPrivateMethods() {
        final SOURCE = '''
            class MyClass {
                int countStuff() { }
                protected String getName() { }
                def myOtherMethod() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MultipleOverloadedMethods() {
        final SOURCE = '''
            class MyClass {
                private int fireEvent(int index) { }
                private int fireEvent(String name) { }
                private int fireEvent(int index, String name) { }
                def other = this.fireEvent(object)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ReferenceMethodOfAnotherObject() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { }
                def doSomething() {
                    someOtherObject.countStuff()
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int countStuff() { }')
    }

    @Test
    void testApplyTo_UnusedPrivateStaticMethod() {
        final SOURCE = '''
            class MyClass {
                private static int countStuff() { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private static int countStuff() { }')
    }

    @Test
    void testApplyTo_PrivateStaticMethodAccessedThroughClassName() {
        final SOURCE = '''
            class MyClass {
                static int getTotal() {
                    println "total=${MyClass.countStuff()}"
                }
                private static int countStuff() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_GStringMethodReference() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { }
                def other = this."${countStuff}"()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int countStuff() { }')
    }

    @Test
    void testApplyTo_StringMethodReference() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { }
                def other = this."countStuff"()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_DereferencedGStringMethodReference() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { }
                def varName = "countStuff"
                def other = this."${varName}"()     // can't see this
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int countStuff() { }')
    }

    @Test
    void testApplyTo_MethodPointerReference() {
        final SOURCE = '''
            class MyClass {
                private int myMethod() { }
                private int otherMethod() { }
                def getValue() { return this.&myMethod }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'private int otherMethod()')
    }

    @Test
    void testApplyTo_MoreThanOneClassInASourceFile() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { }
            }
            class OtherClass {
                int defaultCount = count
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int countStuff() { }')
    }

    @Test
    void testApplyTo_Script() {
        final SOURCE = '''
            private BigDecimal calculateDepositAmount() { 23 }
        '''
        assertSingleViolation(SOURCE, 2, 'private BigDecimal calculateDepositAmount() { 23 }')
    }

    @Test
    void testApplyTo_NoMethodDefinition() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_StackOverflow() {
        final SOURCE = ' Map map = ["a" : 1, "b": 2, "$c": 3, "b": 4 ] '
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ZeroLengthMethodName() {
        final SOURCE = '''
            def doStuff() {
                def results = calculate()
                results[0].""
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnusedPrivateMethodRule()
    }

}

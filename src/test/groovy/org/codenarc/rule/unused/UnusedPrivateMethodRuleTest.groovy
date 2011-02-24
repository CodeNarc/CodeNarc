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

/**
 * Tests for UnusedPrivateMethodRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnusedPrivateMethodRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnusedPrivateMethod'
    }

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

    void testApplyTo_SingleUnusedPrivateMethod() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'private int countStuff() { }', 'The method countStuff is not used within the class')
    }

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

    void testApplyTo_PrivateConstructor_NotUsed() {
        final SOURCE = '''
            class MyClass {
                private MyClass() {
                }
          }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_PrivateMethod_AccessedAsProperty() {
        final SOURCE = '''
            class MyClass {
                private String getName() { 'abc' }
                def doStuff() {
                    def newName = this.name     // known limitation: access getName() method
                }
          }
        '''
        assertSingleViolation(SOURCE, 3, "private String getName() { 'abc' }")
    }

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

    void testApplyTo_UnusedPrivateStaticMethod() {
        final SOURCE = '''
            class MyClass {
                private static int countStuff() { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private static int countStuff() { }')
    }

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

    void testApplyTo_GStringMethodReference() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { }
                def other = this."${countStuff}"()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'private int countStuff() { }')
    }

    void testApplyTo_StringMethodReference() {
        final SOURCE = '''
            class MyClass {
                private int countStuff() { }
                def other = this."countStuff"()
            }
        '''
        assertNoViolations(SOURCE)
    }

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

    void testApplyTo_Script() {
        final SOURCE = '''
            private BigDecimal calculateDepositAmount() { 23 }
        '''
        assertSingleViolation(SOURCE, 2, 'private BigDecimal calculateDepositAmount() { 23 }')
    }

    void testApplyTo_NoMethodDefinition() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnusedPrivateMethodRule()
    }

}
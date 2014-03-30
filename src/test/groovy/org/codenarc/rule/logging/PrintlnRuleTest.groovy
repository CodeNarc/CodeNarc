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
package org.codenarc.rule.logging

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for PrintlnRule
 *
 * @author Chris Mair
  */
class PrintlnRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'Println'
    }

    @Test
    void testApplyTo_Println_NoArgs() {
        final SOURCE = '''
            println()
        '''
        assertSingleViolation(SOURCE, 2, 'println')
    }

    @Test
    void testApplyTo_Println_NoArgs_WithinClosure() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    if (hasResult) {
                        println "$result"
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'println')
    }

    @Test
    void testApplyTo_Println_OneArg() {
        final SOURCE = '''
            println "message"
        '''
        assertSingleViolation(SOURCE, 2, 'println "message"')
    }

    @Test
    void testApplyTo_Println_ExplicitThis() {
        final SOURCE = '''
            this.println "message"
        '''
        assertSingleViolation(SOURCE, 2, 'this.println "message"')
    }

    @Test
    void testApplyTo_Print_OneArg() {
        final SOURCE = '''
            print("message")
        '''
        assertSingleViolation(SOURCE, 2, 'print("message")')
    }

    @Test
    void testApplyTo_Printf_TwoArgs() {
        final SOURCE = '''
            printf "%d", 99
        '''
        assertSingleViolation(SOURCE, 2, 'printf "%d", 99')
    }

    @Test
    void testApplyTo_Printf_ThreeArgs() {
        final SOURCE = '''
            printf "%d, %d", 23, 34
        '''
        assertSingleViolation(SOURCE, 2, 'printf "%d, %d", 23, 34')
    }

    @Test
    void testApplyTo_Printf_FourArgs() {
        final SOURCE = '''
            printf "%d, %d", 23, 34, 45
        '''
        assertSingleViolation(SOURCE, 2, 'printf "%d, %d", 23, 34, 45')
    }

    @Test
    void testApplyTo_Println_AnnotatedMethod() {
        final SOURCE = '''
            import org.gcontracts.annotations.Requires
            class CodenarcViolationService {
                @Requires({arg})
                def violate(arg) {
                    println 'foo bar'
                }
            }
            '''
        assertSingleViolation(SOURCE, 6, "println 'foo bar'")
    }

    @Test
    void testApplyTo_PrintlnButNotThis() {
        final SOURCE = '''
    @Test
            void testSomething() {
                System.out.println "123"
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PrintlnLocallyDefinedMethod() {
        final SOURCE = '''
            class MyClass1 {
                def println(p) {
                }
                def method() {
                    println('a')
                    println('b')
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PrintlnLocallyDefinedClosure() {
        final SOURCE = '''
            class MyClass1 {
                def println = {
                }
                def method() {
                    println('a')
                    println('b')
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PrintlnLocallyDefinedClosure2() {
        final SOURCE = '''
            class MyClass1 {
                Closure println = makeClosure()

                def method() {
                    println('a')
                    println('b')
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PrintLocallyDefinedClosure() {
        final SOURCE = '''
            class MyClass1 {
                def print = {
                }
                def method() {
                    print('a')
                    print('b')
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PrintLocallyDefinedClosure2() {
        final SOURCE = '''
            class MyClass1 {
                Closure print = makeClosure()

                def method() {
                    print('a')
                    print('b')
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new PrintlnRule()
    }
}

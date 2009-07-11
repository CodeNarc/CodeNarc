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

import org.codenarc.rule.AbstractRuleTest
import org.codenarc.rule.Rule

/**
 * Tests for UnusedVariableRule
 *
 * @author Chris Mair
 * @version $Revision: 178 $ - $Date: 2009-06-29 22:16:13 -0400 (Mon, 29 Jun 2009) $
 */
class UnusedVariableRuleTest extends AbstractRuleTest {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnusedVariable'
    }

    void testApplyTo_SingleUnusedVariable() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    int count = 23
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'int count = 23')
    }

    void testApplyTo_MultipleUnusedVariables() {
        final SOURCE = '''
          class MyClass {
                def myMethod() {
                    int count = 23
                    String name
                    String other
                    name = 'def'
                }
          }
        '''
        assertTwoViolations(SOURCE, 4, 'int count = 23', 6, "String other")
    }

    void testApplyTo_AllVariablesUsed() {
        final SOURCE = '''
            class MyClass {
                static final GLOBAL_NAME = 'xxx'
                def doStuff() {
                    def otherVar
                    def defaultName = GLOBAL_NAME
                    String startName = defaultName
                    otherVar = startName.size()
                    println "name=$startName"
                    def amount = 123.45
                    println "amount=$amount"
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_ReferencePropertyWithSameName() {
        final SOURCE = '''
            class MyClass {
                def doSomething() {
                    int count = 99
                    someOtherObject.count = 23
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'int count = 99')
    }

    void testApplyTo_ReferencedWithinClosure() {
        final SOURCE = '''
            class MyClass {
                def doSomething() {
                    int count
                    return { -> println count }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_ReferencedOnSameLineAsDeclaration() {
        final SOURCE = '''
            class MyClass {
                def doSomething() {
                    int count = 99; println 'abc'; println "count=$count"
                }
            }
        '''
        // Known limitation: Does not recognize variable references on same line as declaration
        assertSingleViolation(SOURCE, 4, 'int count = 99')
    }

    void testApplyTo_Script_UnusedVariable() {
        final SOURCE = '''
            BigDecimal depositAmount
        '''
        assertSingleViolation(SOURCE, 2, 'BigDecimal depositAmount')
    }

    void testApplyTo_Script_UnusedBinding() {
        final SOURCE = '''
            depositCount = 99      // not considered a variable
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NoVariableDefinition() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new UnusedVariableRule()
    }

}
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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnusedVariableRule
 *
 * @author Chris Mair
  */
class UnusedVariableRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnusedVariable'
    }

    @Test
    void testApplyTo_SingleUnusedVariable() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    int count
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'int count', 'The variable [count] in class MyClass is not used')
    }

    @Test
    void testApplyTo_SingleUnusedVariable_WithInitialExpression() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    int count = 23
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'int count = 23', 'count')
    }

    @Test
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
        assertTwoViolations(SOURCE, 4, 'int count = 23', 6, 'String other')
    }

    @Test
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

    @Test
    void testApplyTo_VariableWithSameNameReferencedInAnotherBlock() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    int count = 23
                    def name = 'abc'
                    println name
                }
                def myOtherMethod() {
                    println count
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'int count = 23', 'count')
    }

    @Test
    void testApplyTo_SameVariableInOtherBlocks() {
        final SOURCE = '''
            class MyClass {
                def myMethod1() {
                    int count = 23
                }
                def myOtherMethod() {
                    println count
                }
                def myMethod2() {
                    int count = 99
                }
            }
        '''
        assertTwoViolations(SOURCE, 4, 'int count = 23', 10, 'int count = 99')
    }

    @Test
    void testApplyTo_NestedBlock() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    int count = 23
                    if (ready) {
                        def name = 'abc'
                        println count
                        println name
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ReferencedFromReturn() {
        final SOURCE = '''
            def defaultMethod = dc.metaClass.getMetaMethod(name, args)
            if(x == 99) {
                println "too much"
                if (y > 5){
                     println 'way too much'
                 }
             }
             return defaultMethod.invoke(delegate, args)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ReferencePropertyWithSameName() {
        final SOURCE = '''
            class MyClass {
                def doSomething() {
                    int count = 99
                    someOtherObject.count = 23
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'int count = 99', 'count')
    }

    @Test
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

    @Test
    void testApplyTo_ReferencedWithinGString() {
        final SOURCE = '''
            def doSomething() {
                def value = null
                println "value=$value"
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ReferenceClosureVariableByInvokingIt() {
        final SOURCE = '''
            class MyClass {
                def doSomething() {
                    def myClosure = { println 'ok' }
                    if (ready) {
                        myClosure()
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ReferenceNestedClosureVariablesByInvokingThem() {
        final SOURCE = '''
            class MyClass {
                def doSomething() {
                    def outerClosure = {
                        def innerClosure = { count -> println 'ok' }
                        innerClosure(99)
                    }
                    outerClosure()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClosureVariableReferencedButNotInvoked() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    final CLOSURE = {
                        doSomething()
                    }
                    return CLOSURE
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_UntypedVariableInvokedAsAClosure() {
        final SOURCE = '''
            class MyClass {
                def myMethod(someClosure) {
                    def defaultClosure = someClosure
                    defaultClosure()            
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_UnusedClosureVariable() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def count = { println 'ok' }
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, "def count = { println 'ok' }", 'count')
    }

    @Test
    void testApplyTo_ExplicitMethodCallOnThisWithSameMethodName() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    int count = 23
                    this.count()
                }
                def count() { println 99 }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'int count = 23', 'count')
    }

    @Test
    void testApplyTo_ReferencedOnSameLineAsDeclaration() {
        final SOURCE = '''
            class MyClass {
                def doSomething() {
                    int count = 99; println 'abc'; println "count=$count"
                    for(int i=0; i<10; i++) { println 'me' }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_VariableOnlyReferencedWithinInnerClass() {
        final SOURCE = '''
            def buildCallable() {
                String ssn = 'xxx'
                return new Callable<Object>() {
                    public Object call(){
                        return participantDao.getParticipantPlans(ssn)
                    }
                }
            }
        '''
        assertNoViolations(SOURCE) 
    }

    @Test
    void testApplyTo_LoopVariable_ReferencedWithinLoopBlock() {
        final SOURCE = '''
            def doStuff() {
                for (int i = 0; i < 8; i++) {
                    outputMap.putAll(CopanSystemService.canisterInfo(suuid[i], i))
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_LoopVariable_NotReferenced() {
        final SOURCE = '''
            for (int i = 0; ; ) { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'for (int i = 0; ; )', messageText:'i'])
    }

    @Test
    void testApplyTo_ClassicForLoop_SameVariableName() {
        final SOURCE = '''
            for (int i=0;i<3;i++) {
                System.out.println("bla")
            }
            for (int i=0;i<3;i++) {
                System.out.println("bla")
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Script_UnusedVariable() {
        final SOURCE = '''
            BigDecimal depositAmount
        '''
        assertSingleViolation(SOURCE, 2, 'BigDecimal depositAmount', 'depositAmount')
    }

    @Test
    void testApplyTo_Script_UnusedBinding() {
        final SOURCE = '''
            depositCount = 99      // not considered a variable
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoVariableDefinition() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreVariableNames_MatchesSingleName() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    BigDecimal depositAmount
                }
            }
        '''
        rule.ignoreVariableNames = 'depositAmount'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreVariableNames_MatchNoNames() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    BigDecimal depositAmount
                }
            }
        '''
        rule.ignoreVariableNames = 'other'
        assertSingleViolation(SOURCE, 4, 'BigDecimal depositAmount', 'The variable [depositAmount] in class MyClass is not used')
    }

    @Test
    void testApplyTo_IgnoreVariableNames_MultipleNamesWithWildcards() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    BigDecimal depositAmount
                    def other
                    int count
                    long otherMax
                }
            }
        '''
        rule.ignoreVariableNames = 'oth*,xxx,deposit??ount'
        assertSingleViolation(SOURCE, 6, 'int count', 'The variable [count] in class MyClass is not used')
    }

    protected Rule createRule() {
        new UnusedVariableRule()
    }

}

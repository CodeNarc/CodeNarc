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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SpaceAfterCommaRule
 *
 * @author Chris Mair
  */
class SpaceAfterCommaRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SpaceAfterComma'
    }

    // Tests for method calls

    @Test
    void testApplyTo_MethodCall_ProperSpacing_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def value = calculate(1, 3, 'abc')
                def method1() {
                    doStuff()
                    doStuff([1], 2,\t3)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MethodCall_NoPrecedingSpaceForSingleParameter_Violation() {
        final SOURCE = '''
            class MyTestCase {
                def value = calculate(1,399, 'abc')
            }
        '''
        assertSingleViolation(SOURCE, 3, "def value = calculate(1,399, 'abc')", 'The parameter 399')
    }

    @Test
    void testApplyTo_MethodCall_NoPrecedingSpaceForMultipleParameters_Violation() {
        final SOURCE = '''
            class MyTestCase {
                def value = calculate(1,399,'abc',count)
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:"def value = calculate(1,399,'abc',count)", messageText:'The parameter 399'],
            [lineNumber:3, sourceLineText:"def value = calculate(1,399,'abc',count)", messageText:'The parameter abc'],
            [lineNumber:3, sourceLineText:"def value = calculate(1,399,'abc',count)", messageText:'The parameter count'] )
    }

    // Tests for method declarations

    @Test
    void testApplyTo_MethodDeclaration_ProperSpacing_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def method1() { }
                def method2(int a) { }
                def method3(String a, int b) { }
                def method4(String a, int b,\tObject c) { }
                def method5(a,  b,  c) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MethodDeclaration_NoPrecedingSpaceForSingleParameter_Violation() {
        final SOURCE = '''
            class MyClass {
                def method1(int a,String b) { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def method1(int a,String b) { }', 'The parameter b')
    }

    @Test
    void testApplyTo_MethodDeclaration_NoPrecedingSpaceForMultipleParameters_Violation() {
        final SOURCE = '''
            class MyTestCase {
                void calculate(a,int b,String name,count) { }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'void calculate(a,int b,String name,count) { }', messageText:'The parameter b'],
            [lineNumber:3, sourceLineText:'void calculate(a,int b,String name,count) { }', messageText:'The parameter name'],
            [lineNumber:3, sourceLineText:'void calculate(a,int b,String name,count) { }', messageText:'The parameter count'] )
    }

    // Tests for closure declarations

    @Test
    void testApplyTo_ClosureDeclaration_ProperSpacing_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def closure1 = { }
                def closure2 = { -> println 123 }
                def closure3 = { int a -> }
                def closure4 = { String a, int b ->  }
                def closure5 = { String a, int b,\tObject c -> }
                def closure6 = { a,  b,  c -> }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClosureDeclaration_NoPrecedingSpaceForSingleParameter_Violation() {
        final SOURCE = '''
            class MyClass {
                def closure1 = { int a,String b -> }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def closure1 = { int a,String b -> }', 'The closure parameter b')
    }

    @Test
    void testApplyTo_ClosureDeclaration_NoPrecedingSpaceForMultipleParameters_Violation() {
        final SOURCE = '''
            class MyClass {
                void calculate = { a,int b,String name,count -> }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'void calculate = { a,int b,String name,count -> }', messageText:'The closure parameter b'],
            [lineNumber:3, sourceLineText:'void calculate = { a,int b,String name,count -> }', messageText:'The closure parameter name'],
            [lineNumber:3, sourceLineText:'void calculate = { a,int b,String name,count -> }', messageText:'The closure parameter count'] )
    }

    protected Rule createRule() {
        new SpaceAfterCommaRule()
    }
}

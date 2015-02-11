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
        assert rule.priority == 3
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
    void testApplyTo_MethodCall_ChainedMethodCall_Violation() {
        final SOURCE = '''
            class MyTestCase {
                def value = Math.min(1,2).toString()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def value = Math.min(1,2).toString()', 'The parameter 2')
    }

    @Test
    void testApplyTo_ConstructorCall_Violation() {
        final SOURCE = '''
            Calendar c = new GregorianCalendar(2011,Calendar.NOVEMBER,1)
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'Calendar c = new GregorianCalendar(2011,Calendar.NOVEMBER,1)', messageText:'The parameter Calendar.NOVEMBER'],
            [lineNumber:2, sourceLineText:'Calendar c = new GregorianCalendar(2011,Calendar.NOVEMBER,1)', messageText:'The parameter 1'] )
    }

    @Test
    void testApplyTo_UnicodeLiteral_Violations() {
        final SOURCE = '''
            def value1 = calculate( { '\\u00A0' },12)
            def value2 = calculate('\\u00A0',399,'abc'       ,17)
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:"def value1 = calculate( { '\\u00A0' },12)", messageText:'The parameter 12'],
            [lineNumber:3, sourceLineText:"def value2 = calculate('\\u00A0',399,'abc'       ,17)", messageText:'The parameter 399'],
            [lineNumber:3, sourceLineText:"def value2 = calculate('\\u00A0',399,'abc'       ,17)", messageText:'The parameter abc'],
            [lineNumber:3, sourceLineText:"def value2 = calculate('\\u00A0',399,'abc'       ,17)", messageText:'The parameter 17'] )
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

    // Tests for constructor declarations

    @Test
    void testApplyTo_ConstructorDeclaration_NoPrecedingSpaceForMultipleParameters_Violation() {
        final SOURCE = '''
            class MyTestCase {
                MyTestCase(a,int b,String name,count) { }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'MyTestCase(a,int b,String name,count) { }', messageText:'The parameter b'],
            [lineNumber:3, sourceLineText:'MyTestCase(a,int b,String name,count) { }', messageText:'The parameter name'],
            [lineNumber:3, sourceLineText:'MyTestCase(a,int b,String name,count) { }', messageText:'The parameter count'] )
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

    // Tests for list literals

    @Test
    void testApplyTo_ListLiteral_ProperSpacing_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def list1 = []
                def list2 = [1]
                def list3 = [1, 2,\tx,    '123']
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ListLiteral_NoPrecedingSpaceForSingleParameter_Violation() {
        final SOURCE = '''
            def list1 = [a,b, c]
        '''
        assertSingleViolation(SOURCE, 2, 'def list1 = [a,b, c]', 'The list element b')
    }

    @Test
    void testApplyTo_ListLiteral_NoPrecedingSpaceForMultipleParameters_Violation() {
        final SOURCE = '''
            class MyClass {
                def list1 = [a,b,name,123,[x]]
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'def list1 = [a,b,name,123,[x]]', messageText:'The list element b'],
            [lineNumber:3, sourceLineText:'def list1 = [a,b,name,123,[x]]', messageText:'The list element name'],
            [lineNumber:3, sourceLineText:'def list1 = [a,b,name,123,[x]]', messageText:'The list element 123'],
            [lineNumber:3, sourceLineText:'def list1 = [a,b,name,123,[x]]', messageText:'The list element [x]'] )
    }

    // Tests for map literals

    @Test
    void testApplyTo_MapLiteral_ProperSpacing_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def map1 = [:]
                def map2 = [a:1]
                def map3 = [a:1, b:2,\tc:x,    d:'123']
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_MapLiteral_NoPrecedingSpaceForSingleParameter_Violation() {
        final SOURCE = '''
            def map1 = [a:1,b:2, c:3]
        '''
        assertSingleViolation(SOURCE, 2, 'def map1 = [a:1,b:2, c:3]', 'The map entry b:2')
    }

    @Test
    void testApplyTo_MapLiteral_NoPrecedingSpaceForMultipleParameters_Violation() {
        final SOURCE = '''
            class MyClass {
                def map1 = [a:1,b:value,c:'123',d:123,e:[x],f:[a:1]]
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:"def map1 = [a:1,b:value,c:'123',d:123,e:[x],f:[a:1]]", messageText:'The map entry b:value'],
            [lineNumber:3, sourceLineText:"def map1 = [a:1,b:value,c:'123',d:123,e:[x],f:[a:1]]", messageText:'The map entry c:123'],
            [lineNumber:3, sourceLineText:"def map1 = [a:1,b:value,c:'123',d:123,e:[x],f:[a:1]]", messageText:'The map entry d:123'],
            [lineNumber:3, sourceLineText:"def map1 = [a:1,b:value,c:'123',d:123,e:[x],f:[a:1]]", messageText:'The map entry e:[x]'],
            [lineNumber:3, sourceLineText:"def map1 = [a:1,b:value,c:'123',d:123,e:[x],f:[a:1]]", messageText:'The map entry f:[a:1]'] )
    }

    @Test
    void testApplyTo_ClosureParameterAfterParentheses_NoViolation() {
        final SOURCE = '''
            shouldFail(Exception){ dao.read() }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ClosureParameterAfterParentheses_InsideParentheses_Violation() {
        final SOURCE = '''
            shouldFail(Exception,{ dao.read() })
        '''
        assertSingleViolation(SOURCE, 2, 'shouldFail(Exception,{ dao.read() })', 'The parameter ' + SpaceAfterCommaRule.CLOSURE_TEXT)
    }

    protected Rule createRule() {
        new SpaceAfterCommaRule()
    }
}

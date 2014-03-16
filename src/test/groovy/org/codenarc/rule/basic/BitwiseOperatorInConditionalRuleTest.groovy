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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for BitwiseOperatorInConditionalRule
 *
 * @author Jeff Beck
 */
class BitwiseOperatorInConditionalRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BitwiseOperatorInConditional'
    }

    @Test
    void testTempBitwiseOr() {
        //(3 | 6) == 7   a bitwise or
        final SOURCE = '''
        	def temp = (3 | 6)
            if(temp==7) { return true}

        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testOr() {
        final SOURCE = '''
        	def a = false
            def b = true
            if(a||b) { return true}

        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTempWhileOr() {
        final SOURCE = '''
        	def temp = (3 | 6)
            while(temp==7) { return true}

        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testBitwiseOrViolation() {
        final SOURCE = '''
                def a = false
                def b = true
                if(a|b) { return true}
            '''
        assertSingleViolation(SOURCE, 4, 'if(a|b) { return true}', 'Use of a bitwise or (|) operator in a conditional')
    }

    @Test
    void testBitwiseOrViolationNested() {
        final SOURCE = '''
        	def a = false
            def b = true
            if(a|b||a) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'if(a|b||a) { return true}')
    }

    @Test
    void testBitwiseOrViolationNestedLeft() {
        final SOURCE = '''
            def a = false
            def b = true
            if((a|b)||(a||b)) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'if((a|b)||(a||b)) { return true}')
    }

    @Test
    void testBitwiseOrViolationNestedRight() {
        final SOURCE = '''
            def a = false
            def b = true
            if((a||b)||(a|b)) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'if((a||b)||(a|b)) { return true}')
    }

    @Test
    void testBitwiseOrViolationNestedRightDeep() {
        final SOURCE = '''
            def a = false
            def b = true
            if((a||b)||a && b || b || (a|b)) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'if((a||b)||a && b || b || (a|b)) { return true}')
    }

    @Test
    void testBitwiseOrViolationWhile() {
        final SOURCE = '''
            def a = false
            def b = true
            while(a|b) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'while(a|b) { return true}')
    }

    @Test
    void testBitwiseOrViolationTernary() {
        final SOURCE = '''
            def a = false
            def b = true
            a|b ?  true :  false
        '''
        assertSingleViolation(SOURCE, 4, 'a|b ?  true :  false', 'Use of a bitwise or (|) operator in a conditional')
    }

    @Test
    void testBitwiseOrViolationShortTernary() {
        final SOURCE = '''
            def a = false
            def b = true
            a|b ?:  false
        '''
        assertSingleViolation(SOURCE, 4, 'a|b ?:  false')
    }

    @Test
    void testTempBitwiseAnd() {
        //(3 & 6) == 2 a bitwise and
        final SOURCE = '''
        	def temp = (3 & 6)
            if(temp==2) { return true}

        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testBitwiseAndViolation() {
        final SOURCE = '''
        	def a = false
            def b = true
            if(a&b) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'if(a&b) { return true}', 'Use of a bitwise and (&) operator in a conditional')
    }

    @Test
    void testBitwiseAndViolationNested() {
        final SOURCE = '''
        	def a = false
            def b = true
            if(a&b||a) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'if(a&b||a) { return true}')
    }

    @Test
    void testBitwiseAndViolationNestedLeft() {
        final SOURCE = '''
            def a = false
            def b = true
            if((a&b)||(a||b)) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'if((a&b)||(a||b)) { return true}')
    }

    @Test
    void testBitwiseAndViolationNestedRight() {
        final SOURCE = '''
            def a = false
            def b = true
            if((a||b)||(a&b)) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'if((a||b)||(a&b)) { return true}')
    }

    @Test
    void testBitwiseAndViolationNestedRightDeep() {
        final SOURCE = '''
            def a = false
            def b = true
            if((a||b)||a && b || b || (a&b)) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'if((a||b)||a && b || b || (a&b)) { return true}')
    }

    @Test
    void testBitwiseAndViolationWhile() {
        final SOURCE = '''
            def a = false
            def b = true
            while(a&b) { return true}
        '''
        assertSingleViolation(SOURCE, 4, 'while(a&b) { return true}')
    }

    @Test
    void testBitwiseAndViolationTernary() {
        final SOURCE = '''
            def a = false
            def b = true
            a&b ?  true :  false
        '''
        assertSingleViolation(SOURCE, 4, 'a&b ?  true :  false')
    }

    @Test
    void testBitwiseAndViolationShortTernary() {
        final SOURCE = '''
            def a = false
            def b = true
            a&b ?:  false
        '''
        assertSingleViolation(SOURCE, 4, 'a&b ?:  false')
    }

    protected Rule createRule() {
        new BitwiseOperatorInConditionalRule()
    }
}

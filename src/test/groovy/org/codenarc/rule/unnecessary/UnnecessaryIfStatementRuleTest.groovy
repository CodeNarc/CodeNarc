/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for UnnecessaryIfStatementRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnnecessaryIfStatementRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryIfStatement'
    }

    void testApplyTo_ReturnTrueAndFalse_IsAViolation() {
        final SOURCE = '''
            if (expression1) return true else return false
            if (expression2) return Boolean.TRUE else return Boolean.FALSE
            if (expression3) return Boolean.TRUE else return false
            if (expression4) return true else return Boolean.FALSE
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (expression1)'],
            [lineNumber:3, sourceLineText:'if (expression2)'],
            [lineNumber:4, sourceLineText:'if (expression3)'],
            [lineNumber:5, sourceLineText:'if (expression4)'])
    }

    void testApplyTo_ReturnFalseAndTrue_IsAViolation() {
        final SOURCE = '''
            if (expression1) return false else return true
            if (expression2) return Boolean.FALSE else return Boolean.TRUE
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (expression1)'],
            [lineNumber:3, sourceLineText:'if (expression2)'] )
    }

    void testApplyTo_WithBraces_IsAViolation() {
        final SOURCE = '''
            if (expression1) { return true } else { return false }
            if (expression2) return Boolean.FALSE else { return Boolean.TRUE }
            if (expression3) { return false } else return true
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if (expression1)'],
            [lineNumber:3, sourceLineText:'if (expression2)'],
            [lineNumber:4, sourceLineText:'if (expression3)'])
    }

    void testApplyTo_ImplicitReturnAtEndOfMethod_TrueAndFalse_IsAViolation() {
        final SOURCE = '''
            def isSpellingCorrect(word) {
                File file = new File("...")
                def found = false
                file.eachLine {
                    if (it == word) found = true
                }
                if (found) { true } else false
                // if (found) true else false -- does not compile
            }
        '''
        assertSingleViolation(SOURCE, 8, 'if (found) { true } else false')
    }

    void testApplyTo_IfElseInMiddleOfBlock_TrueAndFalse_IsAViolation() {
        final SOURCE = '''
            def myClosure = {
                println 'initializing'
                if (ready) {
                    true
                } else false
                println 'other'
                if (count > 5) false; else true
                println 'done'
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'if (ready) {',
                8, 'if (count > 5) false; else true')
    }

    void testApplyTo_MultipleStatementBlocks_NotAViolation() {
        final SOURCE = '''
            if (expression1) { println 123; return true } else { return false }
            if (expression2) return Boolean.FALSE else { doSomething(); return Boolean.TRUE }
            if (expression3) {
                x = 98.6 
                return false
            } else return true
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_ReturnOtherValues_NotAViolation() {
        final SOURCE = '''
            if (someExpression) return 67 else return false
            if (someExpression) return true else return 88
            if (someExpression) return Boolean.TRUE else return "false"
            if (someExpression) return "true" else return Boolean.FALSE
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_ExpressionStatements_NotTrueAndFalse_NotAViolation() {
        final SOURCE = '''
            if (someExpression) 29; else 57
            if (someExpression) { 'abc' } else { 'xyz' }
            if (someExpression) 29; else false
            if (someExpression) true; else 57
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_MethodCalls_NotAViolation() {
        final SOURCE = '''
            if (someExpression) doStep1(); else doStep2()
            if (someExpression) { doStep1() } else { doStep2() }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NoElseBlock_NotAViolation() {
        final SOURCE = '''
            if (someExpression) return true
            if (someExpression) return true else { }

        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnnecessaryIfStatementRule()
    }
}